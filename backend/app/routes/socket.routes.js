const User = require("../utils/users.js");

// handles socket events
class SocketHandler {
    connection(client) {
        // socket routes
        client.on("testConnection", (message) => {
            console.log(message);
        });

        // event listenser: "startSession"
        // takes (tid), joins room for that tid, update socket state accordingly
        // if first user, fetch from db those tid details (update session state) 
        // else fetch from current socket state of transaction
        client.on("startSession", ({uid, tid}) => {
            console.log(uid, tid);
            // User.userJoin(client.id, uid, tid);
        });

        // event listener: "selectItem"
        // (uid and item_id in json message), update socket state, and reemit for other users
        // of the room - current uids of the item_id
        client.on("selectItem", ({uid, item_id, tid}) => {
            console.log(uid, item_id, tid);
            // User.userSelect(item_id, uid);
        });

        // event listener: "deselectItem"
        client.on("deselectItem", ({uid, item_id, tid}) => {
            console.log(uid, item_id, tid);
            // User.userDeselect(item_id, uid);
        });

        // event listener: "disconnect"
        // find which billify session (socket room) user was in
        // update counts for billify session
        // if last user to leave
        // persist changes to db
        client.on("disconnect", (reason) => {
            console.log(reason);
            // User.userLeave(client.id);
        });
    }
}

module.exports = new SocketHandler();
