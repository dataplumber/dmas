--
-- Copyright (c) 2009, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: $
--

--
-- This SQL script to update database to conform with v2.0.0.
--
ALTER TABLE ing_product_type ADD delivery_rate NUMBER(10, 0) DEFAULT NULL;
