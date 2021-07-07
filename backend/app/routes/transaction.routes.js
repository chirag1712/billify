const express = require('express');
const router = express.Router();
const transaction = require("../controllers/transaction.controller.js");

// @route POST api/transactions/parse-receipt
// Post receipt as form data with key as "file"
router.post("/parse-receipt", transaction.parseReceipt);

// @route POST api/transactions/get-group-transactions
// Post gid in JSON: {"gid": 1} for example
router.post("/get-group-transactions", transaction.getGroupTransactions);

// @route POST api/transactions/get-transaction-items
// Post tid in JSON: {"tid": 1} for example
router.post("/get-transaction-items", transaction.getTransactionItems);

module.exports = router;
