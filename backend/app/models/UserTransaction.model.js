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

    static settle(uid, tid) {
        return new Promise((resolve, reject) => {
            sql.query("UPDATE UserTransaction SET settled = 1 where tid = ? AND uid = ?", [tid, uid], (err, res) => {
                if (err) {
                    console.log(err);
                    reject(err);
                } else {
                    console.log("settled transaction uid =", uid, "tid =", tid);
                    resolve(res);
                }
            });
        });
    }

    static getUserTransaction(uid) {
        if (uid !== undefined) {
            return new Promise((resolve, reject) => {
                sql.query("Select * From UserTransaction Where uid = ?",
                    uid, (err, res) => {
                        if (err) {
                            console.log("error: ", err);
                            reject(err);
                        }
                        console.log('Accessing usertransaction for the user: ', uid, res);
                        resolve(res);
                    });
            });
        } else {
            throw Error("uid is undefined");
        }
    }

    static getUserTransactionDetails(uid) {
        if (uid !== undefined) {
            return new Promise((resolve, reject) => {
                console.log('checkpoint 1');
                let sqlQuery = "Select ut.tid as tid, ut.uid as uid, t.transaction_name as transaction_name, ut.price_share as price_share, l.label_id as label_id, l.label_name as label_name, l.label_color as label_color " +
                                "From UserTransaction as ut " +
                                "Inner Join Transaction as t ON ut.tid = t.tid " +
                                "Inner Join Label as l on ut.label_id = l.label_id " +
                                "Where ut.uid = ?";
                sql.query(sqlQuery,
                    uid, (err, res) => {
                        if (err) {
                            console.log("error: ", err);
                            reject(err);
                        }
                        console.log('Accessing transactions for the user: ', uid, res);
                        resolve(res);
                    });
            });
        } else {
            throw Error("uid is undefined");
        }
    }

    static updateUserTransactionLabel(uid, tid, label_name) {
        if (uid !== undefined && tid !== undefined) {
            return new Promise((resolve, reject) => {
                let sqlQuery = "Update UserTransaction set label_id = " +
                                "(Select label_id From Label Where label_name LIKE ? )" +
                                "Where uid = ? And tid = ?";
                sql.query(sqlQuery,
                    label_name, uid, tid, (err, res) => {
                        if (err) {
                            console.log("error: ", err);
                            reject(err);
                        }
                        console.log('Updating user transaction label: ', uid, tid, res);
                        resolve(res);
                    });
            });
        } else {
            throw Error("uid or tid is undefined");
        }
    }

    static unsettle(uid, tid) {
        return new Promise((resolve, reject) => {
            sql.query("UPDATE UserTransaction SET settled = 0 where tid = ? AND uid = ?", [tid, uid], (err, res) => {
                if (err) {
                    console.log(err);
                    reject(err);
                } else {
                    console.log("settled transaction uid =", uid, "tid =", tid);
                    resolve(res);
                }
            });
        });
    }
}

module.exports = { UserTransaction };