const sql = require("./db.js");

class Item {

    constructor(tid, name, price) {
        this.tid = tid;
        this.name = name;
        this.price = price;
    }

    insertItemToDB() {
        return new Promise((resolve, reject) => {
            sql.query("INSERT INTO Item SET ?", 
            this, (err, res) => {
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

module.exports = Item;
