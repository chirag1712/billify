const { Session, UserInfo } = require("../utils/session.js");

// handles socket events
class SocketHandler {
    constructor(io) {
        this.server = io;
    }

    connection(client) {
        // socket routes
        client.on("testConnection", (message) => {
            console.log(message);
        });

        // event listenser: "startSession"
        // takes (tid), joins room for that tid, update socket state accordingly
        // if first user, fetch from db those tid details (update session state) 
        // else fetch from current socket state of transaction
        client.on("startSession", async (startSessionRequest) => {
            await this.handleStartSession(client, startSessionRequest);
        });

        // event listener: "selectItem"
        // (uid and item_id in json message), update socket state, and reemit for other users
        // of the room - current uids of the item_id
        client.on("selectItem", (selectItemRequest) => {
            this.handleSelectItem(client, selectItemRequest);
        });

        // event listener: "deselectItem"
        client.on("deselectItem", (deselectItemRequest) => {
            this.handleDeselectItem(client, deselectItemRequest);
        });

        // event listener: "disconnect"
        // find which billify session (socket room) user was in
        // update counts for billify session
        // if last user to leave
        // persist changes to db
        client.on("disconnect", async (reason) => {
            await this.handleDisconnect(client, reason);
        });
    }

    // ==== IMPLEMENTATIONS of socket event handlers ====
    async handleStartSession(client, startSessionRequest) {
        const { uid: uid, username: username, tid: tid } = startSessionRequest;
        const userInfo = new UserInfo(uid, username);

        console.log("startSession from uid =", uid, ", tid =", tid);
        const state = await Session.userJoin(client.id, userInfo, tid);
        client.join(tid); // socket room identified by tid

        // return state by emitting events to this client socket
        console.log(state);
        this.server.to(client.id).emit("currentState", state);
    }

    handleSelectItem(client, selectItemRequest) {
        const { uid: uid, userName: username, tid: tid, item_id: item_id } = selectItemRequest;
        const userInfo = new UserInfo(uid, username);

        // TODO: add validation s.t. user not in session cant do this
        const obj = Session.userSelect(userInfo, tid, item_id);
        console.log("selected ", item_id, " userInfos: ", obj.userInfoObjs);
        console.log("new price shares", obj.priceShares);

        // broadcast to everyone in the room {item_id, userInfos}
        this.server.to(tid).emit("itemUpdated", { item_id: item_id, userInfos: obj.userInfoObjs, price_shares: obj.priceShares });
    }

    handleDeselectItem(client, deselectItemRequest) {
        const { uid: uid, userName: username, tid: tid, item_id: item_id } = deselectItemRequest;
        const userInfo = new UserInfo(uid, username);

        // TODO: add validation s.t. user not in session cant do this
        const obj = Session.userDeselect(userInfo, tid, item_id);
        console.log("deselected ", item_id, " userInfos: ", obj.userInfoObjs);
        console.log("new price shares", obj.priceShares);

        // broadcast to everyone in the room {item_id, userInfos}
        this.server.to(tid).emit("itemUpdated", { item_id: item_id, userInfos: obj.userInfoObjs, price_shares: obj.priceShares });
    }

    async handleDisconnect(client, reason) {
        console.log(reason);
        await Session.userLeave(client.id);
    }
}

module.exports = SocketHandler;
