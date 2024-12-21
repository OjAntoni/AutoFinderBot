--liquibase formatted sql
-- changeset anton:006

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    chat_id BIGINT NOT NULL,
    search_url VARCHAR(255) NOT NULL
);
