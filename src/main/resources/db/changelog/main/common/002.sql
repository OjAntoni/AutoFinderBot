--liquibase formatted sql
-- changeset anton:002

ALTER TABLE car_response ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE car_response RENAME TO car;
