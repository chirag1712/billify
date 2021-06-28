const aws = require("aws-sdk");
const awsCreds =  require("./credentials.json");
const fs = require("fs");

// TODO: Clean up and refactor code
aws.config.update({
    accessKeyId: awsCreds.AccessKeyId,
    secretAccessKey: awsCreds.SecretAccessKey,
    region: awsCreds.region
});

let filePath = "loblaw_receipt.png";
var data = fs.readFileSync(filePath);
const textract = new aws.Textract();

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

async function extractItemsFromReceipt() {
    try {
        textract.analyzeDocument(params, (err, data) => {
            if (err) {
                console.log(err, err.stack);
            } else {
                let blocks = data["Blocks"];
                var blockMap = {};
                let [numRows, numCols] = [0, 0];
                for (const block of blocks) {
                    blockMap[block.Id] = block;
                    if (block.BlockType === "CELL") {
                        // NOTE: Might not even need max if the cells are ordered by indices
                        numRows = Math.max(numRows, block.RowIndex);
                        numCols = Math.max(numCols, block.ColumnIndex);
                    }
                }

                let table = createTable(numRows, numCols);
                for (const block of blocks) {
                    if ((block.BlockType === "CELL"))  {
                        cellText = getCellText(block, blockMap);
                        const rowIndex = parseInt(block.RowIndex) - 1;
                        const colIndex = parseInt(block.ColumnIndex) - 1;
                        table[rowIndex][colIndex] = cellText;
                    }
                }
                console.log(table);
            }
        });

    } catch (error) {
    
    } finally {
    
    }
}

extractItemsFromReceipt();
