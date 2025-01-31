--liquibase formatted sql
-- changeset anton:017

CREATE TABLE selected_car (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    url VARCHAR(255) NOT NULL,
    car_id BIGINT,
    created_at TIMESTAMP NOT NULL,
    name VARCHAR(255) NOT NULL,
    message_id INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id));


