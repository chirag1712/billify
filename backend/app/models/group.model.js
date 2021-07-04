const sql = require("./db.js");

const Group = function (group) { this.group_name = group.group_name; };

Group.createGroup = newGroup => {
    return new Promise((resolve, reject) => {
        sql.query("INSERT INTO BillifyGroup SET ?", newGroup, (err, res) => {
            if (err) {
                console.log("error: ", err);
                reject(err);
            }
            console.log("group created: ", { group_id: res.insertId, ...newGroup });
            resolve(res.insertId);
        });
    });
};

Group.addUsers = (groupId, userIds) => {
    // add records for uid - gid relation
};

module.exports = Group;
