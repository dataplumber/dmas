/*
GHRSST FIXES
*/

alter table collection_dataset add(granule_range_360 CHAR(1)  default 'N' NOT NULL);
update collection_dataset set granule_range_360='Y' where dataset_id in(select dataset.dataset_id from dataset, dataset_coverage where dataset.dataset_id=dataset_coverage.dataset_id and ((east_lon > 180 or west_lon < -180) or (north_lat>90 or south_lat<-90)));

update sensor set long_name = 'Geodetic and Earth Orbiting Satellite 3' where short_name='GEOS-3 ALTIMETER';
update sensor set long_name = 'Radio Detection and Ranging Altimeter' where short_name='RADAR ALTIMETER';

update dataset_policy set data_class='ROLLING-STORE' where data_class='ROLLING_STORE';

update source set type='spacecraft' where short_name='GEOS-3';
update source set type='spacecraft' where short_name='TOPEX/POSEIDON';
update source set type='spacecraft' where short_name='ERS-1';
update source set type='spacecraft' where short_name='ERS-2';
update source set type='spacecraft' where short_name='SEASAT';
update source set type='spacecraft' where short_name='GEOSAT';
update source set type='bouy, spacecraft' where short_name='NDBC MOORED BUOY';
update source set type='computer model' where short_name='ECMWF';
update source set type='vessel' where short_name='SHIP';
update source set type='spacecraft' where short_name='GFO';
update source set type='spacecraft' where short_name='ENVISAT';
update source set type='instrument' where short_name='InSitu';
update source set type='multiple' where short_name='Multiple';
update source set type='spacecraft' where short_name='JASON-1';
update source set type='spacecraft' where short_name='SEASAT 1';


update sensor set long_name='Along Track Scanning Radiometer 2' where short_name='ATSR-2';
update sensor set long_name='Advanced Very High Resolution Radiometer' where short_name='AVHRR';
update sensor set long_name='SeaWinds Scatterometer' where short_name='SEAWINDS';
update sensor set long_name='Active Microwave Instrument' where short_name='AMI';
update sensor set long_name='Advanced Very High Resolution Radiometer' where short_name='AVHRR';
update sensor set long_name='Seasat-A Scatterometer System' where short_name='SASS';
update sensor set long_name='NASA Scatterometer' where short_name='NSCAT';
update sensor set long_name='Advanced Very High Resolution Radiometer' where short_name='AVHRR';
update sensor set long_name='Scanning Multichannel Microwave Radiometer' where short_name='SMMR';
update sensor set long_name='Special Sensor Microwave Imager' where short_name='SSMI';
update sensor set long_name='Conductivity Temperature Depth Recorder' where short_name='CTD RECORDER';

/*
ASCAT CHANGES
*/
update dataset_policy set access_constraint='none', use_constraint='All users publishing with PO.DAAC data from our providers and partners, please add a citation as outlined here: http://podaac.jpl.nasa.gov/WEB_INFO/citations.html. This product is intended for operational use only and has not been thoroughly evaluated for scientific applications. Please use with caution.', data_volume=2 where dataset_id in (select dataset_id from dataset where short_name like '%ASCAT%');

UPDATE collection_product set product_id=315  where collection_id in (select collection_id from collection where short_name='ASCAT-L2-25km');
UPDATE collection_product set product_id=316  where collection_id in (select collection_id from collection where short_name='ASCAT-L2-12.5km');

/*
   change granule_archive and granule to bk names
*/

alter table granule rename to granule_bk;
alter table granule_archive rename to granule_archive_bk;

ALTER TABLE granule_bk RENAME CONSTRAINT granule_pk TO granule_bk_pk;
ALTER INDEX granule_pk RENAME TO granule_bk_pk;

/*
** granule
*/
CREATE TABLE granule
(
  granule_id            NUMBER          NOT NULL,
  dataset_id            NUMBER          NOT NULL,
  name                  VARCHAR2(120)   NOT NULL,
  start_time_long NUMBER  NOT NULL               ,
  stop_time_long NUMBER                 ,
  create_time_long           NUMBER          NOT NULL,
  ingest_time_long NUMBER           NOT NULL,
  archive_time_long NUMBER                   ,
  verify_time_long NUMBER                    ,
  acquired_time_long NUMBER                  ,
  requested_time_long NUMBER                ,
  version               NUMBER          NOT NULL,
  access_type           VARCHAR2(20)    NOT NULL,
  data_format           VARCHAR2(20)    NOT NULL,
  compress_type         VARCHAR2(20)    NOT NULL,
  checksum_type         VARCHAR2(20)    NOT NULL,
  status                VARCHAR2(10)    NOT NULL,
  root_path             VARCHAR2(765)    ,
  rel_path		VARCHAR2(8)     ,
  CONSTRAINT granule_pk PRIMARY KEY (granule_id)
);

INSERT INTO granule 
select UNIQUE granule_bk.granule_id, dataset_id, granule_bk.name, inventory.timestampToLong(start_time),  inventory.timestampToLong(stop_time),  inventory.timestampToLong(create_time),  inventory.timestampToLong(ingest_time),  inventory.timestampToLong(archive_time),  inventory.timestampToLong(verify_time),0, 0,  version, access_type, data_format, compress_type, checksum_type, granule_bk.status, SUBSTR(path,1,INSTR(path,'/', -1, 3)-1), SUBSTR(path,INSTR(path,'/', -1, 3)+1,8) from granule_bk, granule_archive_bk where  granule_bk.granule_id < 2185832 AND granule_bk.granule_id=granule_archive_bk.granule_id ORDER BY granule_id;

INSERT INTO granule 
select UNIQUE granule_bk.granule_id, dataset_id, granule_bk.name, inventory.PSTTimestampToLong(start_time),  inventory.PSTTimestampToLong(stop_time),  inventory.PSTTimestampToLong(create_time),  inventory.PSTTimestampToLong(ingest_time),  inventory.PSTTimestampToLong(archive_time),  inventory.PSTTimestampToLong(verify_time),null,null,  version, access_type, data_format, compress_type, checksum_type, granule_bk.status, SUBSTR(path,1,INSTR(path,'/', -1, 3)-1), SUBSTR(path,INSTR(path,'/', -1, 3)+1,8) from granule_bk, granule_archive_bk where  granule_bk.granule_id >= 2185832 AND granule_bk.granule_id in (select granule_id from granule_bk where dataset_id in (select dataset_id from collection_dataset, collection where collection.collection_id = collection_dataset.collection_id and short_name = 'GHRSST')) AND granule_bk.granule_id=granule_archive_bk.granule_id ORDER BY granule_id;

INSERT INTO granule
select UNIQUE granule_bk.granule_id, dataset_id, granule_bk.name, inventory.timestampToLong(start_time),  inventory.timestampToLong(stop_time),  inventory.timestampToLong(create_time),  inventory.timestampToLong(ingest_time),  inventory.timestampToLong(archive_time),  inventory.timestampToLong(verify_time),null, null,  version, access_type, data_format, compress_type, checksum_type, granule_bk.status, SUBSTR(path,1,INSTR(path,'/', -1, 3)-1), SUBSTR(path,INSTR(path,'/', -1, 3)+1,8) from granule_bk, granule_archive_bk where  granule_bk.granule_id >= 2185832 AND granule_bk.granule_id not in (select granule_id from granule_bk where dataset_id in (select dataset_id from collection_dataset, collection where collection.collection_id = collection_dataset.collection_id and short_name = 'GHRSST')) AND granule_bk.granule_id=granule_archive_bk.granule_id ORDER BY granule_id;

INSERT INTO granule
select UNIQUE granule_bk.granule_id, dataset_id, granule_bk.name, inventory.timestampToLong(start_time),  inventory.timestampToLong(stop_time),  inventory.timestampToLong(create_time),  inventory.timestampToLong(ingest_time),  inventory.timestampToLong(archive_time),  inventory.timestampToLong(verify_time),null, null,  version, access_type, data_format, compress_type, checksum_type, granule_bk.status, null,null from granule_bk where granule_id not in (select granule_id from granule_archive_bk)  ORDER BY granule_id;


/*
** granule_archive
*/
CREATE TABLE granule_archive
(
  granule_id            NUMBER          NOT NULL,
  type                  VARCHAR2(20)    NOT NULL,
  file_size             NUMBER          NOT NULL,
  compress_flag         CHAR(1)         NOT NULL,
  checksum              CHAR(32)                ,
  name                  VARCHAR2(250)           ,
  status                VARCHAR2(10)    NOT NULL
);

INSERT INTO granule_archive 
select granule_id, type, file_size, compress_flag, checksum, SUBSTR(path,INSTR(path,'/', -1, 1)+1), status from granule_archive_bk;

/*
** granule datetime only entries in here should be from newly ingested data.
*/
ALTER TABLE granule_datetime add
(
	value_long NUMBER 
);


update granule_datetime set value_long = inventory.PSTTimestampToLong(value) where granule_id >= 2185832 and granule_id in (select granule_id from granule_bk where dataset_id in (select dataset_id from collection_dataset, collection where collection.collection_id = collection_dataset.collection_id and short_name = 'GHRSST'));

update granule_datetime set value_long = inventory.timestampToLong(value) where granule_id >= 2185832 and granule_id not in (select granule_id from granule_bk where dataset_id in (select dataset_id from collection_dataset, collection where collection.collection_id = collection_dataset.collection_id and short_name = 'GHRSST'));

update granule_datetime set value_long = inventory.TimestampToLong(value) where granule_id < 2185832;


/*
** granule meta_history
*/
ALTER TABLE granule_meta_history add
(
	creation_date_long    NUMBER default 0    NOT NULL,
  	last_revision_date_long    NUMBER  default 0          NOT NULL,
  	echo_submit_date_long NUMBER                  
);

update granule_meta_history set creation_date_long = inventory.PSTTimestampToLong(creation_date), last_revision_date_long= inventory.PSTTimestampToLong(last_revision_date), echo_submit_date_long = inventory.PSTTimestampToLong(echo_submit_date) where granule_id >= 2185832 and granule_id in (select granule_id from granule_bk where dataset_id in (select dataset_id from collection_dataset, collection where collection.collection_id = collection_dataset.collection_id and short_name = 'GHRSST'));

update granule_meta_history set creation_date_long = inventory.timestampToLong(creation_date), last_revision_date_long= inventory.timestampToLong(last_revision_date), echo_submit_date_long = inventory.timestampToLong(echo_submit_date) where granule_id >= 2185832 and granule_id not in (select granule_id from granule_bk where dataset_id in (select dataset_id from collection_dataset, collection where collection.collection_id = collection_dataset.collection_id and short_name = 'GHRSST'));

update granule_meta_history set creation_date_long = inventory.TimestampToLong(creation_date), last_revision_date_long= inventory.TimestampToLong(last_revision_date), echo_submit_date_long = inventory.TimestampToLong(echo_submit_date) where granule_id < 2185832;


/*
** collection_element
*/
ALTER TABLE collection_element add
(
  	datatime_value_long NUMBER                 
);

update collection_element set datatime_value_long= inventory.timestampToLong(datatime_value);


/*
** collection_product
*/
ALTER TABLE collection_product add
(
  echo_submit_date_long NUMBER ,
  gcmd_submit_date_long NUMBER
);

update collection_product set echo_submit_date_long= inventory.timestampToLong(echo_submit_date);
update collection_product set gcmd_submit_date_long= inventory.timestampToLong(gcmd_submit_date);

/*
** dataset_coverage
*/
ALTER TABLE dataset_coverage add
(
  start_time_long NUMBER,
  stop_time_long NUMBER           
);

update dataset_coverage set start_time_long= inventory.timestampToLong(start_time);
update dataset_coverage set stop_time_long= inventory.timestampToLong(stop_time);

/*
** dataset_citation
*/
ALTER TABLE dataset_citation add
(
  release_date_long NUMBER
      
);

update dataset_citation set release_date_long= inventory.timestampToLong(release_date);

/*
** dataset_meta_history
*/
ALTER TABLE dataset_meta_history add
(
  creation_date_long NUMBER,
  last_revision_date_long NUMBER           
);

update dataset_meta_history set creation_date_long= inventory.timestampToLong(creation_date);
update dataset_meta_history set last_revision_date_long= inventory.timestampToLong(last_revision_date);

/*
** dataset_software
*/
ALTER TABLE dataset_software add
(
  release_date_long NUMBER
);

update dataset_software set release_date_long= inventory.timestampToLong(release_date);


/*
** dataset_version
*/
ALTER TABLE dataset_version add
(
  version_date_long NUMBER
);

update dataset_version set version_date_long= inventory.timestampToLong(version_date);



/*
   remove foreign keys to granule_bk (or change them?)
*/

ALTER TABLE granule_bk drop constraint granule_fk1;
ALTER TABLE granule_archive_bk drop constraint granule_archive_fk1;
ALTER TABLE granule_character DROP CONSTRAINT granule_character_fk1;

ALTER TABLE granule_contact DROP CONSTRAINT granule_contact_fk1;

ALTER TABLE granule_datetime DROP CONSTRAINT granule_datetime_fk1;

ALTER TABLE granule_element DROP CONSTRAINT granule_element_fk1;

ALTER TABLE granule_integer DROP CONSTRAINT granule_integer_fk1;

ALTER TABLE granule_meta_history DROP CONSTRAINT granule_meta_history_fk1;

ALTER TABLE granule_real DROP CONSTRAINT granule_real_fk1;

ALTER TABLE granule_reference DROP CONSTRAINT granule_reference_fk1;

ALTER TABLE granule_sip DROP CONSTRAINT granule_sip_fk1;

ALTER TABLE granule_spatial DROP CONSTRAINT granule_spatial_fk1;

/*
   create indexs on granule and granule_archive
*/

CREATE INDEX g2_n_idx ON granule(name)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

CREATE INDEX g2_s_idx ON granule(status)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

CREATE INDEX g2_di_idx ON granule(dataset_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

CREATE INDEX ga2_gi_idx on granule_archive(granule_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

/*
   recreate constraints to granule
*/

/*
** granule
*/
ALTER TABLE granule
  ADD CONSTRAINT granule_fk1
  FOREIGN KEY (dataset_id)
  REFERENCES dataset;

delete from granule_archive where granule_id not in (select granule_id from granule);

/*
** granule_archive
*/
ALTER TABLE granule_archive
  ADD CONSTRAINT granule_archive_fk1
  FOREIGN KEY (granule_id)
  REFERENCES granule;

delete from granule_character where granule_id not in (select granule_id from granule);
/*
** granule_character
*/
ALTER TABLE granule_character
  ADD CONSTRAINT granule_character_fk1
  FOREIGN KEY (granule_id)
  REFERENCES granule;


delete from granule_contact where granule_id not in (select granule_id from granule);
/*
** granule_contact
*/
ALTER TABLE granule_contact
  ADD CONSTRAINT granule_contact_fk1
  FOREIGN KEY (granule_id)
  REFERENCES granule;

delete from granule_datetime where granule_id not in (select granule_id from granule);
/*
** granule_datetime
*/
ALTER TABLE granule_datetime
  ADD CONSTRAINT granule_datetime_fk1
  FOREIGN KEY (granule_id)
  REFERENCES granule;

/*
** granule_element
*/
ALTER TABLE granule_element
  ADD CONSTRAINT granule_element_fk1
  FOREIGN KEY (dataset_id)
  REFERENCES dataset;

delete from granule_integer where granule_id not in (select granule_id from granule);
/*
** granule_integer
*/
ALTER TABLE granule_integer
  ADD CONSTRAINT granule_integer_fk1
  FOREIGN KEY (granule_id)
  REFERENCES granule;

delete from granule_meta_history where granule_id not in (select granule_id from granule);
/*
** granule_meta_history
*/
ALTER TABLE granule_meta_history
  ADD CONSTRAINT granule_meta_history_fk1
  FOREIGN KEY (granule_id)
  REFERENCES granule
  ON DELETE CASCADE;

delete from granule_real where granule_id not in (select granule_id from granule);
delete from granule_reference where granule_id not in (select granule_id from granule);
delete from granule_sip where granule_id not in (select granule_id from granule);
/*
** granule_real
*/
ALTER TABLE granule_real
  ADD CONSTRAINT granule_real_fk1
  FOREIGN KEY (granule_id)
  REFERENCES granule;

/*
** granule_reference
*/
ALTER TABLE granule_reference
  ADD CONSTRAINT granule_reference_fk1
  FOREIGN KEY (granule_id)
  REFERENCES granule;

/*
** granule_sip
*/
ALTER TABLE granule_sip
  ADD CONSTRAINT granule_sip_fk1
  FOREIGN KEY (granule_id)
  REFERENCES granule;

/*
** granule_spatial
*/
ALTER TABLE granule_spatial
  ADD CONSTRAINT granule_spatial_fk1
  FOREIGN KEY (granule_id)
  REFERENCES granule;




ALTER TABLE "COLLECTION"
   ADD(full_description CLOB);

CREATE TABLE collection_legacy_product
(
  collection_id      NUMBER          NOT NULL,
  legacy_product_id  NUMBER          NOT NULL,

  CONSTRAINT collection_legacy_product_pk PRIMARY KEY (collection_id, legacy_product_id)
);


ALTER TABLE collection_legacy_product
   ADD CONSTRAINT collection_legacy_product_fk1
   FOREIGN KEY (collection_id)
   REFERENCES collection;

insert into collection_legacy_product select collection_id, TO_NUMBER(product_id) from collection_product where product_id is not null;


ALTER TABLE collection_product
   add(Product_ID_tmp VARCHAR2(20));

UPDATE collection_product SET Product_ID_tmp = PRODUCT_ID;

ALTER TABLE collection_product
   drop column "PRODUCT_ID";

ALTER TABLE collection_product rename column Product_ID_tmp  to Product_ID;


/*
ADD DATASET DD tables
*/

alter table granule_element_dd rename to element_dd;

/*
** dataset_character
*/
CREATE TABLE dataset_character
(
  dataset_id            NUMBER          NOT NULL,
  element_id            NUMBER          NOT NULL,
  value                 VARCHAR2(1024)
);
/*
** dataset_character
*/
ALTER TABLE dataset_character
  ADD CONSTRAINT dataset_character_fk1
  FOREIGN KEY (dataset_id)
  REFERENCES dataset;

ALTER TABLE dataset_character
  ADD CONSTRAINT dataset_character_fk2
  FOREIGN KEY (element_id)
  REFERENCES element_dd;


/*
** dataset_datetime
*/
CREATE TABLE dataset_datetime
(
  dataset_id            NUMBER          NOT NULL,
  element_id            NUMBER          NOT NULL,
  value_long                 NUMBER
);

/*
** dataset_datetime
*/
ALTER TABLE dataset_datetime
  ADD CONSTRAINT dataset_datetime_fk1
  FOREIGN KEY (dataset_id)
  REFERENCES dataset;

ALTER TABLE dataset_datetime
  ADD CONSTRAINT dataset_datetime_fk2
  FOREIGN KEY (element_id)
  REFERENCES element_dd;

/*
** dataset_integer
*/
CREATE TABLE dataset_integer
(
  dataset_id            NUMBER          NOT NULL,
  element_id            NUMBER          NOT NULL,
  value                 NUMBER
);

/*
** dataset_integer
*/
ALTER TABLE dataset_integer
  ADD CONSTRAINT dataset_integer_fk1
  FOREIGN KEY (dataset_id)
  REFERENCES dataset;

ALTER TABLE dataset_integer
  ADD CONSTRAINT dataset_integer_fk2
  FOREIGN KEY (element_id)
  REFERENCES element_dd;

/*set_real
*/
CREATE TABLE dataset_real
(
  dataset_id            NUMBER          NOT NULL,
  element_id            NUMBER          NOT NULL,
  value                 NUMBER
);

 /*
** dataset_real
*/
ALTER TABLE dataset_real
  ADD CONSTRAINT dataset_real_fk1
  FOREIGN KEY (dataset_id)
  REFERENCES dataset;

ALTER TABLE dataset_real
  ADD CONSTRAINT dataset_real_fk2
  FOREIGN KEY (element_id)
  REFERENCES element_dd;

/*
** dataset_element
*/
CREATE TABLE dataset_element
(
  dataset_id            NUMBER          NOT NULL,
  element_id            NUMBER          NOT NULL,
  obligation_flag       CHAR(1)         NOT NULL,
  CONSTRAINT dataset_element_pk PRIMARY KEY (dataset_id, element_id)
);

/*
** dataset_element
*/
ALTER TABLE dataset_element
  ADD CONSTRAINT dataset_element_fk1
  FOREIGN KEY (dataset_id)
  REFERENCES dataset;

ALTER TABLE dataset_element
  ADD CONSTRAINT dataset_element_fk2
  FOREIGN KEY (element_id)
  REFERENCES element_dd;


/**
* New fields for dmas 2.0
*/
INSERT INTO element_dd (element_id, short_name, long_name, type, db_table, description) VALUES (element_id_granule_seq.nextval,'start_time','start time','date','date','Start time of a granule or dataset');
INSERT INTO element_dd (element_id, short_name, long_name, type, db_table, description) VALUES (element_id_granule_seq.nextval,'stop_time','stop time','date','date','Stop time of a granule or dataset');

INSERT INTO element_dd (element_id, short_name, long_name, type, db_table, description) VALUES (element_id_granule_seq.nextval,'region_detail','region detail','character','character','More specific names not specified in the GCMD valids list.');
INSERT INTO element_dd (element_id, short_name, long_name, type, db_table, description) VALUES (element_id_granule_seq.nextval,'latitude_resolution','latitude resolution','character','character','The resolution for the North and South latitude values in km.');
INSERT INTO element_dd (element_id, short_name, long_name, type, db_table, description) VALUES (element_id_granule_seq.nextval,'longitude_resolution','longitude resolution','character','character','The resolution for the East and West longitude values in km.');
INSERT INTO element_dd (element_id, short_name, long_name, type, db_table, description) VALUES (element_id_granule_seq.nextval,'horizontal_resolution_range','horizontal resolution range','character','character','The range of resolutions for the latitude/longitude values.');
INSERT INTO element_dd (element_id, short_name, long_name, type, db_table, description) VALUES (element_id_granule_seq.nextval,'altitude_resolution','altitude resolution','character','character','The resolution for altitude as measured from mean sea level in meters.');
INSERT INTO element_dd (element_id, short_name, long_name, type, db_table, description) VALUES (element_id_granule_seq.nextval,'depth_resolution','depth resolution','character','character','The resolution for depth as measured from mean sea level in meters.');
INSERT INTO element_dd (element_id, short_name, long_name, type, db_table, description) VALUES (element_id_granule_seq.nextval,'temporal_resolution','temporal resolution','character','character','The frequency that the data is sampled.');
INSERT INTO element_dd (element_id, short_name, long_name, type, db_table, description) VALUES (element_id_granule_seq.nextval,'temporal_resolution_range','temporal resolution range','character','character','The range of frequencies within which the data is sampled.');
INSERT INTO element_dd (element_id, short_name, long_name, type, db_table, description) VALUES (element_id_granule_seq.nextval,'ellipsoid_type','ellipsoid type','character','character','The type of ellipsoid.');
INSERT INTO element_dd (element_id, short_name, long_name, type, db_table, description) VALUES (element_id_granule_seq.nextval,'projection_type','projection type','character','character','The type of projection.');
INSERT INTO element_dd (element_id, short_name, long_name, type, db_table, description) VALUES (element_id_granule_seq.nextval,'projection_detail','projection detail','character','character','Free text description of details such as the projections standard parallels, etc., if necessary to fully specify the projection.');
INSERT INTO element_dd (element_id, short_name, long_name, type, db_table, description) VALUES (element_id_granule_seq.nextval,'reference','reference','character','character','Key bibilographic references pertaining to the dataset.');

/*
*End new fields for DMAS 2.0
*/


/*
* This populates the 'M'andatory dataset element
*/

INSERT into dataset_element (dataset_id,element_id, obligation_flag)  (select dataset_id, element_id, 'M' from dataset, element_dd where element_dd.short_name in ('start_time', 'stop_time', 'westLongitude','eastLongitude','northLatitude','southLatitude','minAltitude','maxAltitude','minDepth','maxDepth', 'region', 'latitude_resolution', 'longitude_resolution', 'temporal_resolution','ellipsoid_type','projection_type','projection_detail','reference', 'region_detail', 'horizontal_resolution_range', 'altitude_resolution', 'depth_resolution', 'temporal_resolution_range'));


INSERT into dataset_real (dataset_id,element_id,value) select dataset.dataset_id,(select element_id from element_dd where short_name='westLongitude') ,  west_lon from dataset LEFT OUTER JOIN dataset_coverage ON dataset.dataset_id=dataset_coverage.dataset_id;
INSERT into dataset_real (dataset_id,element_id,value) select dataset.dataset_id,(select element_id from element_dd where short_name='eastLongitude') ,  east_lon from dataset LEFT OUTER JOIN dataset_coverage ON dataset.dataset_id=dataset_coverage.dataset_id;
INSERT into dataset_real (dataset_id,element_id,value) select dataset.dataset_id,(select element_id from element_dd where short_name='northLatitude') ,  north_lat from dataset LEFT OUTER JOIN dataset_coverage ON dataset.dataset_id=dataset_coverage.dataset_id;
INSERT into dataset_real (dataset_id,element_id,value) select dataset.dataset_id,(select element_id from element_dd where short_name='southLatitude') ,  south_lat from dataset LEFT OUTER JOIN dataset_coverage ON dataset.dataset_id=dataset_coverage.dataset_id;

INSERT into dataset_datetime (dataset_id,element_id,value_long) select dataset.dataset_id,(select element_id from element_dd where short_name='start_time') ,  inventory.timestampToLong(start_time) from dataset LEFT OUTER JOIN dataset_coverage ON dataset.dataset_id=dataset_coverage.dataset_id;
INSERT into dataset_datetime (dataset_id,element_id,value_long) select dataset.dataset_id,(select element_id from element_dd where short_name='stop_time') ,  inventory.timestampToLong(stop_time) from dataset LEFT OUTER JOIN dataset_coverage ON dataset.dataset_id=dataset_coverage.dataset_id;

INSERT into dataset_real (dataset_id,element_id,value) select dataset.dataset_id,(select element_id from element_dd where short_name='minAltitude') ,  min_altitude from dataset LEFT OUTER JOIN dataset_coverage ON dataset.dataset_id=dataset_coverage.dataset_id;
INSERT into dataset_real (dataset_id,element_id,value) select dataset.dataset_id,(select element_id from element_dd where short_name='minDepth') ,  min_depth from dataset LEFT OUTER JOIN dataset_coverage ON dataset.dataset_id=dataset_coverage.dataset_id;
INSERT into dataset_real (dataset_id,element_id,value) select dataset.dataset_id,(select element_id from element_dd where short_name='maxAltitude') ,  max_altitude from dataset LEFT OUTER JOIN dataset_coverage ON dataset.dataset_id=dataset_coverage.dataset_id;
INSERT into dataset_real (dataset_id,element_id,value) select dataset.dataset_id,(select element_id from element_dd where short_name='maxDepth') ,  max_depth from dataset LEFT OUTER JOIN dataset_coverage ON dataset.dataset_id=dataset_coverage.dataset_id;

INSERT into dataset_character (dataset_id,element_id,value) select dataset.dataset_id,(select element_id from element_dd where short_name='region') ,region from dataset;
INSERT into dataset_character (dataset_id,element_id,value) select dataset.dataset_id,(select element_id from element_dd where short_name='latitude_resolution') ,latitude_resolution from dataset;
INSERT into dataset_character (dataset_id,element_id,value) select dataset.dataset_id,(select element_id from element_dd where short_name='longitude_resolution') , longitude_resolution from dataset;
INSERT into dataset_character (dataset_id,element_id,value) select dataset.dataset_id,(select element_id from element_dd where short_name='temporal_resolution') ,temporal_resolution from dataset;
INSERT into dataset_character (dataset_id,element_id,value) select dataset.dataset_id,(select element_id from element_dd where short_name='ellipsoid_type') ,ellipsoid_type from dataset;
INSERT into dataset_character (dataset_id,element_id,value) select dataset.dataset_id,(select element_id from element_dd where short_name='projection_type') ,projection_type from dataset;
INSERT into dataset_character (dataset_id,element_id,value) select dataset.dataset_id,(select element_id from element_dd where short_name='projection_detail') ,projection_detail from dataset;
INSERT into dataset_character (dataset_id,element_id,value) select dataset.dataset_id,(select element_id from element_dd where short_name='reference') , reference from dataset;

/*End Required DDs */

INSERT into dataset_character (dataset_id,element_id,value) select dataset.dataset_id,(select element_id from element_dd where short_name='region_detail') ,region_detail from dataset;
INSERT into dataset_character (dataset_id,element_id,value) select dataset.dataset_id,(select element_id from element_dd where short_name='horizontal_resolution_range') ,horizontal_resolution_range from dataset;
INSERT into dataset_character (dataset_id,element_id,value) select dataset.dataset_id,(select element_id from element_dd where short_name='altitude_resolution') ,altitude_resolution from dataset;
INSERT into dataset_character (dataset_id,element_id,value) select dataset.dataset_id,(select element_id from element_dd where short_name='depth_resolution') ,depth_resolution from dataset;
INSERT into dataset_character (dataset_id,element_id,value) select dataset.dataset_id,(select element_id from element_dd where short_name='temporal_resolution_range') ,temporal_resolution_range from dataset;

ALTER TABLE dataset_coverage drop( start_time, stop_time);
ALTER TABLE dataset_citation drop( release_date);
ALTER TABLE dataset_meta_history drop( creation_date, last_revision_date);
ALTER TABLE dataset_software drop( release_date);
ALTER TABLE dataset_version drop(version_date);

ALTER TABLE collection_element drop(datatime_value);
ALTER TABLE collection_product drop(gcmd_submit_date, echo_submit_date);

ALTER TABLE granule_datetime drop (value);
ALTER TABLE granule_meta_history drop (creation_date, last_revision_date, echo_submit_date);

ALTER TABLE granule_real add(units VARCHAR2(10));
ALTER TABLE granule_integer add(units VARCHAR2(10));

ALTER TABLE dataset_real add(units VARCHAR2(10));
ALTER TABLE dataset_integer add(units VARCHAR2(10));

insert into collection_product (collection_id, visible_flag, product_id) VALUES (1,'N','0');

COMMIT;
