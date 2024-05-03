--liquibase formatted sql

--changeset kimislazuli:1
CREATE TABLE IF NOT EXISTS chat
(
    id BIGINT NOT NULL PRIMARY KEY
);

--rollback DROP TABLE scrapper.public.chat
