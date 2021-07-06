const sql = require("./db.js");

const User = function (user) {
    this.user_name = user.user_name;
    this.email = user.email;
    this.hashed_password = user.hashed_password;
};

User.createUser = newUser => {
    return new Promise((resolve, reject) => {
        sql.query("INSERT INTO User SET ?", newUser, (err, res) => {
            if (err) {
                console.log("error: ", err);
                reject(err);
            }
            console.log("user created: ", { user_id: res.insertId, ...newUser });
            resolve(res.insertId);
        });
    });
}

User.findUser = email => {
    return new Promise((resolve, reject) => {
        sql.query("SELECT * FROM User WHERE email = ?", email, (err, res) => {
            if (err) {
                console.log("error: ", err);
                reject(err);
            }
            console.log("user found: ", res);
            resolve(res[0]);
        });
    });
}

User.findUsers = emails => {
    const searchEmails = "(\"" + emails.join("\",\"") + "\")";
    
    return new Promise((resolve, reject) => {
        sql.query(`SELECT * FROM User WHERE email IN ${searchEmails}`, (err, res) => {
            if (err) {
                console.log("error: ", err);
                reject(err);
            }
            resolve(res);
        });
    });
}

module.exports = User;
