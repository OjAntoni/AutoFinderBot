--liquibase formatted sql
-- changeset anton:002

ALTER TABLE car ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
