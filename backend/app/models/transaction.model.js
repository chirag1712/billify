const sql = require("./db.js");
const {Group, MemberOf} = require("./group.model.js");
class TransactionModel {

    constructor(gid) {
        if (gid !== undefined) {
            this.gid = gid;
        }
    }

    async createTransaction(gid, transaction_name, receiptImgData) {
        this.gid = gid;
        this.receipt_img = receiptImgData;
        let currDateTime = new Date();
        this.t_date = currDateTime.toISOString().slice(0, 10);
        const currDateTimeStr = currDateTime.toISOString().slice(0, 16).split("T").join(" ");
        this.t_state = "NOT_STARTED";
        const groupDetails = await Group.getGroupDetails(gid);
        const groupName = groupDetails[0].group_name;
        // TODO: Get better default transaction Name
        // TODO: Default Transaction name is the String concatenation of group name and DateTime.
        this.transaction_name = transaction_name ? transaction_name : groupName + " " + currDateTimeStr;
        console.log(this.transaction_name);
        return new Promise((resolve, reject) => {
            sql.query("INSERT INTO Transaction SET ?", 
            this, (err, res) => {
                if (err) {
                    console.log("error: ", err);
                    reject(err);
                }
                resolve(
                    {
                        "transaction_name": this.transaction_name, 
                        "tid": res["insertId"]
                    });
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

    static getTransactionItems(tid) {
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
