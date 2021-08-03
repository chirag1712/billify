const sql = require("./db.js");



class UserTransaction {
    
    constructor(tid, uid, label_id) {
        this.tid = tid;
        this.uid = uid;
        this.label_id = (label_id === undefined) ? 1 : label_id;
    }

    async createUserTransaction() {
        /*
        Enters a row into UserTransaction table
        */
        return new Promise((resolve, reject) => {
            sql.query("INSERT INTO UserTransaction SET ?",
            this, (err, res) => {
                if (err) {
                    console.log("error: ", err);
                    reject(err);
                }
                resolve(res);
            });
        });
    }

     // returns user information for users who have selected this item
     static getUserInfosForTransaction(tid) {
        return new Promise((resolve, reject) => {
            sql.query("SELECT u.uid, u.user_name FROM UserTransaction ut, User u WHERE u.uid = ut.uid and ut.tid = ?", tid, (err, res) => {
                if (err) {
                    console.log(err);
                    reject(err);
                } else {
                    console.log("users found for tid=", tid, ": ", res);
                    resolve(res);
                }
            });
        })
    }

}

module.exports = {UserTransaction};