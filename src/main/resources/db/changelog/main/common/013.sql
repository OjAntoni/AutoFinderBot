--liquibase formatted sql
-- changeset anton:013

ALTER TABLE user_filter ADD COLUMN damaged BOOLEAN DEFAULT NULL;
