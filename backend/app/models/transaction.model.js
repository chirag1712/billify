const sql = require("./db.js");

class Transaction {

    constructor(gid, receipt_img_data) {
        this.gid = gid;
        this.receipt_img = receipt_img_data;
        let currDateTime = new Date();
        this.t_date = currDateTime.toISOString().slice(0, 10);
        this.t_state = "NOT_STARTED";
    }

    createTransaction() {
        return new Promise((resolve, reject) => {
            sql.query("INSERT INTO Transaction SET ?", 
            this, (err, res) => {
                if (err) {
                    console.log("error: ", err);
                    reject(err);
                }
                console.log("Transaction entered: ", res);
                resolve(res["insertId"]);
            });
        });

    }

    getTransaction() {
        return new Promise((resolve, reject) => {
            sql.query("SELECT * FROM Transaction WHERE gid = ?",
            this.gid, (err, res) => {
                if (err) {
                    console.log("error: ", err);
                    reject(err);
                }
                console.log("Transaction entered: ", res);
                resolve(res);
            });
        });
    }
}

module.exports = Transaction;
