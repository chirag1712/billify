CREATE TABLE IF NOT EXISTS User (
    uid INT NOT NULL AUTO_INCREMENT,
    user_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    hashed_password VARCHAR(255) NOT NULL,
    PRIMARY KEY (uid)
);

CREATE TABLE IF NOT EXISTS BillifyGroup (
    gid INT NOT NULL AUTO_INCREMENT,
    group_name VARCHAR(255) NOT NULL,
    num_members INT,
    PRIMARY KEY (gid)
);

CREATE TABLE IF NOT EXISTS MemberOf (
    gid INT NOT NULL,
    uid INT NOT NULL,
    PRIMARY KEY (uid, gid),
    FOREIGN KEY (gid) REFERENCES BillifyGroup(gid),
    FOREIGN KEY (uid) REFERENCES User(uid)
);

CREATE TABLE IF NOT EXISTS Transaction (
    tid INT NOT NULL AUTO_INCREMENT,
    gid INT NOT NULL,
    transaction_name VARCHAR(200) NOT NULL,
    receipt_img VARCHAR(500) NOT NULL,
    t_date DATE NOT NULL,
    t_state ENUM('NOT_STARTED', 'IN_PROGRESS', 'APPROVED'),
    PRIMARY KEY (tid),
    FOREIGN KEY (gid) REFERENCES BillifyGroup(gid)
);

CREATE TABLE IF NOT EXISTS Item (
    item_id INT NOT NULL AUTO_INCREMENT,
    tid INT NOT NULL,
    name VARCHAR(500) NOT NULL,
    price FLOAT NOT NULL,
    PRIMARY KEY (item_id),
    FOREIGN KEY (tid) REFERENCES Transaction(tid)
);

-- todo: might be possible to remove tid from this relation
CREATE TABLE IF NOT EXISTS UserItem (
    item_id INT NOT NULL,
    uid INT NOT NULL,
    tid INT NOT NULL,
    PRIMARY KEY (item_id, uid), 
    FOREIGN KEY (item_id) REFERENCES Item(item_id),
    FOREIGN KEY (uid) REFERENCES User(uid),
    FOREIGN KEY (tid) REFERENCES Transaction(tid)
);

CREATE TABLE IF NOT EXISTS UserTransaction (
    tid INT NOT NULL,
    uid INT NOT NULL,
    label_id INT NOT NULL,
    PRIMARY KEY (tid, uid),
    FOREIGN KEY (uid) REFERENCES User(uid),
    FOREIGN KEY (tid) REFERENCES Transaction(tid),
    FOREIGN KEY (label_id) REFERENCES Label(label_id)
);

CREATE TABLE IF NOT EXISTS Label (
    label_id INT NOT NULL,
    label_name VARCHAR(255) NOT NULL,
    label_color VARCHAR(255) NOT NULL,
    PRIMARY KEY (label_id)
);

INSERT INTO Label (label_id, label_name, label_color)
VALUES
(1, "Unlabelled", "#F0A500"),
(2, "Food", "#FF4848"),
(3,"Entertainment", "#035397"),
(4, "Groceries", "#1EAE98"),
(5, "Shopping", "#334756"),
(6, "Electronics", "#D62AD0"),
(7, "Housing", "#6930C3");