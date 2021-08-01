const TransactionModel = require("../models/transaction.model.js");
const TransactionService = require("../services/transaction.service.js");

receiptParser = new TransactionService.ReceiptParser();

// parse-receipt endpoint
const parseReceipt = async (request, response) => {
    if ((request.files) && (request.files["file"])) {
        try {
            const imgData = request.files["file"]["data"];
            const parsedReceiptJson = await receiptParser.parseReceiptData(imgData);
            console.log("Parsed receipt:");
            console.log(parsedReceiptJson);
            return response.send(parsedReceiptJson);
        } catch (err) {
            return response.status(500).send({ error: "Internal error: " + err});
        }
    } else {
        response.status(500).send({ error: "Error: Receipt file was not attached"});
    }
}

const createNewTransaction = async (request, response) => {
    try {
        if ((request.body) && (request.files)) {
            console.log(request.files);
            const parsedReceiptJsonString = request.body["transaction_details"];
            // NOTE: Sending JSON as string through mobile since we want to send image + JSON data in same request
            const parsedReceiptJson = JSON.parse(parsedReceiptJsonString); 

            const imgData = request.files["file"]["data"];
            const gid = parsedReceiptJson["gid"];
            const transactionName = parsedReceiptJson["transaction_name"];

            if (gid === undefined) {
                return response.status(500).send({error: "Error: gid (Group ID) has not been provided in POST request"});
            }
            if (transactionName === undefined) {
                return response.status(500).send({error: "Error: Transaction Name not provided"});
            }
            
            jsonResponse = await TransactionService.insertTransactionsAndItemsToDB(
                gid, 
                transactionName,
                imgData,
                request.files["file"].name,
                parsedReceiptJson
            );

            console.log("Final JSON response:");
            console.log(jsonResponse);
            
            return response.send(parsedReceiptJson);
        } else {
            const error = "Error: Creating new Transaction failed due to not having request.body or passing a file";
            console.log(error);
            return response.status(500).send({error});
        }
    } catch (err) {
        const error = "Error: Creating new Transaction failed due to: " + err;
        console.log(error);
        return response.status(500).send({error})
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

const getTransaction = async (request, response) => {
    try {
        const tid = request.params.tid;
        const items = await TransactionService.getTransactionItems(tid);
        const transaction = await TransactionModel.findTransaction(tid);

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

module.exports = { parseReceipt, getGroupTransactions, getTransactionItems, getTransaction, createNewTransaction};
