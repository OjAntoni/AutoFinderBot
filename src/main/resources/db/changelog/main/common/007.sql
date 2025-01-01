--liquibase formatted sql
-- changeset anton:007

CREATE TABLE car_model (
    id BIGINT PRIMARY KEY,
    search_key VARCHAR(256) NOT NULL,
    name VARCHAR(255) NOT NULL
);
