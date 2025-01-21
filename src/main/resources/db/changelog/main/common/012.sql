--liquibase formatted sql
-- changeset anton:012

CREATE TABLE user_filter_2_gearbox (
    user_filter_id BIGINT NOT NULL,
    gearbox_type VARCHAR(32) NOT NULL,
    CONSTRAINT fk_user_filter FOREIGN KEY (user_filter_id) REFERENCES user_filter (id) ON DELETE CASCADE
);

UPDATE user_filter_2_gearbox
SET gearbox_type = 'MANUAL'
WHERE gearbox_type = 'manual';

UPDATE user_filter_2_gearbox
SET gearbox_type = 'AUTOMATIC'
WHERE gearbox_type = 'automatic';
