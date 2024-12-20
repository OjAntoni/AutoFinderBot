--liquibase formatted sql
-- changeset anton:004

DELETE FROM car_detail WHERE car_id = 0;
