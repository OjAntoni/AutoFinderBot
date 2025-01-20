--liquibase formatted sql
-- changeset anton:001

insert into car (id, title, brand, fuel_type, mileage, mileage_unit, price, currency, url, created_at) values
(1, 'Audi A4', 'Audi', 'Diesel', 100000, 'km', 10000, 'EUR', 'https://www.example.com/audi-a4', now() - interval '1 day'),
(2, 'BMW 3', 'BMW', 'Petrol', 200000, 'km', 20000, 'EUR', 'https://www.example.com/bmw-3' , now() - interval '2 day'),
(3, 'Mercedes C', 'Mercedes', 'Diesel', 300000, 'km', 30000, 'EUR', 'https://www.example.com/mercedes-c', now() - interval '3 day'),
(4, 'Toyota Corolla expired 1', 'Toyota', 'Petrol', 400000, 'km', 40000, 'EUR', 'https://www.otomoto.pl/osobowe/oferta/invalid-url-1', now() - interval '32 day'),
(5, 'Toyota Corolla expired 2', 'Toyota', 'Petrol', 400000, 'km', 40000, 'EUR', 'https://www.otomoto.pl/osobowe/oferta/invalid-url-2', now() - interval '33 day'),
(6, 'Toyota Corolla expired 3', 'Toyota', 'Petrol', 400000, 'km', 40000, 'EUR', 'https://www.otomoto.pl/osobowe/oferta/invalid-url-3', now() - interval '34 day');

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

insert into users(id, chat_id, search_url, redirect_to) values
(1, 1, 'https://www.example.com/search-1', NULL),
(2, 2, 'https://www.example.com/search-2', NULL),
(3, 3, 'https://www.example.com/search-3', NULL),
(4, 4, NULL, '/upload_url'),
(5, 5, 'https://www.otomoto.pl/osobowe/audi--bmw', '/confirm_filter'),
(6, 6, 'https://www.otomoto.pl/osobowe/audi--bmw', NULL);

insert into user_filter(id, user_id, price_start, price_end, year_from, year_to, mileage_from, mileage_to, confirmed) values
(1, 5, 10000, NULL, NULL, 2020, 100000, 150000, false),
(2, 6, 10000, NULL, NULL, NULL, 100000, 150000, true);

ALTER SEQUENCE car_id_seq RESTART WITH 100;
ALTER SEQUENCE car_detail_id_seq RESTART WITH 100;
ALTER SEQUENCE report_id_seq RESTART WITH 100;
ALTER SEQUENCE users_id_seq RESTART WITH 100;
ALTER SEQUENCE user_filter_id_seq RESTART WITH 100;
