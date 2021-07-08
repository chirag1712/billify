const sql = require("./db.js");

class TransactionModel {

    constructor(gid) {
        if (gid !== undefined) {
            this.gid = gid;
        }
    }

    createTransaction(gid, transaction_name, receiptImgData) {
        this.gid = gid;
        this.receipt_img = receiptImgData;
        let currDateTime = new Date();
        this.t_date = currDateTime.toISOString().slice(0, 10);
        this.t_state = "NOT_STARTED";
        this.transaction_name = transaction_name;
        return new Promise((resolve, reject) => {
            sql.query("INSERT INTO Transaction SET ?", 
            this, (err, res) => {
                if (err) {
                    console.log("error: ", err);
                    reject(err);
                }
                resolve(res["insertId"]);
            });
        });

    }

    getTransactionsForGroup(gid) {
        if (gid !== undefined) {
            return new Promise((resolve, reject) => {
                sql.query("SELECT * FROM Transaction WHERE gid = ?",
                gid, (err, res) => {
                    if (err) {
                        console.log("error: ", err);
                        reject(err);
                    }
                    res = res.map(elem => {
                        delete elem["receipt_img"];
                        return elem;
                    })
                    resolve(res);
                });
            });
    } else {
        throw Error("gid is undefined");
    }
}

    getTransactionItems(tid) {
        if (tid !== undefined) {
            return new Promise((resolve, reject) => {
                sql.query("SELECT * FROM Item WHERE tid = ?",
                tid, (err, res) => {
                    if (err) {
                        console.log("error: ", err);
                        reject(err);
                    }
                    resolve(res);
                });
            });
        } else {
            throw Error("tid is undefined");
        }
    }
}

module.exports = TransactionModel;
