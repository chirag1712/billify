const sql = require("./db.js");

const Group = function (group) { this.group_name = group.group_name; };

const MemberOf = function (newMemberOf) {
    this.uid = newMemberOf.uid;
    this.gid = newMemberOf.gid;
};

Group.createGroup = newGroup => {
    return new Promise((resolve, reject) => {
        sql.query("INSERT INTO BillifyGroup SET ?", newGroup, (err, res) => {
            if (err) {
                console.log("error: ", err);
                reject(err);
            } else {
                console.log("group created: ", { group_id: res.insertId, ...newGroup });
                resolve(res.insertId);
            }
        });
    });
};

// TODO: add member of fields here only if don't exist
// can use: ON DUPLICATE KEY UPDATE uid=uid
MemberOf.addUsers = (listMemberOf) => {
    return new Promise((resolve, reject) => {
        sql.query("INSERT INTO MemberOf (uid, gid) VALUES ?", [listMemberOf], (err, res) => {
            if (err) {
                console.log("error: ", err);
                reject(err);
            } else {
                console.log("users added to group (uid, gid): ", listMemberOf);
                resolve(res);
            }
        });
    });
};

MemberOf.listGroupsForUser = (uid) => {
    return new Promise((resolve, reject) => {
        sql.query("SELECT g.gid, g.group_name FROM MemberOf mo, BillifyGroup g WHERE mo.uid = ? AND mo.gid = g.gid", uid, (err, res) => {
            if (err) {
                console.log("error: ", err);
                reject(err);
            } else {
                console.log("groups found: ", res);
                resolve(res);
            }
        });
    });
}

module.exports = { Group, MemberOf };
