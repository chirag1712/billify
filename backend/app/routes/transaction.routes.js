const express = require('express');
const router = express.Router();
const transaction = require("../controllers/transaction.controller.js");

// @route POST api/transactions/parse-receipt
router.post("/parse-receipt", transaction.parseReceipt);

// @route GET api/transactions/get-group-transactions/gid_value_here
router.get("/get-group-transactions/:gid", transaction.getGroupTransactions);

// @route GET api/transactions/get-transaction-items/tid_value_here
router.get("/get-transaction-items/:tid", transaction.getTransactionItems);

// @route GET api/transactions/get-user-transaction-details/:uid
router.get("/get-user-transaction-details/:uid", transaction.getUserTransactionDetails);

// @route POST api/transactions/update-user-transaction-labels/
router.post("/update-user-transaction-labels", transaction.updateUserTransactionLabels);

// @route GET api/transactions/transaction/:tid
router.get("/transaction/:tid", transaction.getTransaction);

// @route POST api/transactions/create-transaction
router.post("/create-transaction", transaction.createNewTransaction);

// @route GET api/transactions/price-shares/:tid
router.get("/price-shares/:tid", transaction.getPriceShares);

// @route POST api/transactions/price-shares/:tid
router.post("/settle", transaction.settlePriceShare);

// @route POST api/transactions/price-shares/:tid
router.post("/unsettle", transaction.unsettlePriceShare);

module.exports = router;
