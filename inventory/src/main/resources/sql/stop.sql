/*
** Copyright (c) 2007, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
** 
** $Id: stop.sql 457 2007-12-07 17:19:53Z shardman $
*/

/*
** This script sets system variables for the completion of an 
** Inventory schema operation.
*/

set echo off;

set heading off;

select 'Completed at ' ||
to_char(sysdate,'YYYY-MM-DD"T"HH24:MI:SS".000"') || '.'
from dual;

quit;

