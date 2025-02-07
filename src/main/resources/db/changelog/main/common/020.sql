--liquibase formatted sql
-- changeset anton:020

ALTER TABLE users ADD COLUMN firstname VARCHAR(255);
ALTER TABLE users ADD COLUMN lastname VARCHAR(255);
ALTER TABLE users ADD COLUMN username VARCHAR(255);
ALTER TABLE users ADD COLUMN language_code VARCHAR(32);
ALTER TABLE users ADD COLUMN last_active TIMESTAMP;
