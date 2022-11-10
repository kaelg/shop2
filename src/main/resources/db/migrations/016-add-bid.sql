--liquibase formatted sql

--changeset liquibase:16
CREATE TABLE IF NOT EXISTS bid
(
    id          bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created     timestamp,
    amount      numeric(19, 2),
    updated     timestamp,
    auction_id bigint,
    user_id bigint,
    FOREIGN KEY (user_id)
    REFERENCES user_(id)  ON DELETE CASCADE,
    FOREIGN KEY (auction_id)
    REFERENCES auction (id)  ON DELETE CASCADE
);