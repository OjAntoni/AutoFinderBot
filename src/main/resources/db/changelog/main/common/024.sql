--liquibase formatted sql
-- changeset anton:024

ALTER TABLE car ADD COLUMN source VARCHAR(10);

UPDATE car SET source = 'OTOMOTO' WHERE source IS NULL;

ALTER TABLE car ALTER COLUMN source SET NOT NULL;

ALTER TABLE car ADD CONSTRAINT chk_car_source CHECK (source IN ('OLX', 'OTOMOTO'));

