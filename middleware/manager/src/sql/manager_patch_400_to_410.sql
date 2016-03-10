--
-- Copyright (c) 2009, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id$
--

--
-- This SQL script to update database to conform with v2.0.0.
--
ALTER TABLE ing_product_type ADD priority NUMBER(10, 0) CHECK(priority BETWEEN 1 AND 3);
UPDATE ing_product_type SET priority = 2;
ALTER TABLE ing_product_type MODIFY priority NOT NULL;

ALTER TABLE ing_engine_job ADD priority NUMBER(10, 0) NOT NULL CHECK(priority BETWEEN 1 AND 3);
ALTER TABLE ing_engine_job ADD contribute_storage_id NUMBER(19, 0) NOT NULL;
ALTER TABLE ing_engine_job ADD 
   CONSTRAINT ing_engine_job_fk3
   FOREIGN KEY(contribute_storage_id) 
   REFERENCES ing_storage(id);

CREATE TABLE ing_location(
   id                     NUMBER(19, 0)      NOT NULL,
   version                NUMBER(19, 0)      NOT NULL,
   created                NUMBER                     ,
   last_used              NUMBER                     ,
   local_path             VARCHAR2(255 CHAR) NOT NULL,
   remote_access_protocol VARCHAR2(5 CHAR)           ,
   remote_path            VARCHAR2(255 CHAR) NOT NULL,
   space_reserved         NUMBER(19, 0)      NOT NULL,
   space_threshold        NUMBER(19, 0)              ,
   space_used             NUMBER(19, 0)              ,
   stereotype             VARCHAR2(30 CHAR)  NOT NULL,
   hostname               VARCHAR2(30 CHAR)  NOT NULL,
   active                 NUMBER(1, 0)       NOT NULL,
   CONSTRAINT ing_location_pk PRIMARY KEY(id)
);

CREATE SEQUENCE ing_location_id_seq
   NOCACHE;

INSERT INTO ing_location(id, version, created, last_used, local_path, remote_access_protocol, remote_path, space_reserved, space_threshold, space_used, stereotype, hostname, active) 
   SELECT ing_location_id_seq.nextval, 0, created, last_used, local_path, remote_access_protocol, remote_path, space_reserved, space_threshold, space_used, stereotype, hostname, 1 FROM ing_storage;

UPDATE ing_location l1 SET space_used = (SELECT SUM(space_used) FROM ing_location l2 WHERE l2.local_path=l1.local_path GROUP BY local_path);
DELETE FROM ing_location l1 WHERE l1.rowid > ANY (SELECT l2.rowid FROM ing_location l2 WHERE l1.local_path = l2.local_path); 

ALTER TABLE ing_location ADD CONSTRAINT ing_location_uq UNIQUE(local_path);

ALTER TABLE ing_storage ADD priority NUMBER(10, 0) CHECK(priority BETWEEN 1 AND 3);
ALTER TABLE ing_storage ADD location_id NUMBER(19, 0);

UPDATE ing_storage SET location_id = (SELECT id FROM ing_location WHERE ing_location.local_path=ing_storage.local_path);

ALTER TABLE ing_storage MODIFY location_id NOT NULL;

ALTER TABLE ing_storage DROP (last_used, local_path, remote_access_protocol, remote_path, space_reserved, space_threshold, space_used, stereotype, hostname);

ALTER TABLE ing_storage ADD 
   CONSTRAINT ing_storage_fk1 
   FOREIGN KEY(location_id) 
   REFERENCES ing_location(id);

DELETE FROM ing_storage WHERE id IN (SELECT ins.id FROM ing_storage ins, ing_location loc WHERE loc.id=ins.location_id AND loc.stereotype='ARCHIVE');
