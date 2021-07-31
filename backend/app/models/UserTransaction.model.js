const sql = require("./db.js");



class UserTransaction {
    
    constructor(tid, uid) {
        if ((tid !== undefined) && (uid !== undefined)) {
            this.tid = tid;
            this.uid = uid;    
        }
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

}

module.exports = {UserTransaction};