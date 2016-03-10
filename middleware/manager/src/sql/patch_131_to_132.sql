--
-- Copyright (c) 2009, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: $
--

--
-- This SQL script to correct database-related issues found in v1.3.1
--


ALTER TABLE ing_engine_job 
MODIFY previous_state VARCHAR2(23 CHAR);
