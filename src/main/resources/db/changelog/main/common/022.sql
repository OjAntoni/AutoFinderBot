--liquibase formatted sql
-- changeset anton:022

CREATE TABLE user_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    chat_id BIGINT,
    redirect_to VARCHAR(256),
    firstname VARCHAR(256),
    lastname VARCHAR(256),
    username VARCHAR(256),
    language_code VARCHAR(32),
    command VARCHAR(1024),
    data VARCHAR(2048),
    updated_at TIMESTAMP)
