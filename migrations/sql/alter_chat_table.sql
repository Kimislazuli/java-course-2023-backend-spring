--liquibase formatted sql

--changeset kimislazuli:4
ALTER TABLE chat ADD COLUMN state SMALLINT NOT NULL DEFAULT 0;

SELECT * FROM link

-- 0 - DEFAULT, 1 - WAIT_FOR_LINK_TO_ADD, 2 - WAIT_FOR_LINK_TO_REMOVE
