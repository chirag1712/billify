const sql = require("./db.js");



class UserTransaction {

    constructor(tid, uid, label_id) {
        this.tid = tid;
        this.uid = uid;
        this.label_id = (label_id === undefined) ? 1 : label_id;
        this.settled = 0;
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
    static getUserTransactionInfosForTransaction(tid) {
        return new Promise((resolve, reject) => {
            sql.query("SELECT u.uid, u.user_name, ut.price_share FROM UserTransaction ut, User u WHERE u.uid = ut.uid and ut.tid = ?", tid, (err, res) => {
                if (err) {
                    console.log(err);
                    reject(err);
                } else {
                    console.log("users found for tid=", tid, ": ", res);
                    resolve(res);
                }
            });
        });
    }

    static updatePriceShare(tid, uid, new_price_share) {
        return new Promise((resolve, reject) => {
            sql.query("UPDATE UserTransaction SET price_share = ? where uid = ? AND tid = ?", [new_price_share, uid, tid], (err, res) => {
                if (err) {
                    console.log(err);
                    reject(err);
                } else {
                    console.log("update price share for tid =", tid, "uid =", uid, "to", new_price_share);
                    resolve(res);
                }
            });
        });
    }

    static getAllForTid(tid) {
        return new Promise((resolve, reject) => {
            sql.query("SELECT u.uid, u.user_name, ut.price_share, ut.settled FROM UserTransaction ut, User u WHERE u.uid = ut.uid and ut.tid = ?", tid, (err, res) => {
                if (err) {
                    console.log(err);
                    reject(err);
                } else {
                    console.log("price shares found for tid =", tid, res);
                    res.map((elem) => {
                        elem.settled = elem.settled[0];
                        return elem;
                    });
                    resolve(res);
                }
            });
        });
    }

}

module.exports = { UserTransaction };