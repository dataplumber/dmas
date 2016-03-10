--
-- Copyright (c) 2009, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: $
--

--
-- This SQL script creates the database objects for the Sigevent domain schema.
--


CREATE TABLE sys_event_group (
   id         NUMBER(19, 0)     NOT NULL,
   version    NUMBER(19, 0)     NOT NULL,
   category   VARCHAR2(100 CHAR) NOT NULL,
   purge_rate NUMBER(19, 0)     NOT NULL,
   type       VARCHAR2(12 CHAR) NOT NULL,
   
   CONSTRAINT sys_event_group_pk PRIMARY KEY(id),
   CONSTRAINT sys_event_group_uq UNIQUE (type, category)
);

CREATE TABLE sys_event (
   id             NUMBER(19, 0)      NOT NULL,
   version        NUMBER(19, 0)      NOT NULL,
   computer       VARCHAR2(40 CHAR)  NOT NULL,
   data           CLOB               NULL,
   description    VARCHAR2(256 CHAR) NOT NULL,
   first_received NUMBER             NOT NULL,
   group_id       NUMBER(19, 0)      NOT NULL,
   last_received  NUMBER             NOT NULL,
   occurrence     NUMBER(19, 0)      NOT NULL,
   pid            NUMBER(19, 0)      NULL,
   provider       VARCHAR2(40 CHAR)  NOT NULL,
   resolution     VARCHAR2(256 CHAR) NOT NULL,
   resolved_at    NUMBER             NULL,
   source         VARCHAR2(40 CHAR)  NOT NULL,
   
   CONSTRAINT sys_event_pk PRIMARY KEY (id),
   CONSTRAINT sys_event_fk FOREIGN KEY (group_id) 
      REFERENCES sys_event_group(id)
);

CREATE TABLE sys_notify (
   id             NUMBER(19, 0)      NOT NULL,
   version        NUMBER(19, 0)      NOT NULL,
   contact        VARCHAR2(40 CHAR)  NOT NULL,
   group_id       NUMBER(19, 0)      NOT NULL,
   last_notified  NUMBER             NULL,
   last_report    NUMBER             NULL,
   last_remind    NUMBER             NULL,
   message_format VARCHAR2(10 CHAR)  NOT NULL,
   content        VARCHAR2(11 CHAR)  NOT NULL,
   method         VARCHAR2(20 CHAR)  NOT NULL,
   note           VARCHAR(256 CHAR)  NULL,
   rate           NUMBER(19, 0)      NOT NULL,
   remind_rate    NUMBER(10, 0)      NULL,
   
   CONSTRAINT sys_notify_pk PRIMARY KEY (id),
   CONSTRAINT sys_notify_fk FOREIGN KEY (group_id)
      REFERENCES sys_event_group(id)
);

CREATE TABLE sys_outgoing (
   id       NUMBER(19, 0)       NOT NULL,
   version  NUMBER(19, 0)       NOT NULL,
   category VARCHAR2(40 CHAR)   NOT NULL,
   contact  VARCHAR2(40 CHAR)   NOT NULL,
   created  NUMBER              NOT NULL,
   message  CLOB                NOT NULL,
   method   VARCHAR2(20 CHAR)   NOT NULL,
   type     VARCHAR2(12 CHAR)   NOT NULL,
   notify_id NUMBER(19, 0),
   message_type VARCHAR2(8 CHAR) NOT NULL,
   
   CONSTRAINT sys_outgoing_pk PRIMARY KEY (id)
);

CREATE TABLE sys_role (
   id       NUMBER(19, 0)       NOT NULL,
   version  NUMBER(19, 0)       NOT NULL,
   name VARCHAR2(100 CHAR)   NOT NULL,
   admin  NUMBER(1, 0)       NOT NULL,
   
   CONSTRAINT sys_role_pk PRIMARY KEY (id),
   CONSTRAINT sys_role_uq UNIQUE (name)
);

CREATE TABLE sys_user (
   id       NUMBER(19, 0)       NOT NULL,
   version  NUMBER(19, 0)       NOT NULL,
   username VARCHAR2(40 CHAR)   NOT NULL,
   password  VARCHAR2(100 CHAR)   NOT NULL,
   role_id  NUMBER(19, 0)       NOT NULL,
   
   CONSTRAINT sys_user_pk PRIMARY KEY (id),
   CONSTRAINT sys_user_uq UNIQUE (username),
   CONSTRAINT sys_user_fk FOREIGN KEY (role_id)
      REFERENCES sys_role(id)
);

CREATE TABLE sys_user_setting (
   id       NUMBER(19, 0)       NOT NULL,
   version  NUMBER(19, 0)       NOT NULL,
   username VARCHAR2(40 CHAR)   NOT NULL,
   setting  CLOB,
   
   CONSTRAINT sys_user_setting_pk PRIMARY KEY (id),
   CONSTRAINT sys_user_setting_uq UNIQUE (username)
);

CREATE SEQUENCE sys_event_group_id_seq
   NOCACHE;

CREATE SEQUENCE sys_event_id_seq
   NOCACHE;

CREATE SEQUENCE sys_notify_id_seq
   NOCACHE;

CREATE SEQUENCE sys_outgoing_id_seq
   NOCACHE;

CREATE SEQUENCE sys_user_id_seq
   NOCACHE;

CREATE SEQUENCE sys_user_setting_id_seq
   NOCACHE;

CREATE SEQUENCE sys_role_id_seq
   NOCACHE;
