const { Group, MemberOf } = require("../models/group.model.js");
const User = require("../models/user.model.js");
const ReceiptParser = require("../services/receipt_parser.service.js");

receiptParser = new ReceiptParser();

// parse_receipt_endpoint
const parse_receipt = async (request, response) => {
    if (request.files["file"]) {
        try {
            let data = request.files["file"]["data"];
            let parsedReceiptJson = await receiptParser.parseReceiptData(data);
            return response.send(parsedReceiptJson); 
        } catch (err) {
            return response.status(500).send({ error: "Internal error: Couldn't parse receipt" });
        }
    } else {
        response.status(500).send({ error: "Error: Receipt file" });
    }
}

module.exports = { parse_receipt };
