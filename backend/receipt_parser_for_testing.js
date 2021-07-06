const aws = require("aws-sdk");
const awsCreds =  require("./aws_credentials.json");
const fs = require("fs");
const util = require("util");
const sampleReceiptPaths = require("./sample_receipt_paths.json");

// TODO: Clean up, refactor code and also wrap exception-prone code within try-catch blocks
aws.config.update({
    accessKeyId: process.env.AWS_AccessKeyId,
    secretAccessKey: process.env.AWS_SecretAccessKey,
    region: process.env.AWS_region
});

const textract = new aws.Textract();

// Extending Array Proto by adding a none method
// Source: https://stackoverflow.com/questions/62906597/is-there-an-equivalent-of-array-none-in-js
Object.defineProperty(Array.prototype, 'none', {
    value: function (callback) { return !this.some(callback) }
});

// createTable and argmax are utility functions
function createTable(numRows, numCols) {
    // Creates and returns a 2D array of dimensions (numRows, numCols)
    let table = new Array(numRows);
    for (i=0; i<numRows; ++i) {
        table[i] = new Array(numCols);
        for (j=0; j<numCols; ++j) {
            table[i][j] = "";
        }
    }
    return table;
}

function argmax(arr) {
    // Returns the index of the max value in the array
    return arr.reduce((bestIdx, currVal, currIdx, arr) => currVal > arr[bestIdx] ? currIdx : bestIdx, 0);
}

function getCellText(blockCell, blockMap) {
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


function extractRawItemsFromReceipt(params) {
    /*
    Extracts the raw items from a receipt using Textract.
    params refers to the parameters needed by textract.analyzeDocument
    Returns the  
    */
    return new Promise((resolve, reject) => {
        textract.analyzeDocument(params, (err, data) => {
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
                    if ((block.BlockType === "CELL"))  {
                        cellText = getCellText(block, blockMap);
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


function findItemNameColIdx(itemsTable) {
    numNonPriceCols = itemsTable[0].length - 1;
    numAlphabetsInCols = new Array();
    for (let colIdx=0; colIdx < numNonPriceCols; ++colIdx) {
        let col = itemsTable.map(itemRow => itemRow[colIdx]);
        let numAlphabetsInCol = col.map(
            item => item.replaceAll(/[0-9\. ]/g, "").length
            ).reduce((acc, curr) => acc + curr, 0);
            numAlphabetsInCols.push(numAlphabetsInCol);
    }
    return argmax(numAlphabetsInCols);
}

function identifyItemPriceColIdx(itemsTable) {
    return itemsTable[0].length - 1;
}

function processRawItemsTable(rawItemsTable) {
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
        let priceStr = itemRow[itemRow.length-1];
        let processedPriceStrMatch = priceStr.match(/[\d]*[\.\,][\d]*/);
        return ((processedPriceStrMatch !== null) &&
        (processedPriceStrMatch[0] !== ".") &&
        (processedPriceStrMatch[0] !== ","));
    
    };

    // NOTE: filterEmptyColRows might not work when some cols are empty but still have prices
    // let filterEmptyColRows = itemRow => itemRow.none(item => item == "");
    
    let mapPriceStrToFloat = itemRow => {
        let priceStr = itemRow[itemRow.length-1];
        // HACK: For European decimals with commas
        processedPriceStr = priceStr.replaceAll(",", "."); 
        let parsedPriceFloat = parseFloat(processedPriceStr.match(/[\d]*\.[\d]*/)[0]);
        itemRow[itemRow.length-1] = parsedPriceFloat;
        return itemRow;
    }

    let processedTable = rawItemsTable.filter(filterNonPriceRows).map(mapPriceStrToFloat);
    let itemNameColIdx = findItemNameColIdx(processedTable);
    let itemPriceColIdx = processedTable[0].length - 1;
    // let processed_items = processedTable.reduce((obj, itemRow) => {
    //         obj[itemRow[itemNameColIdx]] = itemRow[itemPriceColIdx];
    //         return obj;
    //     }, {});
    let processed_items = processedTable.map((itemRow) => {
        obj = {};
        obj[itemRow[itemNameColIdx]] = itemRow[itemPriceColIdx];
        return obj;
    }, {});

    return processed_items;
}


function main() {

    let receiptPaths = sampleReceiptPaths["filePaths"];
    receiptPaths.forEach(async filePath => {
        var data = fs.readFileSync(filePath);
        const params = {
            Document: {
                Bytes: data
            },
            FeatureTypes: ["TABLES"]
        };    
        let rawItemsTable = await extractRawItemsFromReceipt(params);
        console.log(rawItemsTable);
        let processed_items = processRawItemsTable(rawItemsTable);
        console.log(processed_items);    
    });
}

const parse_receipt_data = async(data) => {
    const params = {
        Document: {
            Bytes: data
        },
        FeatureTypes: ["TABLES"]
    };    
    let rawItemsTable = await extractRawItemsFromReceipt(params);
    console.log(rawItemsTable);
    let processed_items = processRawItemsTable(rawItemsTable);
    console.log(processed_items);    
    return processed_items;
}

module.exports = {parse_receipt_data, extractRawItemsFromReceipt, processRawItemsTable};
