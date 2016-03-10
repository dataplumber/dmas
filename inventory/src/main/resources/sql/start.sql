/*
** Copyright (c) 2007, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
** 
** $Id: start.sql 457 2007-12-07 17:19:53Z shardman $
*/

/*
** This script sets system variables for the initiation of an
** Inventory schema operation.
*/

set serveroutput on;

set heading off;

SELECT 'Executed by ' || substr(user, 1, length(user)) ||
' at ' || to_char(sysdate,'YYYY-MM-DD"T"HH24:MI:SS".000"') || '.'
FROM dual;

set heading on;

set echo on;

