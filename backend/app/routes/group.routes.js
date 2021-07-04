const express = require('express');
const router = express.Router();
const group = require("../controllers/group.controller.js");

// @route  POST api/groups/create
router.post("/create", group.create);

module.exports = router;
