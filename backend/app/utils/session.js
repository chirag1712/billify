const { UserItem } = require("../models/item.model");
const { UserTransaction } = require("../models/UserTransaction.model");
const Transaction = require("../models/transaction.model.js");

// ==== Data classes ====
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

class ItemInfo {
    constructor(price, userInfos) {
        this.price = price;
        this.userInfos = userInfos;
    }
}

// ==== socket session state handler ====
class Session {
    constructor() {
        // ==== TRANSACTION STATE ====
        // stores tid to itemid to itemInfo
        this.tid2itemInfos = {};

        // stores tid to uid to userPriceInfo
        this.tid2uid2userPriceInfo = {};

        // ==== SOCKET ROOM STATE ====
        // socket id to userInfo (since disconnect only sends socket id)
        this.socketId2userInfo = {};

        // tid to number of connections (to check if last user left)
        this.tid2num = {};

        // maps uid to tid for a session
        this.uid2Tid = {};
    }

    async userJoin(socketId, userInfo, tid) {
        this.socketId2userInfo[socketId] = userInfo;
        this.uid2Tid[userInfo.uid] = tid;
        if (this.tid2num[tid]) {
            // not the first user to join the session
            this.tid2num[tid]++;

            // get current socket state
            return this.getState(tid);
        }

        // first user to join the session, fetch state from db
        // optional TODO: modify s.t. fetch from session when available
        this.tid2num[tid] = 1;
        try {
            const itemId2itemInfo = {};
            const items = await Transaction.getTransactionItems(tid);
            const fetchPromises = items.map(async (item) => {
                const res = await UserItem.getUserInfoForItem(item.item_id);
                const userInfos = [];
                res.map((row) => {
                    userInfos.push(new UserInfo(row.uid, row.user_name));
                });
                itemId2itemInfo[item.item_id] = new ItemInfo(item.price, userInfos);
            });

            // update current socket state
            await Promise.all(fetchPromises);
            const state = await this.setState(tid, itemId2itemInfo);
            return state;
        } catch (err) {
            console.log("Internal error: Couldn't fetch transaction state: " + err);
        }
    }

    userSelect(userInfo, tid, item_id) {
        // subtract old price shares
        this.subtractOldPriceShare(tid, this.tid2itemInfos[tid][item_id]);

        // add the new user
        this.tid2itemInfos[tid][item_id].userInfos.add(JSON.stringify(userInfo));
        const itemInfo = this.tid2itemInfos[tid][item_id];

        // parse to JSONs before returning
        const userInfoObjs = this.getArrayOfJsonsFromSet(itemInfo.userInfos);

        // update new price shares
        const priceShares = this.updatePriceShares(tid, itemInfo.price, userInfoObjs);

        return { priceShares, userInfoObjs };
    }

    userDeselect(userInfo, tid, item_id) {
        // this should never happen
        const userInfoStr = JSON.stringify(userInfo);
        if (!this.tid2itemInfos[tid][item_id].userInfos.has(userInfoStr)) {
            throw Error(userInfo.uid + "can not deselect " + item_id + " because they didn't select it first");
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
    // state: {
    //      price_shares: {[uid] -> userPriceInfo}, 
    //      items: [{item_id, userInfos}]
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
    async setState(tid, itemId2itemInfo) {
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

        Object.entries(itemId2itemInfo).forEach(([item_id, { price, userInfos }]) => {
            state.items.push({ item_id, userInfos });
            this.tid2itemInfos[tid][item_id] = new ItemInfo(price, new Set());
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
            console.log("updating price share:", uid, username);
            const old = this.tid2uid2userPriceInfo[tid][uid].price_share;
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
        // https://stackoverflow.com/questions/8864430/compare-javascript-array-of-objects-to-get-min-max used this for reference
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
