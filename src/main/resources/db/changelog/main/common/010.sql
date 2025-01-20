--liquibase formatted sql
-- changeset anton:010

ALTER TABLE car ADD CONSTRAINT unique_car_url UNIQUE (url);

CREATE INDEX car_created_at_asc_idx ON car (created_at ASC);
