--liquibase formatted sql
-- changeset anton:026

CREATE INDEX IF NOT EXISTS idx_car_brand     ON car (brand);
CREATE INDEX IF NOT EXISTS idx_car_fuel_type ON car (fuel_type);
CREATE INDEX IF NOT EXISTS idx_car_mileage   ON car (mileage);
CREATE INDEX IF NOT EXISTS idx_car_price     ON car (price);
CREATE INDEX IF NOT EXISTS idx_car_detail_model_val ON car_detail ((lower(detail)), value) WHERE lower(detail) = 'model';
CREATE INDEX IF NOT EXISTS idx_car_detail_year_val  ON car_detail ((lower(detail)), value) WHERE lower(detail) = 'year';
CREATE INDEX IF NOT EXISTS idx_car_detail_carid_detail_val ON car_detail(car_id, (lower(detail)), value);
CREATE INDEX IF NOT EXISTS idx_car_detail_carid ON car_detail(car_id);


