--
-- Copyright (c) 2009, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: $
--

--
-- This SQL script to add event category association to the data model
--


CREATE TABLE ing_event_category (
   id                   NUMBER(19, 0)     NOT NULL  ,
   version              NUMBER(19, 0)     NOT NULL  , 
   name                 VARCHAR2(40 CHAR) NOT NULL  ,

   CONSTRAINT ing_event_category_uq UNIQUE(name)
);

ALTER TABLE ing_product_type ADD (
   event_category_id    NUMBER(19, 0)
)

CREATE SEQUENCE ing_event_category_id_seq
   NOCACHE;
