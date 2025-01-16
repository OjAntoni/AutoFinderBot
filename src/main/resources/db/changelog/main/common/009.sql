--liquibase formatted sql
-- changeset anton:009

ALTER TABLE report ADD COLUMN started_at TIMESTAMP;
ALTER TABLE report ADD COLUMN finished_at TIMESTAMP;

UPDATE report SET finished_at = created_at WHERE TRUE;

ALTER TABLE report DROP COLUMN created_at;
