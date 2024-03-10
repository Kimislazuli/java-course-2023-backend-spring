--liquibase formatted sql

--changeset kimislazuli:2
CREATE TABLE IF NOT EXISTS scrapper.public.link
(
    id          BIGINT  NOT NULL GENERATED ALWAYS AS IDENTITY,
    url         VARCHAR NOT NULL,
    last_update TIMESTAMP WITH TIME ZONE,
    last_check  TIMESTAMP WITH TIME ZONE,

    PRIMARY KEY (id),
    UNIQUE (url)
);
--rollback DROP TABLE scrapper.public.link
