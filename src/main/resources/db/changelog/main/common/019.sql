--liquibase formatted sql
-- changeset anton:019

ALTER TABLE message RENAME COLUMN state TO description_state;
ALTER TABLE message ADD COLUMN details_state varchar(64) NOT NULL DEFAULT 'DEFAULT';
