const express = require('express');
const router = express.Router();
const transaction = require("../controllers/transaction.controller.js");

// @route POST api/transactions/parse-receipt
router.post("/parse-receipt", transaction.parse_receipt);

module.exports = router;
