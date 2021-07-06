const TransactionService = require("../services/transaction.service.js");

receiptParser = new TransactionService.ReceiptParser();

// parse-receipt endpoint
const parseReceipt = async (request, response) => {
    if (request.files["file"]) {
        try {
            let data = request.files["file"]["data"];
            let gid = request.body["gid"];
            let parsedReceiptJson = await receiptParser.parseReceiptData(data);
            let jsonResponse = await TransactionService.insertTransactionsAndItemsToDB(gid, data, parsedReceiptJson);
            return response.send(jsonResponse); 
        } catch (err) {
            return response.status(500).send({ error: "Internal error: Couldn't parse receipt: " + err});
        }
    } else {
        response.status(500).send({ error: "Error: Receipt file" });
    }
}

module.exports = { parseReceipt };
