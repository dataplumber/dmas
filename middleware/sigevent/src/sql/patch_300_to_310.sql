--
-- Copyright (c) 2009, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: $
--

--
-- This SQL script to update database to conform with v2.0.0.
--

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

CREATE SEQUENCE sys_user_id_seq
   NOCACHE;

CREATE SEQUENCE sys_role_id_seq
   NOCACHE;
