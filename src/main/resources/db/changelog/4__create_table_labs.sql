--liquibase formatted sql

--changeset MaxMart71:1
CREATE TABLE labs(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES user_details(chat_id),
    subject TEXT NOT NULL,
    lab_number TEXT NOT NULL,
    link TEXT NOT NULL,
    status TEXT NOT NUll
)



