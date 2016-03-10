--
-- Copyright (c) 2009, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: $
--

--
-- This SQL script to update database to conform with v2.0.0.
--
ALTER TABLE ing_federation ADD hostname VARCHAR2(30 CHAR) DEFAULT 'localhost' NOT NULL;
ALTER TABLE ing_federation ADD port NUMBER(10, 0) DEFAULT 8080 NOT NULL;
ALTER TABLE ing_storage ADD stereotype VARCHAR2(30 CHAR) DEFAULT 'INGEST' NOT NULL;
ALTER TABLE ing_storage ADD hostname VARCHAR2(30 CHAR) DEFAULT 'localhost' NOT NULL;
UPDATE ing_storage SET stereotype = (SELECT stereotype FROM ing_engine WHERE ing_engine.storage_id = ing_storage.id AND rownum=1);
UPDATE ing_storage SET hostname = (SELECT hostname FROM ing_engine WHERE ing_engine.storage_id = ing_storage.id AND rownum=1);

ALTER TABLE ing_product MODIFY current_lock VARCHAR2(16 CHAR);
ALTER TABLE ing_product ADD contribute_storage_id NUMBER(19, 0);
ALTER TABLE ing_product DROP CONSTRAINT ing_product_fk3;
ALTER TABLE ing_product ADD 
   CONSTRAINT ing_product_fk3
   FOREIGN KEY(contribute_storage_id) 
   REFERENCES ing_storage(id);
UPDATE ing_product SET contribute_storage_id = (SELECT storage_id FROM ing_engine WHERE ing_engine.id = ing_product.contribute_engine_id);
ALTER TABLE ing_product DROP COLUMN contribute_engine_id;

ALTER TABLE ing_engine_job DROP CONSTRAINT ing_engine_job_uq;
ALTER TABLE ing_engine_job ADD CONSTRAINT ing_engine_job_uq UNIQUE(product_id, operation);
ALTER TABLE ing_engine_job DROP COLUMN engine_id;
ALTER TABLE ing_engine_job ADD path VARCHAR2(255 CHAR);

DROP TABLE ing_engine cascade CONSTRAINTS PURGE;
DROP SEQUENCE ing_engine_id_seq;
