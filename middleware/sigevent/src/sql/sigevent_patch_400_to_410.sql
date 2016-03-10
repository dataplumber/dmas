--
-- Copyright (c) 2009, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: $
--

--
-- This SQL script to update database to conform with v2.0.0.
--

ALTER TABLE sys_outgoing ADD message_type VARCHAR2(8 CHAR) DEFAULT 'NEW' NOT NULL;
UPDATE sys_outgoing SET message_type = 'REMINDER' WHERE is_remind=1;
ALTER TABLE sys_outgoing DROP COLUMN is_remind;

CREATE TABLE sys_user_setting (
   id       NUMBER(19, 0)       NOT NULL,
   version  NUMBER(19, 0)       NOT NULL,
   username VARCHAR2(40 CHAR)   NOT NULL,
   setting  CLOB,
   CONSTRAINT sys_user_setting_pk PRIMARY KEY (id),
   CONSTRAINT sys_user_setting_uq UNIQUE (username)
);

CREATE SEQUENCE sys_user_setting_id_seq
   NOCACHE;

INSERT INTO sys_user_setting(id, version, username, setting) SELECT sys_user_setting_id_seq.nextval, 0, username, setting FROM sys_user;

ALTER TABLE sys_user DROP COLUMN setting;
