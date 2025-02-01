--liquibase formatted sql
-- changeset anton:018

ALTER TABLE car ADD COLUMN description TEXT;

CREATE TABLE message (
    id BIGSERIAL PRIMARY KEY,
    chat_id BIGINT NOT NULL,
    car_id BIGINT NOT NULL,
    state varchar(64) NOT NULL
)