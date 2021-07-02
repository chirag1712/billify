const express = require('express');
const router = express.Router();
const users = require("../controllers/user.controller.js");

// @route  POST api/users/login
router.post("/signup", users.signupValidation, users.signup);

// @route  POST api/users/login
router.post("/login", users.loginValidation, users.login);

module.exports = router;
