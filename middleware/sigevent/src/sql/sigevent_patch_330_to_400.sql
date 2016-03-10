--
-- Copyright (c) 2009, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: $
--

--
-- This SQL script to update database to conform with v2.0.0.
--

ALTER TABLE sys_user ADD setting_clob CLOB;
UPDATE sys_user SET setting_clob = setting;
ALTER TABLE sys_user DROP COLUMN setting;
ALTER TABLE sys_user RENAME COLUMN setting_clob TO setting;
