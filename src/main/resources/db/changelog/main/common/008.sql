--liquibase formatted sql
-- changeset anton:008

ALTER TABLE users ALTER COLUMN search_url TYPE VARCHAR(2048);