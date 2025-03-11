--liquibase formatted sql

--changeset dezzzl:1
CREATE TABLE intermediate_state(
    id SERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES user_details(chat_id),
    state jsonb
);