const bcrypt = require('bcrypt');
const User = require("../models/user.model.js");
const { check, validationResult } = require("express-validator");
const { Group, MemberOf } = require('../models/group.model.js');

const signupValidation = [
    check("email", "Not a valid email").isEmail(),
    check("password", "Password is required").not().isEmpty(),
];

// common validation function for all endpoints
function validate(request, response) {
    const errors = validationResult(request);
    if (!errors.isEmpty()) {
        let errorArray = errors.array().map(e => e.msg);
        return response.status(400).json({ error: errorArray[0] });
    }
}

// user signup
const signup = async (request, response) => {
    validate(request, response);
    const { email, password, user_name } = request.body;

    try {
        const existing_user = await User.findUser(email);
        if (existing_user) {
            return response.status(400).json({ error: "User already exists" });
        }

        // encrypt password and create user
        const salt = await bcrypt.genSalt(10);
        const hashed_password = await bcrypt.hash(password, salt);
        const user = new User({
            email: email,
            user_name: user_name,
            hashed_password: hashed_password
        });

        // signup
        const uid = await User.createUser(user);

        // on signup, users are added to a group with a single member 
        // to manage their individual transactions
        const gid = await Group.createGroup({ group_name: user_name + "'s personal group" })
        const newMemberOf = new MemberOf({ uid, gid });
        await MemberOf.addUsers([Object.values(newMemberOf)]);

        return response.send({ id: uid, userName: user_name });
    } catch (err) {
        console.log(err);
        return response.status(500).send({ error: "Internal error: signup" });
    }
}

const loginValidation = [
    check("email", "Not a valid email").isEmail(),
    check("password", "Password is required").not().isEmpty(),
];

// user login
const login = async (request, response) => {
    validate(request, response);
    const { email, password } = request.body;

    try {
        const user = await User.findUser(email);
        if (!user) {
            return response.status(401).json({ error: "User does not exist" });
        }

        // compare passwords
        const match = await bcrypt.compare(password, user.hashed_password);
        if (!match) {
            return response.status(401).json({ error: "Password is incorrect" });
        }

        return response.status(200).json({ id: user.uid, userName: user.user_name });
    } catch (err) {
        return response.status(500).send({ error: "Internal error: login" });
    }
}

module.exports = {
    signupValidation,
    loginValidation,
    signup,
    login
}
