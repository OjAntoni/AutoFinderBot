--liquibase formatted sql
-- changeset anton:015

ALTER TABLE user_filter ADD COLUMN active BOOLEAN NOT NULL DEFAULT FALSE;
