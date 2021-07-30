const express = require('express');
const router = express.Router();
const transaction = require("../controllers/transaction.controller.js");

// @route POST api/transactions/parse-receipt
router.post("/parse-receipt", transaction.parseReceipt);

// @route GET api/transactions/get-group-transactions/gid_value_here
router.get("/get-group-transactions/:gid", transaction.getGroupTransactions);

// @route GET api/transactions/get-transaction-items/tid_value_here
router.get("/get-transaction-items/:tid", transaction.getTransactionItems);

// @route GET api/transactions/transaction/:tid
router.get("/transaction/:tid", transaction.getTransaction);

// @route POST api/transactions/create-transaction
router.post("/create-transaction", transaction.createNewTransaction);

module.exports = router;
