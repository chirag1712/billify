const aws = require("aws-sdk");
const TransactionModel = require("../models/transaction.model.js");
const { Item } = require("../models/item.model.js");
const {Group, MemberOf} = require("../models/group.model.js");

aws.config.update({
    accessKeyId: process.env.AWS_AccessKeyId,
    secretAccessKey: process.env.AWS_SecretAccessKey,
    region: process.env.AWS_region
});

const s3 = new aws.S3();

// createTable and argmax are utility methods
function createTable(numRows, numCols) {
    // Creates and returns a 2D array of dimensions (numRows, numCols)
    let table = new Array(numRows);
    for (let i = 0; i < numRows; ++i) {
        table[i] = new Array(numCols);
        for (let j = 0; j < numCols; ++j) {
            table[i][j] = "";
        }
    }
    return table;
}

function argmax(arr) {
    // Returns the index of the max value in the array
    return arr.reduce((bestIdx, currVal, currIdx, arr) => currVal > arr[bestIdx] ? currIdx : bestIdx, 0);
}

class ReceiptParser {
    constructor() {

        this.textract = new aws.Textract();
        // Extending Array Proto by adding a none method
        // Source: https://stackoverflow.com/questions/62906597/is-there-an-equivalent-of-array-none-in-js
        Object.defineProperty(Array.prototype, 'none', {
            value: function (callback) { return !this.some(callback) }
        });
    }


    getCellText(blockCell, blockMap) {
        // Returns the text from the blockCell
        const relationships = blockCell.Relationships;
        if (!relationships) {
            return ""
        }
        // TODO: Check and confirm that a Cell has at most one relationship
        const relationship = relationships[0];
        if ((relationship) && (relationship.Type === "CHILD")) {
            let cellText = "";
            for (const id_val of relationship.Ids) {
                cellText += (blockMap[id_val].Text + " ");
            }
            return cellText.trim();
        } else {
            return "";
        }
    }

    extractRawItemsFromReceipt(params) {
        /*
        Extracts the raw items from a receipt using Textract.
        params refers to the parameters needed by textract.analyzeDocument
        Returns the  
        */
        return new Promise((resolve, reject) => {
            this.textract.analyzeDocument(params, (err, data) => {
                if (err) {
                    reject(err);
                } else {
                    let blocks = data["Blocks"];
                    var blockMap = {};
                    let [numRows, numCols] = [0, 0];

                    // Get the number of rows and columns
                    for (const block of blocks) {
                        blockMap[block.Id] = block;

                        /* 
                        Textract returns tabularized/structured data of receipt
                        If BlockType is CELL, then that CELL corresponds to a cell in the 
                        table extracted by Textract.
                        */
                        if (block.BlockType === "CELL") {
                            // NOTE: Might not even need max if the cells are ordered by indices
                            numRows = Math.max(numRows, block.RowIndex);
                            numCols = Math.max(numCols, block.ColumnIndex);
                        }
                    }

                    // itemsTable is a 2D array consisting the rows and columns extracted from Textract
                    let itemsTable = createTable(numRows, numCols);

                    // We fill itemsTable with the text of the corresponding cells
                    for (const block of blocks) {
                        if ((block.BlockType === "CELL")) {
                            let cellText = this.getCellText(block, blockMap);
                            const rowIdx = parseInt(block.RowIndex) - 1;
                            const colIdx = parseInt(block.ColumnIndex) - 1;
                            itemsTable[rowIdx][colIdx] = cellText;
                        } else if (block.BlockType == "LINE") {
                            // TODO: Optionally, can try to identify merchants using string matching
                        }
                    }
                    resolve(itemsTable);
                }
            });
        });
    }

    findItemNameColIdx(itemsTable) {
        let numNonPriceCols = itemsTable[0].length - 1;
        let numAlphabetsInCols = new Array();
        for (let colIdx = 0; colIdx < numNonPriceCols; ++colIdx) {
            let col = itemsTable.map(itemRow => itemRow[colIdx]);
            let numAlphabetsInCol = col.map(
                item => item.replace(/[0-9\. ]/g, "").length
            ).reduce((acc, curr) => acc + curr, 0);
            numAlphabetsInCols.push(numAlphabetsInCol);
        }
        return argmax(numAlphabetsInCols);
    }

    identifyItemPriceColIdx(itemsTable) {
        return itemsTable[0].length - 1;
    }

    processRawItemsTable(rawItemsTable) {
        /*
        This function takes in a rawItemsTable and processes it to return 
        an Object where keys are the item names and values are the prices.
    
        NOTE: Making assumption that last column will always be prices in receipts.
        */
        /*
        NOTE: Also assume price must have a decimal point,
        e.g. "432.10" is a price and "432" is not a price.
        TODO: Check above assumption is valid.
        */
        let filterNonPriceRows = itemRow => {
            let priceStr = itemRow[itemRow.length - 1];
            let processedPriceStrMatch = priceStr.match(/[\d]*[\.\,][\d]*/);
            return ((processedPriceStrMatch !== null) &&
                (processedPriceStrMatch[0] !== ".") &&
                (processedPriceStrMatch[0] !== ","));

        };

        // NOTE: filterEmptyColRows might not work when some cols are empty but still have prices
        // let filterEmptyColRows = itemRow => itemRow.none(item => item == "");

        let mapPriceStrToFloat = itemRow => {
            let priceStr = itemRow[itemRow.length - 1];
            // HACK: For European decimals with commas
            let processedPriceStr = priceStr.replace(",", ".");
            let parsedPriceFloat = parseFloat(processedPriceStr.match(/[\d]*\.[\d]*/)[0]);
            itemRow[itemRow.length - 1] = parsedPriceFloat;
            return itemRow;
        }

        let processedTable = rawItemsTable.filter(filterNonPriceRows).map(mapPriceStrToFloat);
        const itemNameColIdx = this.findItemNameColIdx(processedTable);
        const itemPriceColIdx = processedTable[0].length - 1;
        const processedItemsJson = processedTable.map((itemRow) => {
            let obj = {};
            obj["name"] = itemRow[itemNameColIdx];
            obj["price"] = itemRow[itemPriceColIdx];
            return obj;
        }, {});

        return processedItemsJson;
    }

    async parseReceiptData(data) {
        const params = {
            Document: {
                Bytes: data
            },
            FeatureTypes: ["TABLES"]
        };
        let rawItemsTable = await this.extractRawItemsFromReceipt(params);
        if (rawItemsTable.length === 0) {
            console.log("Couldn't parse receipt, no items found.");
            throw new Error("Couldn't parse receipt, no items found.");
        }
        const processed_items = this.processRawItemsTable(rawItemsTable);
        return processed_items;
    }

}

async function insertTransactionsAndItemsToDB(gid, transactionName, imgData, imgFileName, parsedReceiptJson) {
    try {
        parsedReceiptJson = await insertTransactionToDB(gid, transactionName, imgData, imgFileName, parsedReceiptJson);
    } catch(err) {
        console.log("Couldn't insert expense into DB");
        throw new Error("Couldn't insert expense into DB, could be that provided gid value was invalid");
    }
    parsedReceiptJson["items"] = await insertItemsToDB(parsedReceiptJson["tid"], parsedReceiptJson["items"]);
    return parsedReceiptJson;
}


function uploadReceiptImgToS3(params) {
    /*
    Uploads receipt image in params to S3
    */
    return new Promise((resolve, reject) => {
        s3.upload(params, (err, data) => {
            if (err) {
                console.log("Error uploading image to S3 bucket");
                reject(err);
            } else {
                if (data) {
                    resolve(data.Location);
                } else {
                    throw new Error("S3 didn't return data");
                }
            }
        });
    });
}

async function insertTransactionToDB(gid, transactionName, imgData, imgFileName, parsedReceiptJson) {
    const transactionService = new TransactionModel(gid);
    const imgFileExtension = imgFileName.split(".")[1];
    const groupDetails = await Group.getGroupDetails(gid);
    const groupName = groupDetails[0].group_name;
    let currDateTime = new Date();
    const currDateTimeStr = currDateTime.toISOString().slice(0, 16).split("T").join(" ");
    transactionName = transactionName ? transactionName : groupName + " " + currDateTimeStr;
    const params = {
        ACL: 'public-read',
        Bucket: 'billify',
        Body: imgData,
        Key: transactionName + "-" + Date.now().toString() +  "." + imgFileExtension
    }
    
    // NOTE: Goal is to add transaction during receipt parsing stage, but we only add items after
    // user edits items on their mobile app and confirms right set of items to add.
    const receiptImgS3URI = await uploadReceiptImgToS3(params);
    const transactionObj = await transactionService.createTransaction(gid, transactionName, receiptImgS3URI);

    parsedReceiptJson = {
        "items": parsedReceiptJson,
        "tid": transactionObj["tid"],
        "transaction_name": transactionObj["transaction_name"],
        "receipt_img": receiptImgS3URI
    };
    return parsedReceiptJson;
}

async function insertItemsToDB(tid, receiptItemsJson) {
    const createPromises = receiptItemsJson.map(async itemObject => {
        const itemName = itemObject["name"];
        const itemPrice = itemObject["price"];
        const item = new Item(tid, itemName, itemPrice);
        const insertedItemId = await item.insertItemToDB();
        itemObject["item_id"] = insertedItemId;
    });
    await Promise.all(createPromises);
    console.log("here:", receiptItemsJson);
    return receiptItemsJson;
}

async function getGroupTransactions(gid) {
    const transactionService = new TransactionModel(gid);
    const transactionJson = await transactionService.getTransactionsForGroup(gid);
    return transactionJson;
}

async function getTransactionItems(tid) {
    const transactionService = new TransactionModel();
    const transactionItemsJson = await transactionService.getTransactionItems(tid);
    return transactionItemsJson;
}

module.exports = {
    ReceiptParser,
    insertTransactionsAndItemsToDB,
    getGroupTransactions,
    getTransactionItems
};
