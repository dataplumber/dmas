/*
  Remvoing the source from dataset_source
*/
alter table SENSOR drop column SOURCE_ID;

delete from dataset_source where rowid in (
select rowid  from(select dataset_source.rowid ,dataset_id, source.short_name as sn, sensor.short_name as ssn from dataset_source, source, sensor where dataset_source.sensor_id=sensor.sensor_id and dataset_source.source_id= source.source_id) A
where rowid >
(SELECT min(rowid) from (select dataset_id, source.short_name as sn, sensor.short_name as ssn from dataset_source, source, sensor where dataset_source.sensor_id=sensor.sensor_id and dataset_source.source_id= source.source_id) B
where A.dataset_id = B.dataset_id
AND A.sn = B.sn
AND A.ssn = B.ssn)
);

UPDATE dataset_source x set x.sensor_id=(
  select MIN(sensor_id) from sensor ss where ss.short_name in (
    select short_name from sensor s 
    where s.sensor_id=x.sensor_id));

delete from sensor where sensor_id in (select sensor_id from sensor where sensor_id not in (select sensor_id from dataset_source));

UPDATE dataset_source x set x.source_id=(
  select MIN(source_id) from source ss where ss.short_name in (
    select short_name from source s
    where s.source_id=x.source_id));
    
delete from source where source_id in (select source_id from source where source_id not in (select source_id from dataset_source));

/*
Add new column to dataset_policy (LATENCY) val in hours
*/

ALTER TABLE DATASET_POLICY ADD ("DATA_LATENCY" NUMBER);

/*
Add New Table for manifests
*/
CREATE SEQUENCE metadata_manifest_id_seq
  NOCACHE;
/*
** METADATA MANIFEST
*/
CREATE TABLE metadata_manifest
(       
  manifest_id NUMBER NOT NULL,
  item_id               NUMBER                  NOT NULL,
  type                  VARCHAR2(10 BYTE)       NOT NULL,
  username              VARCHAR2(20)            NOT NULL,
  submission_date_long  NUMBER                  NOT NULL,
  manifest               CLOB                   NOT NULL,
  CONSTRAINT metadata_manifest_pk PRIMARY KEY (manifest_id)
);


ALTER TABLE ELEMENT_DD 
ADD ("SCOPE" NUMBER)
;

ALTER TABLE ELEMENT_DD 
ADD ("MAX_LENGTH" NUMBER)
;


Alter table element_dd  drop column DB_TABLE;

INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'anomalyType' ,'Anomaly Type' ,'character', 'Type of anomaly reported.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'eventType' ,'Event Type' ,'character', 'Type of Event, such as distribution, anomaly, Generation, etc.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'dataOriginationName' ,'Data Origination Name' ,'character', 'The project origination name for this granule.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'deliveryVersion' ,'Delivery Version' ,'character', 'A version identifier supplied by the data provider.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'GFZName' ,'GFZ Name' ,'character', 'The German GeoForschungsZentrum (GFZ) Potsdam file name for a Grace granule.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'imageGranularity' ,'Image Granularity' ,'character', 'Image granularity type for this granule.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'imageMask' ,'Image Mask' ,'character', 'Image mask type for this granule.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'imageType' ,'Image Type' ,'character', 'Image type for this granule.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'originalInventoryLocation' ,'Original Inventory Location' ,'character', 'Provides the original location of the granule if it has been moved.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'phase' ,'Phase' ,'character', 'The ERS-1 mission was comprised of seven phases. Each phase is a time period in which the mission parameters and characteristics remain unchanged.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'polarization' ,'Polarization' ,'character', 'Polarization for this granule.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'processType' ,'Process Type' ,'character', 'Process type for this granule.');

INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'dataConsumerId' , 'Data Consumer ID', 'integer', 'ID of the data consumer.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'dataProviderId' , 'Data Provider ID', 'integer', 'ID of the data provider.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'duration' , 'Duration', 'integer', 'The duration of granule measurement (in minutes).');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'eventId' , 'Event ID', 'integer', 'The ID of the event.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'size' , 'Size', 'integer', 'Size of ganule (in KB).');

INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'eventTime' , 'Event Time', 'datetime', 'The time of the event.');


update element_dd set short_name='startTime' where short_name='start_time';
update element_dd set short_name='stopTime' where short_name='stop_time';
update element_dd set short_name='regionDetail' where short_name='region_detail';
update element_dd set short_name='latitudeResolution' where short_name='latitude_resolution';
update element_dd set short_name='longitudeResolution' where short_name='longitude_resolution';
update element_dd set short_name='horizontalResolutionRage' where short_name='horizontal_resolution_range';
update element_dd set short_name='altitudeResolution' where short_name='altitude_resolution';
update element_dd set short_name='depthResolution' where short_name='depth_resolution';
update element_dd set short_name='temporalResolution' where short_name='temporal_resolution';
update element_dd set short_name='temporalResolutionRange' where short_name='temporal_resolution_range';
update element_dd set short_name='ellipsoidType' where short_name='ellipsoid_type';
update element_dd set short_name='projectionType' where short_name='projection_type';
update element_dd set short_name='projectionDetail' where short_name='projection_detail';

/*
Updates to the data model (again)
*/

CREATE SEQUENCE dataset_element_id_seq
  NOCACHE;

drop table granule_element;

ALTER TABLE DATASET_ELEMENT ADD (
"DE_ID" NUMBER,
"SCOPE" VARCHAR2(10)
);
ALTER TABLE DATASET_ELEMENT DROP CONSTRAINT "DATASET_ELEMENT_PK";
update dataset_element set de_id=dataset_element_id_seq.nextval;
update dataset_element set SCOPE='BOTH';


/*
INSERT NEW VALS
*/

insert into dataset_element (dataset_id, element_id, obligation_flag, de_id, scope)
select dataset_id, 3, 'O',dataset_element_id_seq.nextval ,'GRANULE' from dataset;

/*
DATASET_CHARACTER
*/
ALTER TABLE DATASET_CHARACTER ADD("DE_ID" NUMBER);
ALTER TABLE DATASET_CHARACTER  DROP CONSTRAINT "DATASET_CHARACTER_FK2";
update dataset_character dc set de_id=(select de_id from dataset_element where dc.dataset_id=dataset_element.dataset_id AND dc.element_id= dataset_element.element_id);
ALTER TABLE DATASET_CHARACTER DROP COLUMN "ELEMENT_ID";


/*
 DATASET_DATETIME
*/
alter table dataset_datetime add( "DE_ID" NUMBER);
ALTER TABLE DATASET_DATETIME DROP CONSTRAINT "DATASET_DATETIME_FK2";
update dataset_datetime dc set de_id=(select de_id from dataset_element where dc.dataset_id=dataset_element.dataset_id AND dc.element_id= dataset_element.element_id);
ALTER TABLE DATASET_DATETIME DROP COLUMN "ELEMENT_ID";

/*
 DATASET_INTEGER
*/
alter table dataset_integer add( "DE_ID" NUMBER);
ALTER TABLE DATASET_INTEGER DROP CONSTRAINT "DATASET_INTEGER_FK2";
update dataset_integer dc set de_id=(select de_id from dataset_element where dc.dataset_id=dataset_element.dataset_id AND dc.element_id= dataset_element.element_id);
ALTER TABLE DATASET_INTEGER DROP COLUMN "ELEMENT_ID";

/*
 DATASET_REAL
*/
alter table dataset_real add( "DE_ID" NUMBER);
ALTER TABLE DATASET_REAL DROP CONSTRAINT "DATASET_REAL_FK2";
update dataset_real dc set de_id=(select de_id from dataset_element where dc.dataset_id=dataset_element.dataset_id AND dc.element_id= dataset_element.element_id);
ALTER TABLE DATASET_REAL DROP COLUMN "ELEMENT_ID";


CREATE INDEX DATASET_ELEMENT_INDEX1 ON DATASET_ELEMENT (DE_ID);
CREATE INDEX DATASET_ELEMENT_INDEX2 ON DATASET_ELEMENT (DATASET_ID);


/*
 GRANULE_CHARACTER
*/
alter table granule_character rename to granule_character_bk;
CREATE TABLE granule_character
(
  granule_id            NUMBER          NOT NULL,
  de_id            NUMBER          NOT NULL,
  value                 VARCHAR2(255)
);

insert into dataset_element (dataset_id, element_id, obligation_flag,  scope)
select gc.dataset_id, gc.element_id, 'O','GRANULE'  from (select distinct dataset_id, element_id from granule_character_bk, granule where granule.granule_id=granule_character_bk.granule_id) gc
LEFT OUTER JOIN dataset_element de
on gc.dataset_id=de.dataset_id AND gc.element_id=de.element_id
where DE_ID is null
ORDER BY gc.dataset_id;

update dataset_element set de_id=dataset_element_id_seq.nextval where de_id is null;

insert into granule_character gc (granule_id, de_id, value)
select gcb.granule_id,(select de_id from dataset_element where dataset_id=(select dataset_id from granule g where g.granule_id=gcb.granule_id) AND element_id= gcb.element_id ) ,gcb.value
from granule_character_bk gcb;

/*
 GRANULE_DATETIME
*/
alter table granule_datetime rename to granule_datetime_bk;
CREATE TABLE granule_datetime
(
  granule_id            NUMBER          NOT NULL,
  de_id            NUMBER          NOT NULL,
  value_long NUMBER  
);

insert into dataset_element (dataset_id, element_id, obligation_flag,  scope)
select gc.dataset_id, gc.element_id, 'O','GRANULE'  from (select distinct dataset_id, element_id from granule_datetime_bk, granule where granule.granule_id=granule_datetime_bk.granule_id) gc
LEFT OUTER JOIN dataset_element de
on gc.dataset_id=de.dataset_id AND gc.element_id=de.element_id
where DE_ID is null
ORDER BY gc.dataset_id;
update dataset_element set de_id=dataset_element_id_seq.nextval where de_id is null;

insert into granule_datetime gc (granule_id, de_id, value_long)
select gcb.granule_id,(select de_id from dataset_element where dataset_id=(select dataset_id from granule g where g.granule_id=gcb.granule_id) AND element_id= gcb.element_id ) ,gcb.value_long
from granule_datetime_bk gcb;

/*
 GRANULE_INTEGER
*/
alter table granule_integer rename to granule_integer_bk;
CREATE TABLE granule_integer
(
  granule_id            NUMBER          NOT NULL,
  de_id            NUMBER          NOT NULL,
  value                 NUMBER,
  units					VARCHAR2(10)
);

insert into dataset_element (dataset_id, element_id, obligation_flag,  scope)
select gc.dataset_id, gc.element_id, 'O','GRANULE'  from (select distinct dataset_id, element_id from granule_integer_bk, granule where granule.granule_id=granule_integer_bk.granule_id) gc
LEFT OUTER JOIN dataset_element de
on gc.dataset_id=de.dataset_id AND gc.element_id=de.element_id
where DE_ID is null
ORDER BY gc.dataset_id;
update dataset_element set de_id=dataset_element_id_seq.nextval where de_id is null;

insert into granule_integer gc (granule_id, de_id, value, units)
select gcb.granule_id,(select de_id from dataset_element where dataset_id=(select dataset_id from granule g where g.granule_id=gcb.granule_id) AND element_id= gcb.element_id ) ,gcb.value, units
from granule_integer_bk gcb;
/*
 GRANULE_REAL
*/
alter table granule_real rename to granule_real_bk;
CREATE TABLE granule_real
(  granule_id            NUMBER          NOT NULL,
  de_id            NUMBER          NOT NULL,
  value                 NUMBER,
  units                                 VARCHAR2(10)
);

insert into dataset_element (dataset_id, element_id, obligation_flag,  scope)
select gc.dataset_id, gc.element_id, 'O','GRANULE'  from (select distinct dataset_id, element_id from granule_real_bk, granule where granule.granule_id=granule_real_bk.granule_id) gc
LEFT OUTER JOIN dataset_element de
on gc.dataset_id=de.dataset_id AND gc.element_id=de.element_id
where DE_ID is null
ORDER BY gc.dataset_id;update dataset_element set de_id=dataset_element_id_seq.nextval where de_id is null;

insert into granule_real gc (granule_id, de_id, value, units)
select gcb.granule_id,(select de_id from dataset_element where dataset_id=(select dataset_id from granule g where g.granule_id=gcb.granule_id) AND element_id= gcb.element_id ) ,gcb.value, units
from granule_real_bk gcb;



/*
 GRANULE_SPATIAL
*/
alter table GRANULE_SPATIAL add( "DE_ID" NUMBER);
ALTER TABLE GRANULE_SPATIAL DROP CONSTRAINT "GRANULE_SPATIAL_FK2";
update GRANULE_SPATIAL gc set de_id=(select de_id from dataset_element where dataset_element.dataset_id in (select dataset_id from granule where granule_id = gc.granule_id) AND gc.element_id= dataset_element.element_id);
ALTER TABLE GRANULE_SPATIAL DROP COLUMN "ELEMENT_ID";

/* 
  DROP OLD INDEXES
*/
DROP INDEX gd_gi_idx;
DROP INDEX gi_gi_idx;
DROP INDEX gi_ei_idx;
DROP INDEX greal_gi_idx;
DROP INDEX gr_ei_idx;
DROP INDEX gref_gi_idx;
DROP INDEX gs_gi_idx;

/*
  RE-ADD INDEXES
*/
CREATE INDEX gd_gi_idx ON granule_datetime(granule_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

CREATE INDEX gi_gi_idx ON granule_integer(granule_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

CREATE INDEX gi_ei_idx ON granule_integer(de_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

CREATE INDEX greal_gi_idx ON granule_real(granule_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);
  
CREATE INDEX gr_ei_idx ON granule_real(de_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

CREATE INDEX gref_gi_idx ON granule_reference(granule_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

CREATE INDEX gs_gi_idx ON granule_spatial(granule_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);
  
CREATE INDEX gs_ei_idx ON granule_spatial(de_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);


/*
DATASET CROSSTRACK STUFF
*/

alter table DATASET add(
	"ACROSS_TRACK_RESOLUTION" NUMBER,
	"ALONG_TRACK_RESOLUTION" NUMBER,
	"ASCENDING_NODE_TIME" VARCHAR2(80)
);

ALTER TABLE COLLECTION_LEGACY_PRODUCT
ADD CONSTRAINT COLLECTION_LEGACY_PRODUCT_FK1 
FOREIGN KEY (COLLECTION_ID)  
REFERENCES COLLECTION;

