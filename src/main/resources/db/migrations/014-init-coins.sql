--liquibase formatted sql

--changeset liquibase:14
UPDATE user_
SET coins = 0
WHERE coins is null ;