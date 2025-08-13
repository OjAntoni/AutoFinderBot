--liquibase formatted sql
-- changeset anton:001

insert into car (id, title, brand, fuel_type, mileage, mileage_unit, price, currency, url, created_at, description, source, thumbnail_url) values
(1, 'Audi A4', 'Audi', 'Diesel', 100000, 'km', 10000, 'EUR', 'https://www.example.com/audi-a4', now() - interval '1 day', 'Description 1', 'OTOMOTO', 'https://www.example.com/audi-a4-thumbnail'),
(2, 'BMW 3', 'BMW', 'Petrol', 200000, 'km', 20000, 'EUR', 'https://www.example.com/bmw-3' , now() - interval '2 day', 'Description 2', 'OTOMOTO', 'https://www.example.com/bmw-3-thumbnail'),
(3, 'Mercedes C', 'Mercedes', 'Diesel', 300000, 'km', 30000, 'EUR', 'https://www.example.com/mercedes-c', now() - interval '3 day', 'Description 3', 'OTOMOTO', 'https://www.example.com/mercedes-c-thumbnail'),
(4, 'Toyota Corolla expired 1', 'Toyota', 'Petrol', 400000, 'km', 40000, 'EUR', 'https://www.otomoto.pl/osobowe/oferta/invalid-url-1', now() - interval '32 day', 'Description 4', 'OLX', null),
(5, 'Toyota Corolla expired 2', 'Toyota', 'Petrol', 400000, 'km', 40000, 'EUR', 'https://www.otomoto.pl/osobowe/oferta/invalid-url-2', now() - interval '33 day', 'Description 5', 'OLX', 'https://www.otomoto.pl/osobowe/oferta/invalid-url-2-thumbnail'),
(6, 'Toyota Corolla expired 3', 'Toyota', 'Petrol', 400000, 'km', 40000, 'EUR', 'https://www.otomoto.pl/osobowe/oferta/invalid-url-3', now() - interval '34 day', 'Description 6', 'OLX', 'https://www.otomoto.pl/osobowe/oferta/invalid-url-3-thumbnail'),
(7, 'Audi A4 similar A', 'Audi', 'Diesel', 102000, 'km', 10500, 'EUR', 'https://www.example.com/audi-a4-7', now() - interval '1 hour', 'Similar to #1, within band', 'OTOMOTO', 'https://www.example.com/audi-a4-7-thumb'),
(8, 'Audi A4 similar B', 'Audi', 'Diesel', 95000, 'km', 9500, 'EUR', 'https://www.example.com/audi-a4-8', now() - interval '2 hours', 'Similar to #1, within band', 'OTOMOTO', 'https://www.example.com/audi-a4-8-thumb'),
(9, 'Audi A4 far mileage', 'Audi', 'Diesel', 500000, 'km', 15000, 'EUR', 'https://www.example.com/audi-a4-9', now() - interval '3 hours', 'Out of band mileage', 'OTOMOTO', 'https://www.example.com/audi-a4-9-thumb'),
(10, 'BMW 3 similar low', 'BMW', 'Petrol', 190000, 'km', 19000, 'EUR', 'https://www.example.com/bmw-3-10', now() - interval '4 hours', 'Similar to #2', 'OTOMOTO', 'https://www.example.com/bmw-3-10-thumb'),
(11, 'BMW 3 similar high', 'BMW', 'Petrol', 210000, 'km', 21000, 'EUR', 'https://www.example.com/bmw-3-11', now() - interval '5 hours', 'Similar to #2', 'OTOMOTO', 'https://www.example.com/bmw-3-11-thumb'),
(12, 'BMW 3 clearly lower', 'BMW', 'Petrol', 205000, 'km', 18000, 'EUR', 'https://www.example.com/bmw-3-12', now() - interval '6 hours', 'Below 0.95*avg', 'OTOMOTO', 'https://www.example.com/bmw-3-12-thumb'),
(13, 'BMW 3 clearly higher', 'BMW', 'Petrol', 195000, 'km', 23000, 'EUR', 'https://www.example.com/bmw-3-13', now() - interval '7 hours', 'Above 1.05*avg', 'OTOMOTO', 'https://www.example.com/bmw-3-13-thumb'),
(14, 'Mercedes C no-mileage', 'Mercedes', 'Diesel', null, 'km', 25000, 'EUR', 'https://www.example.com/mercedes-c-14', now() - interval '8 hours', 'Null mileage -> band [0,0]', 'OTOMOTO', 'https://www.example.com/mercedes-c-14-thumb'),
(15, 'Mercedes C case-key', 'Mercedes', 'Diesel', 300000, 'km', 32000, 'EUR', 'https://www.example.com/mercedes-c-15', now() - interval '9 hours', 'Case-insensitive detail key', 'OTOMOTO', 'https://www.example.com/mercedes-c-15-thumb'),
(16, 'Toyota Corolla fresh', 'Toyota', 'Petrol', 400000, 'km', 38000, 'EUR', 'https://www.otomoto.pl/osobowe/oferta/valid-url-4', now() - interval '10 day', 'Fresh listing for expiry contrast', 'OLX', 'https://www.example.com/toyota-corolla-16-thumb');

insert into car_detail (id, car_id, detail, value) values
(1, 1, 'color', 'black'),
(2, 1, 'year', '2010'),
(3, 2, 'color', 'white'),
(4, 2, 'year', '2015'),
(5, 3, 'color', 'silver'),
(6, 3, 'year', '2018'),
(7, 1, 'engine', '2.0 TDI'),
(8, 2, 'engine', '2.0 TFSI'),
(9, 3, 'engine', '2.0 TDI'),
(10, 4, 'color', 'red'),
(11, 4, 'year', '2010'),
(12, 5, 'color', 'blue'),
(13, 5, 'year', '2015'),
(14, 6, 'color', 'green'),
(15, 6, 'year', '2018'),
(16, 1, 'model', 'A4'),
(17, 1, 'damaged', 'Nie'),
(18, 2, 'model', '3'),
(19, 2, 'damaged', 'Tak'),
(20, 3, 'model', 'C'),
(21, 3, 'damaged', 'Tak'),
(22, 4, 'model', 'Corolla'),
(23, 5, 'model', 'Corolla'),
(24, 6, 'model', 'Corolla'),
(25, 7, 'MODEL', 'A4'),
(26, 7, 'year', '2010'),
(27, 7, 'damaged', 'Nie'),
(28, 8, 'model', 'A4'),
(29, 8, 'year', '2010'),
(30, 8, 'damaged', 'Tak'),
(31, 9, 'model', 'A4'),
(32, 9, 'year', '2010'),
(33, 9, 'damaged', 'Nie'),
(34, 10, 'model', '3'),
(35, 10, 'year', '2015'),
(36, 10, 'damaged', 'Nie'),
(37, 11, 'model', '3'),
(38, 11, 'year', '2015'),
(39, 11, 'damaged', 'Nie'),
(40, 12, 'model', '3'),
(41, 12, 'year', '2015'),
(42, 12, 'damaged', 'Tak'),
(43, 13, 'model', '3'),
(44, 13, 'year', '2015'),
(45, 13, 'damaged', 'Nie'),
(46, 14, 'model', 'C'),
(47, 14, 'year', '2018'),
(48, 15, 'Model', 'C'),
(49, 15, 'year', '2018'),
(50, 15, 'damaged', 'Nie'),
(51, 16, 'model', 'Corolla'),
(52, 16, 'year', '2015'),
(53, 16, 'damaged', 'Nie');

insert into users(id, chat_id, redirect_to, firstname) values
(1, 1, NULL, 'John'),
(2, 2, NULL, 'Alice'),
(3, 3, NULL, 'Bob'),
(4, 4, '/upload_url', 'Charlie'),
(5, 5, '/confirm_filter', 'David'),
(6, 6, NULL, 'Eve'),
(7, 7, 'redirection', 'Eve');

insert into user_filter(id, user_id, price_start, price_end, year_from, year_to, mileage_from, mileage_to, confirmed, active, search_url) values
(1, 5, 10000, NULL, NULL, 2020, 100000, 150000, false, true, 'https://www.otomoto.pl/osobowe/audi--bmw'),
(2, 6, 10000, NULL, NULL, NULL, 100000, 150000, true, true, 'https://www.otomoto.pl/osobowe/audi--bmw'),
(3, 3, 50000, NULL, 2016, NULL, 10453, 444000, true, false, 'https://www.example.com/search-3');

insert into selected_car(id, user_id, url, car_id, created_at, name, message_id) values
(1, 1, 'https://www.example.com/audi-a4', 1, now() - interval '1 day', 'Audi A4', 1),
(2, 1, 'https://www.example.com/bmw-3', 2, now() - interval '2 day', 'BMW 3', 2),
(3, 1, 'https://www.example.com/mercedes-c', 3, now() - interval '3 day', 'Mercedes C', 3),
(4, 1, 'https://www.otomoto.pl/osobowe/oferta/invalid-url-1', 4, now() - interval '32 day', 'Toyota Corolla expired 1', 4),
(5, 1, 'https://www.otomoto.pl/osobowe/oferta/invalid-url-2', 5, now() - interval '33 day', 'Toyota Corolla expired 2', 5),
(6, 6, 'https://www.otomoto.pl/osobowe/oferta/invalid-url-3', 6, now() - interval '34 day', 'Toyota Corolla expired 3', 6);

insert into notification(id, title, description, state) values
(1, 'Title 1', 'Description 1', 'PROCESSED'),
(2, 'Title 2', 'Description 2', 'NEW');

insert into notification_2_user(notification_id, user_id) values
(1, 1),
(1, 2),
(1, 3),
(2, 4),
(2, 5),
(2, 6);

insert into message(id, chat_id, car_id, description_state, details_state) values
(1, 2, 1, 'DESCRIPTION', 'DEFAULT'),
(2, 3, 1, 'DEFAULT', 'DETAILS');

insert into account(id, username, password, role) values
(1, 'admin_user', 'password', 'ADMIN'),
(2, 'user', 'password', NULL);

insert into car_brand (id, search_key, name) values
(1, 'audi', 'Audi'),
(2, 'bmw', 'BMW');

insert into car_model (id, search_key, name, car_brand_id) values
(1, 'a4', 'A4', 1),
(2, 'a6', 'A6', 1),
(3, 'm3', '3', 2);

insert into generation (id, search_key, name, car_model_id) values
(1, 'b8', 'B8', 1),
(2, 'b9', 'B9', 1),
(3, 'g20', 'G20', 3);

insert into fuel_type (id, search_key, name) values
(1, 'diesel', 'Diesel'),
(2, 'petrol', 'Petrol');

ALTER SEQUENCE car_id_seq RESTART WITH 100;
ALTER SEQUENCE car_detail_id_seq RESTART WITH 100;
ALTER SEQUENCE report_id_seq RESTART WITH 100;
ALTER SEQUENCE users_id_seq RESTART WITH 100;
ALTER SEQUENCE user_filter_id_seq RESTART WITH 100;
ALTER SEQUENCE selected_car_id_seq RESTART WITH 100;
ALTER SEQUENCE message_id_seq RESTART WITH 100;
ALTER SEQUENCE account_id_seq RESTART WITH 100;
