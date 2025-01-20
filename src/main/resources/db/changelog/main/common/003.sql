--liquibase formatted sql
-- changeset anton:003

ALTER TABLE car_detail RENAME COLUMN car_response_id TO car_id;
