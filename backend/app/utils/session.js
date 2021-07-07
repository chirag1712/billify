class Session {
    constructor() {
        // TRANSACTION STATE
        // stores tid to itemid to set of uids - state of transaction items
        this.tid2itemId2uids = {};

        // stores tid to uid to price owed for that transaction - state of user prices
        // this.tid2uid2price = {}; // if we want running totals even when transaction not final

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

    userSelect(tid, item_id, uid) {
        if(!this.tid2itemId2uids[tid][item_id]) {
            this.tid2itemId2uids[tid][item_id] = new Set();
        }
        this.tid2itemId2uids[tid][item_id].add(uid);
        const uids = Array.from(this.tid2itemId2uids[tid][item_id]);
        return {item_id, uids};
    }

    userDeselect(tid, item_id, uid) {
        if(!this.tid2itemId2uids[tid][item_id].delete(uid)) {
            throw Error(uid + " didnt select " + item_id + " to begin with");
        }
        const uids = Array.from(this.tid2itemId2uids[tid][item_id]);
        return {item_id, uids};
    }

    userLeave(socketId) {
        const uid = this.socketId2uid[socketId];
        const tid = this.uid2Tid[uid];

        if (this.tid2num[tid] == 1) {
            // TODO: persist to db latest state - flush userItems for this tid and push server state
            // can also clear socket state for this transaction since next fetch would fetch from db
            // might not need to clear it as it can save db trip
        }

        // update room state
        delete this.tid2num[tid];
        delete this.socketId2uid[socketId];
        delete this.uid2Tid[uid];
    }

    // STATE MANAGEMENT
    // STATE: {
    //      user_prices: [{uid, price}], 
    //      items: [{item_id, price, uids: [uid]}]
    // }
    getState(tid) {
        const state = {items: []};
        for ([item_id, uids] in Object.entries(this.tid2itemId2uids[tid])) {
            state.items.push({ item_id, uids: Array.from(uids) });
        }
        return state;
    }

    // returns new state
    // todo: deal with prices later
    setState(tid, itemId2uids) {
        const state = { items: [] };
        Object.entries(itemId2uids).forEach(([item_id, uids]) => {
            state.items.push({ item_id, uids });
            if (!this.tid2itemId2uids[tid]) {
                this.tid2itemId2uids[tid] = {};
            }
            this.tid2itemId2uids[tid][item_id] = new Set(uids);
        });
        return state;
    }
}

module.exports = new Session();