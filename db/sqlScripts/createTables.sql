CREATE TABLE IF NOT EXISTS User (
    user_id INT NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    hashed_password VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS BillifyGroup (
    group_id INT NOT NULL,
    user_id INT NOT NULL,
    group_name VARCHAR(255) NOT NULL,
    num_members INT,
    PRIMARY KEY (group_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

CREATE TABLE IF NOT EXISTS UserItem (
    item_id INT NOT NULL,
    user_id INT NOT NULL, 
    PRIMARY KEY(item_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

CREATE TABLE IF NOT EXISTS GroupTransaction (
    t_id INT NOT NULL,
    group_id INT NOT NULL, 
    receipt_img VARCHAR(255) NOT NULL,
    t_date DATE NOT NULL,
    t_state ENUM('NOT_STARTED', 'IN_PROGRESS', 'APPROVED'),
    PRIMARY KEY(t_id),
    FOREIGN KEY (group_id) REFERENCES BillifyGroup(group_id)
);

CREATE TABLE IF NOT EXISTS UserTransaction (
    item_id INT NOT NULL,
    user_id INT NOT NULL, 
    PRIMARY KEY(item_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);
