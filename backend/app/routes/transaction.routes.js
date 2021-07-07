const express = require('express');
const router = express.Router();
const transaction = require("../controllers/transaction.controller.js");

// @route POST api/transactions/parse-receipt
// Post receipt as form data with key as "file"
router.post("/parse-receipt", transaction.parseReceipt);

// @route GET api/transactions/get-group-transactions/gid_value_here
router.get("/get-group-transactions/:gid", transaction.getGroupTransactions);

// @route GET api/transactions/get-transaction-items/tid_value_here
router.get("/get-transaction-items/:tid", transaction.getTransactionItems);

module.exports = router;
