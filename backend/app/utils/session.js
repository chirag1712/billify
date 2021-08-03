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
        this.subtractOldPriceShare(tid, this.tid2itemInfos[tid][item_id]);

        // add the new user
        this.tid2itemInfos[tid][item_id].userInfos.add(JSON.stringify(new UserInfo(uid, username)));
        const itemInfo = this.tid2itemInfos[tid][item_id];

        // parse to JSONs before returning
        const userInfoObjs = this.getArrayOfJsonsFromSet(itemInfo.userInfos);

        // update new price shares
        const priceShares = this.updatePriceShares(tid, itemInfo.price, userInfoObjs);

        return { priceShares, userInfoObjs };
    }

    userDeselect(uid, username, tid, item_id) {
        // this should never happen
        const userInfoStr = JSON.stringify(new UserInfo(uid, username));
        if (!this.tid2itemInfos[tid][item_id].userInfos.has(userInfoStr)) {
            throw Error(uid + "can not deselect " + item_id + " because they didn't select it first");
        }

        // subtract old price shares
        this.subtractOldPriceShare(tid, this.tid2itemInfos[tid][item_id]);

        // remove the user
        this.tid2itemInfos[tid][item_id].userInfos.delete(userInfoStr);
        const itemInfo = this.tid2itemInfos[tid][item_id];

        // parse to JSONs before returning
        const userInfoObjs = this.getArrayOfJsonsFromSet(itemInfo.userInfos);

        // update new price shares
        const priceShares = this.updatePriceShares(tid, itemInfo.price, userInfoObjs);

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
            Object.entries(this.tid2itemInfos[tid]).forEach(([item_id, { price, userInfos }]) => {
                userInfos.forEach((userInfoString) => {
                    const { uid } = JSON.parse(userInfoString);
                    const userItem = new UserItem(tid, uid, item_id);
                    userItem.createUserItem();
                });
            });

            // TODO: persist price shares to db
            Object.entries(this.tid2uid2userPriceInfo[tid]).forEach(([uidStr, userPriceInfo]) => {
                UserTransaction.updatePriceShare(tid, uidStr, userPriceInfo.price_share);  
            })

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

        // fetch price_shares for all members using UserTransaction table
        const allUserTransactionInfos = await UserTransaction.getUserTransactionInfosForTransaction(tid);
        allUserTransactionInfos.forEach((userTransactionInfo) => {
            state.price_shares[userTransactionInfo.uid] = {
                userName: userTransactionInfo.user_name,
                price_share: userTransactionInfo.price_share
            };
            this.tid2uid2userPriceInfo[tid][userTransactionInfo.uid] = new UserPriceInfo(
                userTransactionInfo.user_name,
                userTransactionInfo.price_share
            );
        });

        Object.entries(itemInfos).forEach(([item_id, { price, userInfos }]) => {
            state.items.push({ item_id, userInfos });
            this.tid2itemInfos[tid][item_id] = { price: price, userInfos: new Set() };
            userInfos.forEach((userInfoObj) => {
                this.tid2itemInfos[tid][item_id].userInfos.add(JSON.stringify(userInfoObj));
            });
        });

        return state;
    }

    // ==== utility functions ====
    getArrayOfJsonsFromSet(userInfos) {
        /* converts an array of json strings to an array of JSON parsed objects */
        const userInfoObjs = [];
        Array.from(userInfos).forEach((userInfoStr) => {
            userInfoObjs.push(JSON.parse(userInfoStr));
        });
        return userInfoObjs;
    }

    subtractOldPriceShare(tid, itemInfo) {
        /* subtracts an item's old price share from all it's selectors */
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

    // used by userSelect and userDeselect
    updatePriceShares(tid, price, userInfoObjs) {
        if (userInfoObjs.length == 0) {
            return this.tid2uid2userPriceInfo[tid];
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
