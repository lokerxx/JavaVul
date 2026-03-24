DROP TABLE IF EXISTS brute_force_users;

CREATE TABLE brute_force_users (
    id INTEGER PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    display_name VARCHAR(128) NOT NULL,
    role VARCHAR(32) NOT NULL,
    enabled INTEGER NOT NULL DEFAULT 1
);
