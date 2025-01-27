--liquibase formatted sql
-- changeset anton:016

CREATE TABLE seller (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    type VARCHAR(64),
    address VARCHAR(255),
    city VARCHAR(255),
    city_id BIGINT,
    region VARCHAR(255),
    region_id BIGINT,
    short_address VARCHAR(255),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION);

ALTER TABLE car ADD COLUMN seller_id BIGINT;
ALTER TABLE car ADD CONSTRAINT fk_seller FOREIGN KEY (seller_id) REFERENCES seller (id) ON DELETE CASCADE;

ALTER TABLE user_filter ADD COLUMN seller_type VARCHAR(64) DEFAULT NULL;
