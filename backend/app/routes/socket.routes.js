const Session = require("../utils/session.js");
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
        client.on("startSession", async ({ uid, tid }) => {
            console.log("startSession from uid=", uid, ", tid=", tid);
            const isFirst = Session.userJoin(client.id, uid, tid);
            client.join(tid); // socket room identified by tid

            var state;
            if (isFirst) {
                // fetch state from db
                try {
                    const itemId2uids = {};
                    const items = await Transaction.getTransactionItems(tid);
                    items.map(async (item) => {
                        const uids = await UserItem.getUidsForItem(item.item_id);
                        itemId2uids[item.item_id] = uids;
                    });

                    // update current socket state
                    state = Session.setState(tid, itemId2uids);
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
        client.on("selectItem", ({ uid, tid, item_id }) => {
            console.log(uid, tid, item_id);
            const obj = Session.userSelect(tid, item_id, uid);
            console.log("selected. for ", item_id, " uids: ", obj.uids);
            // emit {item_id, uids}
        });

        // event listener: "deselectItem"
        client.on("deselectItem", ({ uid, item_id, tid }) => {
            console.log(uid, item_id, tid);
            const obj = Session.userDeselect(item_id, uid);
            console.log("deselected. for ", item_id, " uids: ", obj.uids);
            // emit {item_id, uids}
        });

        // event listener: "disconnect"
        // find which billify session (socket room) user was in
        // update counts for billify session
        // if last user to leave
        // persist changes to db
        client.on("disconnect", (reason) => {
            console.log(reason);
            Session.userLeave(client.id);
        });
    }
}

module.exports = new SocketHandler();
