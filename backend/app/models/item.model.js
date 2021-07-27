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
                    console.log(err);
                    reject(err);
                } else {
                    resolve(res);
                }
            });
        })
    }

}

class UserItem {

    constructor(tid, uid, item_id) {
        this.tid = tid;
        this.uid = uid;
        this.item_id = item_id;
    }

    createUserItem() {
        return new Promise((resolve, reject) => {
            sql.query("INSERT INTO UserItem SET ?", this, (err, res) => {
                if (err) {
                    console.log("error: ", err);
                    reject(err);
                }
                console.log("UserItem added to db: ", res);
                resolve(res["insertId"]);
            });
        });
    }

    // returns user information for users who have selected this item
    static getUserInfoForItem(itemId) {
        return new Promise((resolve, reject) => {
            sql.query("SELECT u.uid, u.user_name FROM UserItem ui, User u WHERE u.uid = ui.uid and ui.item_id = ?", itemId, (err, res) => {
                if (err) {
                    console.log(err);
                    reject(err);
                } else {
                    console.log("users found for itemId=", itemId, ": ", res);
                    resolve(res);
                }
            });
        })
    }

    static deleteAll(tid) {
        return new Promise((resolve, reject) => {
            sql.query("DELETE from UserItem WHERE tid = ?", tid, (err, res) => {
                if (err) {
                    console.log(err);
                    reject(err);
                } else {
                    console.log("deleted all user items for tid = ", tid);
                    resolve(res);
                }
            });
        });
    }
}

module.exports = { Item, UserItem };
