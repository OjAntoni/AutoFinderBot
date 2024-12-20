--liquibase formatted sql
-- changeset anton:005

CREATE TABLE report (
    id BIGSERIAL PRIMARY KEY,
    affected_rows BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    operation VARCHAR(255) NOT NULL
);

CREATE TABLE target_ids_2_report (
    report_id BIGINT NOT NULL,
    target_id BIGINT NOT NULL,
    CONSTRAINT fk_report FOREIGN KEY (report_id) REFERENCES report (id) ON DELETE CASCADE
);

CREATE INDEX idx_target_ids_report ON target_ids_2_report (report_id);
