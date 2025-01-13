--liquibase formatted sql
-- changeset anton:008

ALTER TABLE users ALTER COLUMN search_url TYPE VARCHAR(2048);
ALTER TABLE users ALTER COLUMN search_url DROP NOT NULL;
ALTER TABLE users ADD COLUMN redirect_to VARCHAR(64);

CREATE TABLE user_filter (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    price_start NUMERIC(10, 2),
    price_end NUMERIC(10, 2),
    year_from INT,
    year_to INT,
    mileage_from INT,
    mileage_to INT,
    confirmed BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE user_filter_2_car_brands (
    user_filter_id BIGINT NOT NULL,
    car_brand_id BIGINT NOT NULL,
    PRIMARY KEY (user_filter_id, car_brand_id),
    CONSTRAINT fk_user_filter_brand FOREIGN KEY (user_filter_id) REFERENCES user_filter (id),
    CONSTRAINT fk_car_brand FOREIGN KEY (car_brand_id) REFERENCES car_brand (id)
);

CREATE TABLE user_filter_2_car_models (
    user_filter_id BIGINT NOT NULL,
    car_model_id BIGINT NOT NULL,
    PRIMARY KEY (user_filter_id, car_model_id),
    CONSTRAINT fk_user_filter_model FOREIGN KEY (user_filter_id) REFERENCES user_filter (id),
    CONSTRAINT fk_car_model FOREIGN KEY (car_model_id) REFERENCES car_model (id)
);

CREATE TABLE user_filter_2_generations (
    user_filter_id BIGINT NOT NULL,
    generation_id BIGINT NOT NULL,
    PRIMARY KEY (user_filter_id, generation_id),
    CONSTRAINT fk_user_filter_generation FOREIGN KEY (user_filter_id) REFERENCES user_filter (id),
    CONSTRAINT fk_generation FOREIGN KEY (generation_id) REFERENCES generation (id)
);

CREATE TABLE user_filter_2_fuel_types (
    user_filter_id BIGINT NOT NULL,
    fuel_type_id BIGINT NOT NULL,
    PRIMARY KEY (user_filter_id, fuel_type_id),
    CONSTRAINT fk_user_filter_fuel FOREIGN KEY (user_filter_id) REFERENCES user_filter (id),
    CONSTRAINT fk_fuel_type FOREIGN KEY (fuel_type_id) REFERENCES fuel_type (id)
);

