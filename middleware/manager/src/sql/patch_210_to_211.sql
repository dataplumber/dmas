--
-- Copyright (c) 2009, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: $
--

--
-- This SQL script to update database to conform with v2.0.0.
--
ALTER TABLE ing_event_category MODIFY name VARCHAR2(100 CHAR);

ALTER TABLE ing_product_type MODIFY name VARCHAR2(100 CHAR);

UPDATE ing_product_type SET name = substr(name, 8), note = substr(name, 8)||' product', relative_path = substr(name, 8)||'/' WHERE name LIKE 'GHRSST-%';
