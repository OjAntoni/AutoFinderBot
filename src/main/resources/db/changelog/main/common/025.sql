--liquibase formatted sql
-- changeset anton:025

ALTER TABLE car ADD COLUMN thumbnail_url TEXT;

CREATE TABLE car_image_urls (
    id BIGSERIAL PRIMARY KEY,
    car_id BIGINT NOT NULL,
    image_url TEXT NOT NULL
);

ALTER TABLE car_image_urls ADD CONSTRAINT fk_car_image_urls_2_car
    FOREIGN KEY (car_id) REFERENCES car (id) ON DELETE CASCADE;

CREATE INDEX idx_car_image_urls_car_id ON car_image_urls (car_id);

