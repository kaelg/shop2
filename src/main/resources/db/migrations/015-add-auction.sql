--liquibase formatted sql

--changeset liquibase:15
CREATE TABLE IF NOT EXISTS auction_
(
    id          bigint NOT NULL PRIMARY KEY,
    starts    timestamp,
    ends   timestamp,
    FOREIGN KEY (id) REFERENCES product(id) ON DELETE CASCADE
);