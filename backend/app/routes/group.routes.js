const express = require('express');
const router = express.Router();
const group = require("../controllers/group.controller.js");

// @route  POST api/groups/create
router.post("/create", group.create);

// @route  GET api/groups/user/:uid
// route here for getting all groups for a user
// returns group names and gids for the user
// dealing with individual group - either not returning it or just letting it be there as a feature
router.get("/user/:uid", group.listUserGroups);

// @route  GET api/groups/:gid/user/:uid
// route for fetching details (like list of transactions etc) for a single group
// need uid to fetch labels for the user transactions

module.exports = router;
