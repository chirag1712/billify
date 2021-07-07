class Session {
    constructor() {
        // TRANSACTION STATE
        // stores tid to itemid to list of users - state of transaction items
        this.tid2itemId2uid = {};

        // stores tid to uid to price owed for that transaction - state of user prices
        this.tid2uid2price = {}; // if we want running totals even when transaction not final

        // SOCKET ROOM STATE
        // socket id to uid (since disconnect only sends socket id)
        this.socketId2uid = {};

        // tid to number of connections
        // to check if last user left
        this.tid2num = {};

        // only one active billify session per user
        // need this to check num connections on disconnect
        this.uid2Tid = {};
    }

    // returns true if first user
    userJoin(socketId, uid, tid) {
        this.socketId2uid[socketId] = uid;
        this.uid2Tid[uid] = tid;
        if (this.tid2num[tid]) {
            this.tid2num[tid]++;
            return false;
        }
        this.tid2num[tid] = 1;
        return true;
    }

    userSelect(item_id, uid) {
        
    }

    userDeselect(item_id, uid) {

    }

    userLeave(socketId) {
        const uid = this.socketId2uid[socketId];
        const tid = this.uid2Tid[uid];

        if (this.tid2num[tid] == 1) {
            // persist to db latest state from these:
            // this.tid2itemId2uid
            // this.tid2uid2price
        }

        delete this.tid2num[tid];
        delete this.socketId2uid[socketId];
        delete this.uid2Tid[uid];
    }

    // STATE MANAGEMENT
    // STATE: {
    //      user_prices: [{uid, price}], 
    //      items: [{item_id, price, users: [uid]}]
    // }
    getState(tid) {
        // should return uids mapped to {price: price, items: [{itemId, itemNames}]}
    }

    setState(tid, itemId, price, uids) {
        // should return new state as well
    }
}

module.exports = new Session();
