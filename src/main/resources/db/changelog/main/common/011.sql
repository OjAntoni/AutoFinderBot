--liquibase formatted sql
-- changeset anton:011

CREATE TABLE notification (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    state VARCHAR(255)
);

CREATE TABLE notification_2_user (
    notification_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_notification FOREIGN KEY (notification_id) REFERENCES notification (id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id)
);
