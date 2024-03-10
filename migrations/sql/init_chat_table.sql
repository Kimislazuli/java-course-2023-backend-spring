--liquibase formatted sql

--changeset kimislazuli:1
CREATE TABLE IF NOT EXISTS scrapper.public.chat
(
    id BIGINT NOT NULL PRIMARY KEY
);

--rollback DROP TABLE scrapper.public.chat
