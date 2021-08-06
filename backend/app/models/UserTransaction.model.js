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

}

module.exports = {UserTransaction};