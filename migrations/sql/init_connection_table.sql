--liquibase formatted sql

--changeset kimislazuli:3
CREATE TABLE IF NOT EXISTS scrapper.public.chat_to_link_connection
(
    chat_id BIGINT NOT NULL,
    link_id BIGINT NOT NULL,

    FOREIGN KEY (chat_id) REFERENCES scrapper.public.chat (id),
    FOREIGN KEY (link_id) REFERENCES scrapper.public.link (id)
);
--rollback DROP TABLE scrapper.public.chat_to_link_connection
