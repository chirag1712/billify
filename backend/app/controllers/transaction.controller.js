const TransactionModel = require("../models/transaction.model.js");
const TransactionService = require("../services/transaction.service.js");

receiptParser = new TransactionService.ReceiptParser();

// parse-receipt endpoint
const parseReceipt = async (request, response) => {
    if ((request.files) && (request.files["file"])) {
        try {
            // console.log(request.files["file"].name);
            const imgData = request.files["file"]["data"];
            const gid = request.body["gid"];
            if (gid === undefined) {
                return response.status(500).send({ error: "Error: gid (Group ID) has not been provided in POST request" })
            }
            const transactionName = request.body["transaction_name"];
            const parsedReceiptJson = await receiptParser.parseReceiptData(imgData);
            const jsonResponse = await TransactionService.insertTransactionsAndItemsToDB(
                gid,
                transactionName,
                imgData,
                request.files["file"].name,
                parsedReceiptJson);
            console.log(jsonResponse);
            return response.send(jsonResponse);
        } catch (err) {
            return response.status(500).send({ error: "Internal error: " + err });
        }
    } else {
        response.status(500).send({ error: "Error: Receipt file was not attached" });
    }
}

const getGroupTransactions = async (request, response) => {
    try {
        const gid = request.params.gid;
        const groupTransactionsJson = await TransactionService.getGroupTransactions(gid);
        return response.send(groupTransactionsJson);
    } catch (err) {
        return response.status(500).send({ error: "Internal error: Couldn't get group transactions: " + err })
    }
}

const getTransactionItems = async (request, response) => {
    try {
        const tid = request.params.tid;
        const transactionItemsJson = await TransactionService.getTransactionItems(tid);
        return response.send(transactionItemsJson);
    } catch (err) {
        return response.status(500).send({ error: "Internal error: Couldn't get transaction items: " + err })
    }
}

const getTransaction = async (request, response) => {
    try {
        const tid = request.params.tid;
        const items = await TransactionService.getTransactionItems(tid);
        const transaction = await TransactionModel.findTransaction(tid);

        console.log(transaction);
        console.log({
            items: items,
            tid: tid,
            transaction_name: transaction.transaction_name,
            receipt_img: transaction.receipt_img
        });

        return response.send({
            items: items,
            tid: tid,
            transaction_name: transaction.transaction_name,
            receipt_img: transaction.receipt_img
        });
    } catch (err) {
        return response.status(500).send({ error: "Internal error: Couldn't get transaction: " + err })
    }
}

module.exports = { parseReceipt, getGroupTransactions, getTransactionItems, getTransaction };
