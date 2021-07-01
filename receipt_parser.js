const aws = require("aws-sdk");
const awsCreds =  require("./credentials.json");
const fs = require("fs");
const util = require("util");

// TODO: Clean up and refactor code
aws.config.update({
    accessKeyId: awsCreds.AccessKeyId,
    secretAccessKey: awsCreds.SecretAccessKey,
    region: awsCreds.region
});

let filePath = "loblaw_receipt.png";
const textract = new aws.Textract();
var data = fs.readFileSync(filePath);

const params = {
    Document: {
        Bytes: data
    },
    FeatureTypes: ["TABLES"]
};

function getCellText(blockCell, blockMap) {
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

function createTable(numRows, numCols) {
    
    let table = new Array(numRows);
    for (i=0; i<numRows; ++i) {
        table[i] = new Array(numCols);
        for (j=0; j<numCols; ++j) {
            table[i][j] = "";
        }
    }
    return table;
}

function extractRawItemsFromReceipt() {
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
                        const rowIndex = parseInt(block.RowIndex) - 1;
                        const colIndex = parseInt(block.ColumnIndex) - 1;
                        itemsTable[rowIndex][colIndex] = cellText;
                    }
                }
                resolve(itemsTable);
            }
        });
    });
}

async function main() {
    let rawItemsTable = await extractRawItemsFromReceipt();
    console.log(rawItemsTable);
}

main();