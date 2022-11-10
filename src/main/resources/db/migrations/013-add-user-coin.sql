--liquibase formatted sql

--changeset liquibase:13
ALTER TABLE user_
    ADD coins bigint;