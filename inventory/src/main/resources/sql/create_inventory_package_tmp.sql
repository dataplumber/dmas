create or replace
PACKAGE INVENTORY as
 TYPE MyRefCur is REF CURSOR;

 FUNCTION longToTimestamp(int_to_convert in Number) return TIMESTAMP; 

 FUNCTION TimestampToLong(ts in TIMESTAMP) return NUMBER; 

 FUNCTION PSTTimestampToLong(ts in TIMESTAMP) return number;

  FUNCTION StringToFloat(str in VARCHAR2) return float;

END INVENTORY;
/

create or replace
PACKAGE BODY INVENTORY AS

function longToTimestamp(int_to_convert IN NUMBER)
return TIMESTAMP is
ts timestamp;
begin

return to_timestamp('1-JAN-1970 00:00:00', 'dd-mon-yyyy hh24:mi:ss') +
         numtodsinterval(int_to_convert /(1000*60*60*24),'DAY' );
end longToTimestamp;

function TimestampToLong(ts IN TIMESTAMP)
return NUMBER is
epoch timestamp;
num number;
begin

epoch := to_timestamp('1-JAN-1970 00:00:00', 'dd-mon-yyyy hh24:mi:ss');
    num:=0;
    num := (extract(day from (ts - epoch)) * 24*60*60000) +   (extract(hour from (ts - epoch)) *60*60000) +   (extract(minute from (ts - epoch)) *60000) +   (extract(second from (ts - epoch)) *1000);
    return num;

end TimestampToLong;

function PSTTimestampToLong(ts IN TIMESTAMP)
return NUMBER is
epoch timestamp;
gmtts timestamp;
begin

epoch := to_timestamp('1-JAN-1970 00:00:00', 'dd-mon-yyyy hh24:mi:ss');
gmtts := sys_extract_utc(from_tz(ts, 'PST'));

    return (extract(day from (gmtts - epoch)) * 24*60*60000) +   (extract(hour from (gmtts - epoch)) *60*60000) +   (extract(minute from (gmtts - epoch)) *60000) +   (extract(second from (gmtts - epoch)) *1000);
end PSTTimestampToLong;

FUNCTION StringToFloat(str in VARCHAR2) 
return float is
ret  number;
temp  VARCHAR2(80);
begin

temp := REPLACE(str, ' ');
temp := REPLACE(temp, 'km');
temp := REPLACE(temp, 'deg');
temp := REPLACE(temp, 'degree');
temp := REPLACE(temp, 'decimal degrees');

ret := to_number(temp);
return ret;

exception when others then
return null;

end StringToFloat;
END INVENTORY; 

/

