const { UserItem } = require("../models/item.model");

class UserInfo {
    constructor(uid, username) {
        this.uid = uid;
        this.username = username;
    }
}

class Session {
    constructor() {
        // TRANSACTION STATE
        // stores tid to itemid to set of uids - state of transaction items
        this.tid2itemId2userInfos = {};

        // stores tid to uid to price owed for that transaction - state of user prices
        // this.tid2uid2price = {}; // if we want running totals even when transaction not final

        // SOCKET ROOM STATE
        // socket id to uid (since disconnect only sends socket id)
        this.socketId2userInfo = {};

        // tid to number of connections
        // to check if last user left
        this.tid2num = {};

        // only one active billify session per user
        // need this to check num connections on disconnect
        this.uid2Tid = {};
    }

    // returns true if first user
    userJoin(socketId, uid, username, tid) {
        this.socketId2userInfo[socketId] = new UserInfo(uid, username);
        this.uid2Tid[uid] = tid;
        if (this.tid2num[tid]) {
            this.tid2num[tid]++;
            return false;
        }
        this.tid2num[tid] = 1;
        return true;
    }

    userSelect(uid, username, tid, item_id) {
        if (!this.tid2itemId2userInfos[tid][item_id]) {
            this.tid2itemId2userInfos[tid][item_id] = new Set();
        }
        this.tid2itemId2userInfos[tid][item_id].add(new UserInfo(uid, username));
        const userInfos = Array.from(this.tid2itemId2userInfos[tid][item_id]);
        return { item_id, userInfos };
    }

    userDeselect(uid, username, tid, item_id) {
        if (!this.tid2itemId2userInfos[tid][item_id].delete(uid)) {
            throw Error(uid + " didnt select " + item_id + " to begin with");
        }
        const userInfos = Array.from(this.tid2itemId2userInfos[tid][item_id]);
        return { item_id, userInfos };
    }

    async userLeave(socketId) {
        const { uid } = this.socketId2userInfo[socketId];
        const tid = this.uid2Tid[uid];
        if (this.tid2num[tid] == 1) {
            console.log("last user leaving");
            // TODO: persist to db latest state
            // option 1: flush userItems for tid and push server state
            // option 2: search if useritem exists in db and not in state: delete 
            //      and if useritem does not exist in db but in state: insert

            // using option 1 for now
            await UserItem.deleteAll(tid);
            Object.entries(this.tid2itemId2userInfos[tid]).forEach(([item_id, userInfos]) => {
                userInfos.forEach(({uid}) => {
                    const userItem = new UserItem(tid, uid, item_id);
                    userItem.createUserItem();
                });
            });

            // clearing socket state
            // might not need to clear it as it can save db trip later
            delete this.tid2itemId2userInfos[tid];
        }

        // update room state
        this.tid2num[tid]--;
        delete this.socketId2userInfo[socketId];
        delete this.uid2Tid[uid];
    }

    // STATE MANAGEMENT
    // STATE: {
    //      user_prices: [{uid, price}], 
    //      items: [{item_id, price, uids: [uid]}]
    // }
    getState(tid) {
        const state = { items: [] };
        Object.entries(this.tid2itemId2userInfos[tid]).forEach(([item_id, userInfos]) => {
            state.items.push({ item_id, userInfos: Array.from(userInfos) });
        });
        return state;
    }

    // returns new state
    // todo: deal with prices later
    setState(tid, itemId2userInfos) {
        const state = { items: [] };
        Object.entries(itemId2userInfos).forEach(([item_id, userInfos]) => {
            state.items.push({ item_id, userInfos });
            if (!this.tid2itemId2userInfos[tid]) {
                this.tid2itemId2userInfos[tid] = {};
            }
            this.tid2itemId2userInfos[tid][item_id] = new Set(userInfos);
        });
        return state;
    }
}

module.exports = {
    Session: new Session(),
    UserInfo
};
