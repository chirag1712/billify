const { Session, UserInfo } = require("../utils/session.js");
const { UserItem } = require("../models/item.model.js");
const Transaction = require("../models/transaction.model.js");

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
        client.on("startSession", async ({ uid: uid, username: username, tid: tid }) => {
            console.log("startSession from uid=", uid, ", tid=", tid);
            const isFirst = Session.userJoin(client.id, uid, username, tid);
            client.join(tid); // socket room identified by tid

            // optional todo: modify s.t. fetch from session when available
            var state;
            if (isFirst) {
                // fetch state from db
                try {
                    const itemId2userInfos = {};
                    const items = await Transaction.getTransactionItems(tid);
                    const fetchPromises = items.map(async (item) => {
                        const res = await UserItem.getUserInfoForItem(item.item_id);
                        const userInfos = [];
                        res.map((row) => {
                            userInfos.push(new UserInfo(row.uid, row.user_name));
                        });
                        itemId2userInfos[item.item_id] = userInfos;
                    });

                    // update current socket state
                    await Promise.all(fetchPromises);
                    state = Session.setState(tid, itemId2userInfos);
                } catch (err) {
                    console.log("Internal error: Couldn't fetch transaction state: " + err);
                }
            } else {
                // get current socket state
                state = Session.getState(tid);
            }
            // return state by emitting events to this client socket
            console.log(state);
        });

        // event listener: "selectItem"
        // (uid and item_id in json message), update socket state, and reemit for other users
        // of the room - current uids of the item_id
        client.on("selectItem", ({ uid: uid, username: username, tid: tid, item_id: item_id }) => {
            // add validation s.t. user not in session cant do this
            const obj = Session.userSelect(uid, username, tid, item_id);
            console.log("selected ", item_id, " userInfos: ", obj.userInfos);
            // emit {item_id, uids}
        });

        // event listener: "deselectItem"
        client.on("deselectItem", ({ uid: uid, username: username, tid: tid, item_id: item_id }) => {
            // add validation s.t. user not in session cant do this
            const obj = Session.userDeselect(uid, username, tid, item_id);
            console.log("deselected ", item_id, " userInfos: ", obj.userInfos);
            // emit {item_id, uids}
        });

        // event listener: "disconnect"
        // find which billify session (socket room) user was in
        // update counts for billify session
        // if last user to leave
        // persist changes to db
        client.on("disconnect", async (reason) => {
            console.log(reason);
            await Session.userLeave(client.id);
        });
    }
}

module.exports = new SocketHandler();
