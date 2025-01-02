--liquibase formatted sql
-- changeset anton:007

CREATE TABLE car_brand (
    id BIGINT PRIMARY KEY,
    search_key VARCHAR(256) NOT NULL,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE car_model (
    id BIGINT PRIMARY KEY,
    search_key VARCHAR(256) NOT NULL,
    name VARCHAR(255) NOT NULL,
    car_brand_id BIGINT NOT NULL,
    CONSTRAINT fk_car_brand FOREIGN KEY (car_brand_id) REFERENCES car_brand (id) ON DELETE CASCADE
);

CREATE TABLE fuel_type (
    id BIGINT PRIMARY KEY,
    search_key VARCHAR(256) NOT NULL,
    name VARCHAR(255) NOT NULL
);
