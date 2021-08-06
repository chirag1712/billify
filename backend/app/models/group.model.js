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

Group.getUsersForGroup = (gid) => {
    return new Promise((resolve, reject) => {
        sql.query(
            "SELECT u.uid, u.user_name, u.email FROM MemberOf mo, User u WHERE (mo.gid = ?) AND (mo.uid = u.uid)", 
            gid,
            (err, res) => {
                if (err) {
                    console.log("error: ", err);
                    reject(err);
                } else {
                    console.log("Users of group: ", res);
                    resolve(res);
                }
            });
    });
}

Group.getGroupDetails = (gid) => {
    return new Promise((resolve, reject) => {
        sql.query(
            "SELECT * FROM BillifyGroup WHERE gid = ?",
            gid,
            (err, res) => {
                if (err) {
                    console.log("error: ", err);
                    reject(err);
                } else {
                    resolve(res);
                }
            }
         )
    })
}

module.exports = { Group, MemberOf };
