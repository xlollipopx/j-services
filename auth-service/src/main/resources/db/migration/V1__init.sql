CREATE SEQUENCE person_id_seq AS BIGINT INCREMENT 1 START 100;

create table person(
person_id BIGINT primary key not null default nextval('person_id_seq'),
username TEXT,
name TEXT,
picture TEXT,
password_hash TEXT
);