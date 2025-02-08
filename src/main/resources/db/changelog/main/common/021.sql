--liquibase formatted sql
-- changeset anton:021

ALTER TABLE user_filter ADD COLUMN search_url VARCHAR(2048);

UPDATE user_filter SET search_url =
(SELECT u.search_url FROM users u WHERE u.id = user_filter.user_id) WHERE TRUE;

ALTER TABLE user_filter ALTER COLUMN search_url SET NOT NULL;

ALTER TABLE users DROP COLUMN search_url;
