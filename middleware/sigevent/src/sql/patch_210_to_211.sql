--
-- Copyright (c) 2009, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: $
--

--
-- This SQL script to update database to conform with v2.0.0.
--
ALTER TABLE sys_event_group MODIFY category VARCHAR2(100 CHAR);
