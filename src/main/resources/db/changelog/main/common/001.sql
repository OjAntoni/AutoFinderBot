--liquibase formatted sql
-- changeset anton:001

CREATE TABLE car_detail (
    id BIGSERIAL PRIMARY KEY,
    car_response_id BIGINT NOT NULL,
    detail VARCHAR(255) NOT NULL,
    value VARCHAR(255) NOT NULL
);

CREATE TABLE car (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    brand VARCHAR(255),
    fuel_type VARCHAR(255),
    mileage BIGINT,
    mileage_unit VARCHAR(255),
    price BIGINT,
    currency VARCHAR(255),
    url VARCHAR(255)
);