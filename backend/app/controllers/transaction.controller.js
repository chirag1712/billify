const TransactionService = require("../services/transaction.service.js");

receiptParser = new TransactionService.ReceiptParser();

// parse-receipt endpoint
const parseReceipt = async (request, response) => {
    if ((request.files) && (request.files["file"])) {
        try {
            const data = request.files["file"]["data"];
            const gid = request.body["gid"];
            if (gid === undefined) {
                return response.status(500).send({error: "Error: gid (Group ID) has not been provided in POST request"})
            }
            const transaction_name = request.body["transaction_name"];
            const parsedReceiptJson = await receiptParser.parseReceiptData(data);
            const jsonResponse = await TransactionService.insertTransactionsAndItemsToDB(
                gid, 
                transaction_name,
                data, 
                parsedReceiptJson);
            console.log(jsonResponse);
            return response.send(jsonResponse); 
        } catch (err) {
            return response.status(500).send({ error: "Internal error: " + err});
        }
    } else {
        response.status(500).send({ error: "Error: Receipt file was not attached"});
    }
}

const getGroupTransactions = async (request, response) => {
    try {
        const gid = request.params.gid;
        const groupTransactionsJson = await TransactionService.getGroupTransactions(gid);
        return response.send(groupTransactionsJson);
    } catch (err) {
        return response.status(500).send({error: "Internal error: Couldn't get group transactions: " + err})
    }
}

const getTransactionItems = async (request, response) => {
    try {
        const tid = request.params.tid;
        const transactionItemsJson = await TransactionService.getTransactionItems(tid);
        return response.send(transactionItemsJson);
    } catch (err) {
        return response.status(500).send({error: "Internal error: Couldn't get transaction items: " + err})
    }
}

module.exports = { parseReceipt, getGroupTransactions, getTransactionItems};
