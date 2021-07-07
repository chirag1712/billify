const express = require('express');
const router = express.Router();
const transaction = require("../controllers/transaction.controller.js");

// @route POST api/transactions/parse-receipt
// Post receipt as form data with key as "file"
router.post("/parse-receipt", transaction.parseReceipt);

<<<<<<< HEAD
// @route GET api/transactions/get-group-transactions/gid_value_here
router.get("/get-group-transactions/:gid", transaction.getGroupTransactions);

// @route GET api/transactions/get-transaction-items/tid_value_here
router.get("/get-transaction-items/:tid", transaction.getTransactionItems);
=======
// @route POST api/transactions/get-group-transactions
// Post gid in JSON: {"gid": 1} for example
router.post("/get-group-transactions", transaction.getGroupTransactions);

// @route POST api/transactions/get-transaction-items
// Post tid in JSON: {"tid": 1} for example
router.post("/get-transaction-items", transaction.getTransactionItems);
>>>>>>> Add getTransactionsForGroup and getTransactionItems endpoints

module.exports = router;
