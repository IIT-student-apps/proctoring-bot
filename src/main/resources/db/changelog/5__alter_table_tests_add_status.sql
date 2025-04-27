--liquibase formatted sql

--changeset dezzzl:1
ALTER TABLE tests ADD COLUMN status VARCHAR;

--changeset dezzzl:2
ALTER TABLE tests ADD COLUMN table_link VARCHAR;


