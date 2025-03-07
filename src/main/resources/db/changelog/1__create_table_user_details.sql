--liquibase formatted sql

--changeset dezzzl:1
CREATE TABLE user_details(
    chat_id BIGINT PRIMARY KEY ,
    name VARCHAR(128) NOT NULL ,
    username VARCHAR(128) UNIQUE NOT NULL ,
    role VARCHAR(16) NOT NULL ,
    state VARCHAR(128)
)



