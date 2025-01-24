--liquibase formatted sql
-- changeset anton:014

ALTER TABLE user_filter ADD COLUMN state VARCHAR(64) NOT NULL default 'NEW';
