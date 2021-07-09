const { Group, MemberOf } = require("../models/group.model.js");
const User = require("../models/user.model.js");

// create group
const create = async (request, response) => {
    const { user_id, name, emails } = request.body;

    try {
        // map all emails to user ids
        const user_ids = [user_id];
        User.findUsers(emails).then((users) => {
            users.forEach(user => {
                user_ids.push(user.uid)
            });
        });

        // create group and add user ids
        const newGroup = new Group({ group_name: name });
        const gid = await Group.createGroup(newGroup);

        // construct memberof fields here
        const newMemberOfs = [];
        user_ids.map((uid) => {
            const obj = new MemberOf({ uid, gid });
            newMemberOfs.push(Object.values(obj));    
        });
        await MemberOf.addUsers(newMemberOfs);
        
        return response.send({ gid: gid });
    } catch (err) {
        return response.status(500).send({ error: "Internal error: create group" });
    }
}

// need a function to add users to existing group -> can use the model's add users function
// validation -> user shouldn't already exist

const listUserGroups = async (request, response) => {
    const uid = request.params.uid;
    try {
        const listGroups = await MemberOf.listGroupsForUser(uid);
        return response.send({ groups: listGroups });
    } catch (err) {
        return response.status(500).send({ error: "Internal error: list groups" });
    }
}

const getUsersOfGroup = async (request, response) => {
    const gid = request.params.gid;
    try {
        const UsersData = await Group.getUsersForGroup(gid);
        return response.send({users: UsersData});
    } catch (err) {
        return response.status(500).send({ error: "Internal error: : Failed getting users of the group" + err});
    }
}

module.exports = { create, listUserGroups, getUsersOfGroup};
