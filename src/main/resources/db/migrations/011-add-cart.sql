--liquibase formatted sql

--changeset liquibase:11
CREATE TABLE IF NOT EXISTS cart
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,
    total_amount bigint,
    created timestamp,
    updated timestamp,
    user_id bigint,
    FOREIGN KEY (user_id)
    REFERENCES user_(id)
    );