--liquibase formatted sql
-- changeset anton:001

insert into car (id, title, brand, fuel_type, mileage, mileage_unit, price, currency, url, created_at, description, source) values
(1, 'Audi A4', 'Audi', 'Diesel', 100000, 'km', 10000, 'EUR', 'https://www.example.com/audi-a4', now() - interval '1 day', 'Description 1', 'OTOMOTO'),
(2, 'BMW 3', 'BMW', 'Petrol', 200000, 'km', 20000, 'EUR', 'https://www.example.com/bmw-3' , now() - interval '2 day', 'Description 2', 'OTOMOTO'),
(3, 'Mercedes C', 'Mercedes', 'Diesel', 300000, 'km', 30000, 'EUR', 'https://www.example.com/mercedes-c', now() - interval '3 day', 'Description 3', 'OTOMOTO'),
(4, 'Toyota Corolla expired 1', 'Toyota', 'Petrol', 400000, 'km', 40000, 'EUR', 'https://www.otomoto.pl/osobowe/oferta/invalid-url-1', now() - interval '32 day', 'Description 4', 'OLX'),
(5, 'Toyota Corolla expired 2', 'Toyota', 'Petrol', 400000, 'km', 40000, 'EUR', 'https://www.otomoto.pl/osobowe/oferta/invalid-url-2', now() - interval '33 day', 'Description 5', 'OLX'),
(6, 'Toyota Corolla expired 3', 'Toyota', 'Petrol', 400000, 'km', 40000, 'EUR', 'https://www.otomoto.pl/osobowe/oferta/invalid-url-3', now() - interval '34 day', 'Description 6', 'OLX');

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
(15, 6, 'year', '2018');

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

ALTER SEQUENCE car_id_seq RESTART WITH 100;
ALTER SEQUENCE car_detail_id_seq RESTART WITH 100;
ALTER SEQUENCE report_id_seq RESTART WITH 100;
ALTER SEQUENCE users_id_seq RESTART WITH 100;
ALTER SEQUENCE user_filter_id_seq RESTART WITH 100;
ALTER SEQUENCE selected_car_id_seq RESTART WITH 100;
ALTER SEQUENCE message_id_seq RESTART WITH 100;
ALTER SEQUENCE account_id_seq RESTART WITH 100;
