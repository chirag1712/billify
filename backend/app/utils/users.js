class User {
    constructor() {
        // TRANSACTION STATE
        // stores itemid to list of users - state of transaction items
        this.itemId2uid = {};

        // keeping track of user prices for the tid
        this.tid2uid2price = {}; // if we want running totals even when transaction not final

        // SOCKET ROOM STATE
        // socket id to uid (since disconnect only sends socket id)
        this.socketId2uid = {};

        // tid to number of connections
        // to check if last user left
        this.tid2num = {};

        // only one active billify session per user
        // do we need this at all
        // this.uid2Tid = {};
    }

    userJoin(socketId, uid, tid) {

    }

    userSelect(item_id, uid) {

    }

    userDeselect(item_id, uid) {

    }

    userLeave(socketId) {

    }
}

module.exports = User;
