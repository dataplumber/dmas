--
-- Copyright (c) 2009, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: $
--

--
-- This SQL script to update database to conform with v2.0.0.
--

ALTER TABLE sys_user ADD setting VARCHAR2(1024 CHAR) DEFAULT NULL;
ALTER TABLE sys_notify ADD remind_rate NUMBER(10, 0) DEFAULT NULL;
ALTER TABLE sys_notify ADD last_remind NUMBER DEFAULT NULL;
ALTER TABLE sys_outgoing ADD is_remind NUMBER(1,0);
