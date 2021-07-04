const Group = require("../models/group.model.js");
const User = require("../models/user.model.js");

// create group
const create = async (request, response) => {
    const { user_id, name, emails } = request.body;

    try {
        // map all emails to user ids
        const user_ids = [user_id]
        User.findUsers(emails).then((users) => {
            users.forEach(user => {
                user_ids.push(user.uid)
            });
        });

        // create group and add user ids
        const newGroup = new Group({ group_name: name });
        const gid = await Group.createGroup(newGroup);
        await Group.addUsers(gid, user_ids);

        return response.send({ gid: gid });
    } catch (err) {
        return response.status(500).send({ error: "Internal error: create group" });
    }
}

module.exports = { create }
