create or replace
PACKAGE INVENTORY as
 TYPE MyRefCur is REF CURSOR;

 PROCEDURE getTemporalSpatialOverview(westLong IN NUMBER, eastLong IN NUMBER, northLat IN NUMBER, southLat IN NUMBER, startDate_unix_ts IN DATE, stopDate_unix_ts IN DATE, rdac IN VARCHAR2, source_sensor_combo IN VARCHAR2, cCursor OUT MyRefCur);

 PROCEDURE getTemporalSpatialDetail(westLong IN NUMBER, eastLong IN NUMBER, northLat IN NUMBER, southLat IN NUMBER, startDate_unix_ts IN DATE, stopDate_unix_ts IN DATE, rdac IN VARCHAR2, source_sensor_combo IN VARCHAR2, cCursor OUT MyRefCur);

 PROCEDURE getPathAndType(granuleId IN NUMBER, cCursor out MyRefCur);
 
 FUNCTION longToTimestamp(int_to_convert in Number) return TIMESTAMP; 

 FUNCTION TimestampToLong(ts in TIMESTAMP) return NUMBER; 

 FUNCTION PSTTimestampToLong(ts in TIMESTAMP) return number;

 FUNCTION StringToFloat(str in VARCHAR2) return float;

END INVENTORY;
/
create or replace
PACKAGE BODY INVENTORY AS

procedure getTemporalSpatialOverview(westLong IN NUMBER, eastLong IN NUMBER, northLat IN NUMBER, southLat IN NUMBER, 
startDate_unix_ts IN DATE, stopDate_unix_ts IN DATE, rdac IN VARCHAR2, source_sensor_combo IN VARCHAR2, cCursor out MyRefCur )
 is
  start_t number;
  stop_t number; 
  BEGIN
   
    start_t := inventory.timestamptolong(CAST(startdate_unix_ts AS TIMESTAMP));
    stop_t := inventory.timestamptolong(CAST(stopdate_unix_ts AS TIMESTAMP));
  
  OPEN cCursor FOR
  select  granule.granule_id, granule.name 
  from granule, (
  /*BEGIN 'temp' table creation*/ 
  Select granule_id as grid,
     MAX(case when de_id=(select de_id from dataset_element d where d.dataset_id=(select dataset_id from granule where granule.granule_id = granule_real.granule_id) and d.element_id in (select element_id from element_dd where short_name='westernmostLongitude')) then value else null end) west_long,
     MAX(case when de_id=(select de_id from dataset_element d where d.dataset_id=(select dataset_id from granule where granule.granule_id = granule_real.granule_id) and d.element_id in (select element_id from element_dd where short_name='easternmostLongitude')) then value else null end) east_long,
     MAX(case when de_id=(select de_id from dataset_element d where d.dataset_id=(select dataset_id from granule where granule.granule_id = granule_real.granule_id) and d.element_id in (select element_id from element_dd where short_name='northernmostLatitude')) then value else null end) north_lat,
     MAX(case when de_id=(select de_id from dataset_element d where d.dataset_id=(select dataset_id from granule where granule.granule_id = granule_real.granule_id) and d.element_id in (select element_id from element_dd where short_name='southernmostLatitude')) then value else null end) south_lat
  from granule_real
  where granule_id IN (select granule_id from granule where dataset_id in (SELECT dataset_id FROM dataset WHERE REGEXP_LIKE(short_name,rdac,'i') AND
                                                                           REGEXP_LIKE(short_name,source_sensor_combo,'i')) AND granule.status = 'ONLINE' 
  
  /*
  (select de_id from dataset_element d where d.dataset_id=g.dataset_id and d.element_id in (select element_id from element_dd where short_name='northLatitude'))
  */
  
  AND  (
      (granule.start_time_long >= start_t AND granule.stop_time_long <= stop_t)
      OR(granule.stop_time_long >= start_t AND granule.stop_time_long <=stop_t )
      OR( granule.start_time_long >= start_t AND granule.start_time_long <= stop_t)
    ))
  group by granule_id)
/*END 'temp' table creation*/  
where
    
 granule.granule_id = grid /*AND granule.start_time > to_date('2009-03-20 00:00:00','yyyy-mm-dd HH24:MI:SS') AND granule.stop_time < to_date('2009-03-21 00:00:00','yyyy-mm-dd HH24:MI:SS')*/
  
   
  
  AND(
    (
      (west_long >= westLong AND west_long <= eastLong) OR
      (east_long <= eastLong AND east_long >= westLong) OR
      (west_long <= westLong AND east_long >= eastLong)
    )
    AND
    (
      (south_lat >= southLat AND south_lat <= northLat) OR
      (north_lat <= northLat AND north_lat >= southLat) OR
      (south_lat <= southLat AND north_lat >= northLat)
    )
  )
  
  AND rownum<=10000
  
  ORDER BY granule.name
  ;
    return; 
END getTemporalSpatialOverview;

procedure getTemporalSpatialDetail(westLong IN NUMBER, eastLong IN NUMBER, northLat IN NUMBER, southLat IN NUMBER, 
startDate_unix_ts IN DATE, stopDate_unix_ts IN DATE, rdac IN VARCHAR2, source_sensor_combo IN VARCHAR2, cCursor out MyRefCur )
 is
  start_t NUMBER;
  stop_t number; 
  BEGIN
   
    start_t := inventory.timestamptolong(CAST(startdate_unix_ts AS TIMESTAMP));
    stop_t := inventory.timestamptolong(CAST(stopdate_unix_ts AS TIMESTAMP));
  
  OPEN cCursor FOR
  select  granule.granule_id, granule.name, granule_reference.path, granule_reference.type
  from granule, granule_reference, (
  Select granule_id as grid,
     MAX(case when de_id=(select de_id from dataset_element d where d.dataset_id=(select dataset_id from granule where granule.granule_id = granule_real.granule_id) and d.element_id in (select element_id from element_dd where short_name='westernmostLongitude')) then value else null end) west_long,
     MAX(case when de_id=(select de_id from dataset_element d where d.dataset_id=(select dataset_id from granule where granule.granule_id = granule_real.granule_id) and d.element_id in (select element_id from element_dd where short_name='easternmostLongitude')) then value else null end) east_long,
     MAX(case when de_id=(select de_id from dataset_element d where d.dataset_id=(select dataset_id from granule where granule.granule_id = granule_real.granule_id) and d.element_id in (select element_id from element_dd where short_name='northernmostLatitude')) then value else null end) north_lat,
     MAX(case when de_id=(select de_id from dataset_element d where d.dataset_id=(select dataset_id from granule where granule.granule_id = granule_real.granule_id) and d.element_id in (select element_id from element_dd where short_name='southernmostLatitude')) then value else null end) south_lat
  from granule_real
   where granule_id IN (select granule_id from granule where dataset_id in (SELECT dataset_id FROM dataset WHERE REGEXP_LIKE(short_name,rdac,'i') AND
                                                                           REGEXP_LIKE(short_name,source_sensor_combo,'i')) AND granule.status = 'ONLINE' 
   AND  (
      (granule.start_time_long >= start_t AND granule.stop_time_long <= stop_t)
      OR(granule.stop_time_long >= start_t AND granule.stop_time_long <=stop_t )
      OR( granule.start_time_long >= start_t AND granule.start_time_long <= stop_t)
    )                                                                                                                                     )
  group by granule_id)
  
  where granule.granule_id = grid and granule.granule_id = granule_reference.granule_id
  AND(
    (
      (west_long >= westLong AND west_long <= eastLong) OR
      (east_long <= eastLong AND east_long >= westLong) OR
      (west_long <= westLong AND east_long >= eastLong)
    )
    AND
    (
      (south_lat >= southLat AND south_lat <= northLat) OR
      (north_lat <= northLat AND north_lat >= southLat) OR
      (south_lat <= southLat AND north_lat >= northLat)
    )
  )
  AND rownum<=10000
  ORDER BY granule.name, granule_reference.type;
    return; 
END getTemporalSpatialDetail;



PROCEDURE getPathAndType(granuleId IN NUMBER, cCursor out MyRefCur )
 is
  BEGIN
  OPEN cCursor FOR
  SELECT path, type FROM granule_reference WHERE granule_id = granuleId;
return;
END getPathAndType;

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
