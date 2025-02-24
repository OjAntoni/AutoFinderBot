--liquibase formatted sql
-- changeset anton:023

CREATE TABLE account (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(256) NOT NULL ,
    password VARCHAR(256) NOT NULL ,
    role VARCHAR(256))
