--liquibase formatted sql

--changeset liquibase:17
ALTER TABLE product
    ADD auction_type varchar(255);