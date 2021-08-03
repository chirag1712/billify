const { UserItem } = require("../models/item.model");
const { UserTransaction } = require("../models/UserTransaction.model");

class UserInfo {
    constructor(uid, username) {
        this.uid = uid;
        this.username = username;
    }
}

class UserPriceInfo {
    constructor(username, price_share) {
        this.username = username;
        this.price_share = price_share;
    }
}

class Session {
    constructor() {
        // TRANSACTION STATE
        // stores tid to itemid to itemInfo (price and corresponding userInfos)
        this.tid2itemInfos = {};

        // stores tid to uid to price owed for that transaction
        this.tid2uid2userPriceInfo = {};

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
        // subtract old price shares
        this.subtractCosts(tid, this.tid2itemInfos[tid][item_id]);
        this.tid2itemInfos[tid][item_id].userInfos.add(JSON.stringify(new UserInfo(uid, username)));
        const itemInfo = this.tid2itemInfos[tid][item_id];

        // parse to JSONs before returning
        const userInfoObjs = this.getArrayOfJsonsFromSet(itemInfo.userInfos);

        // update new price shares
        const priceShares = this.updatePriceShares(tid, itemInfo.price, userInfoObjs);

        return { priceShares, userInfoObjs };
    }

    getArrayOfJsonsFromSet(userInfos) {
        const userInfoObjs = [];
        Array.from(userInfos).forEach((userInfoStr) => {
            userInfoObjs.push(JSON.parse(userInfoStr));
        });
        return userInfoObjs;
    }

    subtractCosts(tid, itemInfo) {
        if (itemInfo.userInfos.size == 0) {
            return;
        }

        const userInfoObjs = this.getArrayOfJsonsFromSet(itemInfo.userInfos);
        const oldMinUid = this.findMinUid(userInfoObjs);
        const old_n = itemInfo.userInfos.size;
        const [old_share, old_last_share] = this.dividePrice(itemInfo.price, old_n);

        userInfoObjs.forEach(({ uid, _ }) => {
            const old = this.tid2uid2userPriceInfo[tid][uid].price_share;
            if (uid == oldMinUid) {
                const val = parseFloat((old - old_last_share).toFixed(2));
                this.tid2uid2userPriceInfo[tid][uid].price_share = val;
            } else {
                const val = parseFloat((old - old_share).toFixed(2));
                this.tid2uid2userPriceInfo[tid][uid].price_share = val;
            }
        });
    }

    userDeselect(uid, username, tid, item_id) {
        const oldMinUid = this.findMinUid(this.tid2itemInfos[tid][item_id].userInfos);
        if (!this.tid2itemInfos[tid][item_id].userInfos.delete(JSON.stringify(new UserInfo(uid, username)))) {
            throw Error(uid + " did not select " + item_id + " to begin with");
        }

        // parse to JSONs before returning
        const userInfos = Array.from(this.tid2itemInfos[tid][item_id].userInfos);
        const userInfoObjs = [];
        userInfos.forEach((userInfoStr) => {
            userInfoObjs.push(JSON.parse(userInfoStr));
        });

        // update price shares
        const priceShares = this.updatePriceShares(tid, itemInfo.price, userInfoObjs, oldMinUid, itemInfo.userInfos.length + 1, itemInfo.userInfos.length);

        return { priceShares, userInfoObjs };
    }

    async userLeave(socketId) {
        if (!this.socketId2userInfo[socketId]) {
            return; // zombie user leaving
        }
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
            Object.entries(this.tid2itemInfos[tid]).forEach(([item_id, userInfos]) => {
                userInfos.forEach((userInfoString) => {
                    const { uid } = JSON.parse(userInfoString);
                    const userItem = new UserItem(tid, uid, item_id);
                    userItem.createUserItem();
                });
            });

            // TODO: persist price shares to db as well (update price_share in usertransaction table)

            // clearing socket state
            // might not need to clear it as it can save db trip later
            delete this.tid2itemInfos[tid];
        }

        // update room state
        this.tid2num[tid]--;
        delete this.socketId2userInfo[socketId];
        delete this.uid2Tid[uid];
    }

    // STATE MANAGEMENT
    // STATE: {
    //      price_shares: {[uid] -> {userName, price_share}}, 
    //      items: [{item_id, price, uids: [uid]}]
    // }
    getState(tid) {
        const state = { items: [], price_shares: {} };
        Object.entries(this.tid2itemInfos[tid]).forEach(([item_id, { price, userInfos }]) => {
            const arr = [];
            userInfos.forEach((userInfoString) => {
                arr.push(JSON.parse(userInfoString));
            });
            state.items.push({ item_id, userInfos: arr });
        });

        Object.entries(this.tid2uid2userPriceInfo[tid]).forEach(([uidStr, userPriceInfo]) => {
            state.price_shares[uidStr] = { userName: userPriceInfo.username, price_share: userPriceInfo.price_share };
        })
        return state;
    }

    // returns new state
    async setState(tid, itemInfos) {
        const state = { items: [], price_shares: {} };
        if (!this.tid2itemInfos[tid]) {
            this.tid2itemInfos[tid] = {};
        }
        if (!this.tid2uid2userPriceInfo[tid]) {
            this.tid2uid2userPriceInfo[tid] = {};
        }

        // initialize price_shares for all members of the group
        const allUserInfos = await UserTransaction.getUserInfosForTransaction(tid);
        allUserInfos.forEach((userInfo) => {
            state.price_shares[userInfo.uid] = { userName: userInfo.user_name, price_share: 0 };
            this.tid2uid2userPriceInfo[tid][userInfo.uid] = new UserPriceInfo(userInfo.user_name, 0);
        });

        Object.entries(itemInfos).forEach(([item_id, { price, userInfos }]) => {
            state.items.push({ item_id, userInfos });
            this.tid2itemInfos[tid][item_id] = { price: price, userInfos: new Set() };
            userInfos.forEach((userInfoObj) => {
                this.tid2itemInfos[tid][item_id].add(JSON.stringify(userInfoObj));
            });
        });

        // set tid2uid2userPriceInfo and also update state
        state.price_shares = this.calculatePriceShares(tid, itemInfos, state);
        return state;
    }

    // method for price share calculation here
    // should be called on select, deselect and start session for the first user
    //  (from userSelect, userDeselect and setState)

    // should be used for select and deselect
    updatePriceShares(tid, price, userInfoObjs) {
        if (userInfoObjs.length == 0) {
            return;
        }

        const new_n = userInfoObjs.length;
        const newMinUid = this.findMinUid(userInfoObjs);
        const [new_share, new_last_share] = this.dividePrice(price, new_n);

        userInfoObjs.forEach(({ uid, username }) => {
            console.log("updating:", uid, username);
            const old = this.tid2uid2userPriceInfo[tid][uid].price_share;
            console.log("old", old);
            var val;
            if (uid == newMinUid) {
                val = parseFloat((old + new_last_share).toFixed(2));
            } else {
                val = parseFloat((old + new_share).toFixed(2));
            }
            this.tid2uid2userPriceInfo[tid][uid].price_share = val;
        });
        return this.tid2uid2userPriceInfo[tid];
    }

    // should be used for setState
    calculatePriceShares(tid, itemInfos, state) {
        Object.entries(itemInfos).forEach(([_, { price, userInfos }]) => {
            const [share, last_share] = this.dividePrice(price, userInfos.length);
            if (userInfos.length == 0) {
                return; // skip this iteration
            }
            const minUid = this.findMinUid(userInfos);
            console.log("Min UID:", minUid);
            for (let i = 0; i < n; i++) {
                const userInfoObj = userInfos[i];
                const old = this.tid2uid2userPriceInfo[tid][userInfoObj.uid].price_share;
                var val;
                if (userInfoObj.uid == minUid) {
                    val = parseFloat((old + last_share).toFixed(2));
                } else {
                    val = parseFloat((old + share).toFixed(2));
                }
                this.tid2uid2userPriceInfo[tid][userInfoObj.uid].price_share = val;
                state.price_shares[userInfoObj.uid].price_share = val;
            }
        });
        return state.price_shares;
    }

    findMinUid(userInfoObjs) {
        return userInfoObjs.reduce((prev, curr) => {
            return prev.uid < curr.uid ? prev : curr;
        }).uid;
    }

    dividePrice(price, n) {
        const share = parseFloat((price / n).toFixed(2));
        const last_share = parseFloat((price - (share * (n - 1))).toFixed(2));
        return [share, last_share];
    }
}

module.exports = { Session: new Session(), UserInfo };
