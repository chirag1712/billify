const sql = require("./db.js");

class Item {

    constructor(tid, name, price) {
        this.tid = tid;
        this.name = name;
        this.price = price;
    }

    insertItemToDB() {
        return new Promise((resolve, reject) => {
            sql.query("INSERT INTO Item SET ?", this, (err, res) => {
                if (err) {
                    console.log("error: ", err);
                    reject(err);
                }
                // console.log("Item entered: ", res);
                resolve(res["insertId"]);
            });
        });
    }
}

class UserItem {

    static getUidsForItem(itemId) {
        return new Promise((resolve, reject) => {
            sql.query("SELECT uid FROM UserItem where item_id = ?", itemId, (err, res) => {
                if (err) {
                    console.log(err);
                    reject(err);
                } else {
                    console.log("uids found for itemId=", itemId, ": ", res);
                    resolve(res);
                }
            });
        })
    }

}

module.exports = { Item, UserItem };
