--liquibase formatted sql

--changeset egorsemenovv:1
CREATE TABLE tests(
    id SERIAL PRIMARY KEY,
    author_id BIGINT REFERENCES user_details(chat_id),
    group_number VARCHAR(12) NOT NULL,
    name VARCHAR(128) UNIQUE NOT NULL,
    url VARCHAR(256) NOT NULL,
    start_time TIMESTAMP
);