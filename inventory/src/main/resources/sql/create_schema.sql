/*
** Copyright (c) 2007-2008, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: create_schema.sql 13817 2015-01-29 18:22:24Z gangl $
*/

/*
** This SQL script creates the database objects for the Inventory schema.
*/

/*
** Create Tables
*/

/*
** collection
*/
CREATE TABLE collection
(
  collection_id         NUMBER          NOT NULL,
  short_name            VARCHAR2(80)    NOT NULL,
  long_name             VARCHAR2(1024)          ,
  type                  VARCHAR2(20)    NOT NULL,
  description           VARCHAR2(4000)          ,
  full_description	CLOB			,
  aggregate   		CHAR(1) 	DEFAULT 'N',
  CONSTRAINT collection_pk PRIMARY KEY (collection_id)
);

/*
** collection_contact
*/
CREATE TABLE collection_contact
(
  collection_id         NUMBER          NOT NULL,
  contact_id            NUMBER          NOT NULL,
  CONSTRAINT collection_contact_pk PRIMARY KEY (collection_id, contact_id)
);

/*
** collection_dataset
*/
CREATE TABLE collection_dataset
(
  collection_id         NUMBER          NOT NULL,
  dataset_id            NUMBER          NOT NULL,
  granule_flag          CHAR(1)         NOT NULL,
  granule_range_360	CHAR(1)		default 'N' NOT NULL,		
  start_granule_id      NUMBER                  ,
  stop_granule_id       NUMBER                  ,
  CONSTRAINT collection_dataset_pk PRIMARY KEY (collection_id, dataset_id)
);

/*
** collection_element
*/
CREATE TABLE collection_element
(
  collection_id         NUMBER          NOT NULL,
  element_id            NUMBER          NOT NULL,
  character_value       VARCHAR2(255)           ,
  datatime_value_long   NUMBER	        ,
  integer_value         NUMBER                  ,
  real_value            NUMBER    
);
 
/*
** collection_element_dd
*/
CREATE TABLE collection_element_dd
(
  element_id            NUMBER          NOT NULL,
  short_name            VARCHAR2(30)    NOT NULL,
  long_name             VARCHAR2(80)            ,
  type                  VARCHAR2(20)    NOT NULL,
  description           VARCHAR2(1024)          ,
  CONSTRAINT collection_element_dd_pk PRIMARY KEY (element_id)  
);

CREATE TABLE collection_legacy_product
(
  collection_id      NUMBER          NOT NULL,
  legacy_product_id  NUMBER          NOT NULL,

  CONSTRAINT collection_legacy_product_pk PRIMARY KEY (collection_id, legacy_product_id)
);

/*
** collection_product
*/
CREATE TABLE collection_product
(
  collection_id         NUMBER          NOT NULL,
  visible_flag          CHAR(1)         NOT NULL,
  product_id            VARCHAR2(20)             ,
  echo_submit_date_long NUMBER         ,
  gcmd_submit_date_long NUMBER           ,
  CONSTRAINT collection_product_pk PRIMARY KEY (collection_id)
);

/*
** contact
*/
CREATE TABLE contact
(
  contact_id            NUMBER          NOT NULL,
  role                  VARCHAR2(40)    NOT NULL,
  first_name            VARCHAR2(80)    NOT NULL,
  middle_name           VARCHAR2(80)            ,
  last_name             VARCHAR2(80)    NOT NULL,
  email                 VARCHAR2(255)   NOT NULL,
  phone                 VARCHAR2(80)            ,
  fax                   VARCHAR2(80)            ,
  address               VARCHAR2(512)           ,
  provider_id           NUMBER                  ,
  notify_type           VARCHAR2(20)            ,
  CONSTRAINT contact_pk PRIMARY KEY (contact_id)
);

/*
** dataset
*/
CREATE TABLE dataset
(
  dataset_id            NUMBER          NOT NULL,
  persistent_id		VARCHAR2(24)		,
  provider_id           NUMBER          NOT NULL,
  short_name            VARCHAR2(160)   NOT NULL,
  long_name             VARCHAR2(255)   NOT NULL,
  original_provider     VARCHAR2(160)           ,
  provider_dataset_name VARCHAR2(160)           ,
  processing_level      VARCHAR2(10)            ,
  latitude_resolution   FLOAT    ,
  longitude_resolution  FLOAT    ,
  across_track_resolution	NUMBER		,
  along_track_resolution	NUMBER		,
  ascending_node_time 	VARCHAR2(80)		,
  horizontal_resolution_range  VARCHAR2(80)     ,
  altitude_resolution   VARCHAR2(80)            ,
  depth_resolution      VARCHAR2(80)            ,
  temporal_resolution   VARCHAR2(1024)          ,
  temporal_resolution_range    VARCHAR2(80)     ,
  ellipsoid_type        VARCHAR2(160)           ,
  projection_type       VARCHAR2(160)           ,
  projection_detail     VARCHAR2(1024)          ,
  reference             VARCHAR2(1024)          ,
  description           CLOB                    ,
  remote_dataset             CHAR(1)         DEFAULT 'L',
  metadata				VARCHAR(1024)			,
  sample_frequency	NUMBER,
  swath_width		NUMBER,
  temporal_repeat          VARCHAR2(1024)  NULL,
  temporal_repeat_min          VARCHAR2(1024)  NULL,
  temporal_repeat_max          VARCHAR2(1024)  NULL,
  note 			 	CLOB NULL,
  CONSTRAINT dataset_pk PRIMARY KEY (dataset_id)
);

/*
** dataset_character
*/
CREATE TABLE dataset_character
(
  dataset_id            NUMBER          NOT NULL,
  de_id            NUMBER          NOT NULL,
  value                 VARCHAR2(1024)
);

/*
** dataset_citation
*/
CREATE TABLE dataset_citation
(
  dataset_id            NUMBER          NOT NULL,
  title                 VARCHAR2(255)   NOT NULL,
  creator               VARCHAR2(255)   NOT NULL,
  version               VARCHAR2(80)    NOT NULL,
  publisher             VARCHAR2(160)           ,
  series_name           VARCHAR2(160)           ,
  release_date_long NUMBER            ,
  release_place         VARCHAR2(160)           ,
  citation_detail       VARCHAR2(255)           ,
  online_resource       VARCHAR2(255)               
);

/*
** dataset_contact
*/
CREATE TABLE dataset_contact
(
  dataset_id            NUMBER          NOT NULL,
  contact_id            NUMBER          NOT NULL,
  CONSTRAINT dataset_contact_pk PRIMARY KEY (dataset_id, contact_id)
);

/*
** dataset_coverage
*/
CREATE TABLE dataset_coverage
(
  dataset_id            NUMBER          NOT NULL,
  start_time_long       NUMBER       NOT NULL,
  stop_time_long NUMBER                 ,
  north_lat             NUMBER                  ,
  south_lat             NUMBER                  ,
  east_lon              NUMBER                  ,
  west_lon              NUMBER                  ,
  min_altitude          NUMBER                  ,
  max_altitude          NUMBER                  ,
  min_depth             NUMBER                  ,
  max_depth             NUMBER                  ,
  CONSTRAINT dataset_coverage_pk PRIMARY KEY (dataset_id)
);

/*
** dataset_datetime
*/
CREATE TABLE dataset_datetime
(
  dataset_id            NUMBER          NOT NULL,
  de_id            NUMBER          NOT NULL,
  value_long NUMBER 
);

/*
** dataset_element
*/
CREATE TABLE dataset_element
(
  de_id    NUMBER		NOT NULL,
  dataset_id            NUMBER          NOT NULL,
  element_id            NUMBER          NOT NULL,
  obligation_flag       CHAR(1)         NOT NULL,
  scope			VARCHAR2(20)     DEFAULT 'DATASET' NOT NULL,
  CONSTRAINT dataset_element_pk PRIMARY KEY (de_id)
);


/*
** dataset_integer
*/
CREATE TABLE dataset_integer
(
  dataset_id            NUMBER          NOT NULL,
  de_id            NUMBER          NOT NULL,
  value                 NUMBER,
  units					VARCHAR2(10)
);

/*
** dataset_location_policy
*/
CREATE TABLE dataset_location_policy
(
  dataset_id            NUMBER          NOT NULL,
  type                  VARCHAR2(20)    NOT NULL,
  base_path             VARCHAR2(255)   NOT NULL
);

/*
** dataset_meta_history
*/
CREATE TABLE dataset_meta_history
(
  dataset_id            NUMBER          NOT NULL,
  version_id            NUMBER          NOT NULL,
  creation_date_long         NUMBER            NOT NULL,
  last_revision_date_long    NUMBER            NOT NULL,
  revision_history      VARCHAR2(1024)  NOT NULL,
  CONSTRAINT dataset_meta_history_pk PRIMARY KEY (dataset_id, version_id)
);

/*
** dataset_parameter
*/
CREATE TABLE dataset_parameter
(
  dataset_id            NUMBER          NOT NULL,
  category              VARCHAR2(80)    NOT NULL,
  topic                 VARCHAR2(80)    NOT NULL,
  term                  VARCHAR2(80)    NOT NULL,
  variable              VARCHAR2(80)    NOT NULL,
  variable_detail       VARCHAR2(160)            
);

/*
** dataset_policy
*/
CREATE TABLE dataset_policy
(
  dataset_id            NUMBER          NOT NULL,
  data_class            VARCHAR2(20)    NOT NULL,
  data_frequency        NUMBER                  ,
  data_volume           NUMBER                  ,
  data_duration         NUMBER                  ,
  data_latency		NUMBER			,
  access_type           VARCHAR2(20)    NOT NULL,
  base_path_append_type VARCHAR2(20)    NOT NULL,
  data_format           VARCHAR2(20)    NOT NULL,
  compress_type         VARCHAR2(20)    NOT NULL,
  checksum_type         VARCHAR2(20)    NOT NULL,
  spatial_type          VARCHAR2(20)    NOT NULL,
  access_constraint     VARCHAR2(1024)  NOT NULL,
  use_constraint        VARCHAR2(1024)  NOT NULL,
  view_online 		VARCHAR2(1) 	DEFAULT 'Y',
  versioned 		VARCHAR2(1) 	DEFAULT 'N' NOT NULL,
  version_policy	VARCHAR(20)		,
CONSTRAINT dataset_policy_pk PRIMARY KEY (dataset_id)
);

CREATE TABLE DATASET_REGION
(
  DATASET_ID NUMBER NOT NULL,
  REGION VARCHAR2(128) NOT NULL,
  REGION_DETAIL VARCHAR2(1024)
);

ALTER TABLE DATASET_REGION
   ADD CONSTRAINT dataset_region_fk1
   FOREIGN KEY (dataset_id)
   REFERENCES DATASET;


/*
** dataset_project
*/
CREATE TABLE dataset_project
(      
  dataset_id            NUMBER          NOT NULL,
  project_id            NUMBER          NOT NULL,
  CONSTRAINT dataset_project_pk PRIMARY KEY (dataset_id, project_id)
);

/*
** dataset_real
*/
CREATE TABLE dataset_real
(
  dataset_id            NUMBER          NOT NULL,
  de_id            NUMBER          NOT NULL,
  value                 NUMBER,
  units					VARCHAR2(10)
);

/*
** dataset_resource
*/
CREATE TABLE dataset_resource
(
  dataset_id            NUMBER          NOT NULL,
  name                  VARCHAR2(80)            ,     
  path                  VARCHAR2(255)   NOT NULL,
  type                  VARCHAR2(31)            ,
  description           VARCHAR2(1024)           
);

/*
** dataset_source
*/
CREATE TABLE dataset_source
(      
  dataset_id            NUMBER          NOT NULL,
  source_id             NUMBER          NOT NULL,
  sensor_id             NUMBER          NOT NULL,
  CONSTRAINT dataset_source_pk PRIMARY KEY (dataset_id, source_id, sensor_id)
);

/*
** dataset_software
*/
CREATE TABLE dataset_software
(
  dataset_id            NUMBER          NOT NULL,
  name                  VARCHAR2(80)    NOT NULL,
  type                  VARCHAR2(31)    NOT NULL,
  path                  VARCHAR2(255)   NOT NULL,
  release_date_long NUMBER                      ,
  version               VARCHAR2(20)            ,
  language              VARCHAR2(20)            ,
  platform              VARCHAR2(20)            ,
  description           VARCHAR2(1024)              
);

/*
** dataset_version
*/
CREATE TABLE dataset_version
(
  dataset_id            NUMBER          NOT NULL,
  version_id            NUMBER          NOT NULL,
  version               VARCHAR2(80)    NOT NULL,
  version_date_long NUMBER             ,
  description           VARCHAR2(1024)          ,
  CONSTRAINT dataset_version_pk PRIMARY KEY (dataset_id, version_id)
);

/*
** granule
*/
CREATE TABLE granule
(
  granule_id            NUMBER          NOT NULL,
  dataset_id            NUMBER          NOT NULL,
  name                  VARCHAR2(120)   NOT NULL,
  official_name		VARCHAR2(120)		,
  start_time_long NUMBER  NOT NULL               ,
  stop_time_long NUMBER                 ,
  create_time_long           NUMBER          NOT NULL,
  ingest_time_long NUMBER           NOT NULL,
  archive_time_long NUMBER                   ,
  verify_time_long NUMBER                    ,
  acquired_time_long NUMBER                  ,
  requested_time_long NUMBER                 ,
  version               NUMBER          NOT NULL,
  access_type           VARCHAR2(20)    NOT NULL,
  data_format           VARCHAR2(20)    NOT NULL,
  compress_type         VARCHAR2(20)    NOT NULL,
  checksum_type         VARCHAR2(20)    NOT NULL,
  status                VARCHAR2(10)    NOT NULL,
  root_path		VARCHAR2(765)	,
  rel_path              VARCHAR2(20)     ,
  CONSTRAINT granule_pk PRIMARY KEY (granule_id)
);

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

/*
** granule_character
*/
CREATE TABLE granule_character
(
  granule_id            NUMBER          NOT NULL,
  de_id            NUMBER          NOT NULL,
  value                 VARCHAR2(255)
);

/*
** granule_contact
*/
CREATE TABLE granule_contact
(
  granule_id            NUMBER          NOT NULL,
  contact_id            NUMBER          NOT NULL,
  CONSTRAINT granule_contact_pk PRIMARY KEY (granule_id, contact_id)
);

/*
** granule_datetime
*/
CREATE TABLE granule_datetime
(
  granule_id            NUMBER          NOT NULL,
  de_id            NUMBER          NOT NULL,
  value_long NUMBER  
);

/*
** granule_element
*/
CREATE TABLE granule_element
(
  dataset_id            NUMBER          NOT NULL,
  element_id            NUMBER          NOT NULL,
  obligation_flag       CHAR(1)         NOT NULL,
  CONSTRAINT granule_element_pk PRIMARY KEY (dataset_id, element_id)
);
 
/*
** element_dd
*/
CREATE TABLE element_dd
(
  element_id            NUMBER          NOT NULL,
  short_name            VARCHAR2(30)    NOT NULL,
  long_name             VARCHAR2(80)            ,
  type                  VARCHAR2(20)    NOT NULL,
  description           VARCHAR2(1024)          ,
  max_length		NUMBER			,
  scope			VARCHAR2(20)		,
  CONSTRAINT element_dd_pk PRIMARY KEY (element_id)
);

/*
** granule_integer
*/
CREATE TABLE granule_integer
(
  granule_id            NUMBER          NOT NULL,
  de_id            NUMBER          NOT NULL,
  value                 NUMBER,
  units					VARCHAR2(10)
);

/*
** granule_meta_history
*/
CREATE TABLE granule_meta_history
(
  granule_id            NUMBER          NOT NULL,
  version_id            NUMBER          NOT NULL,
  creation_date_long    NUMBER            NOT NULL,
  last_revision_date_long    NUMBER            NOT NULL,
  revision_history      VARCHAR2(1024)  NOT NULL,
  echo_submit_date_long NUMBER                   ,
  CONSTRAINT granule_meta_history_pk PRIMARY KEY (granule_id, version_id)
);

/*
** granule_real
*/
CREATE TABLE granule_real
(
  granule_id            NUMBER          NOT NULL,
  de_id            NUMBER          NOT NULL,
  value                 NUMBER,
  units					VARCHAR2(10)
);

/*
** granule_reference
*/
CREATE TABLE granule_reference
(
  granule_id            NUMBER          NOT NULL,
  path                  VARCHAR2(1024)   NOT NULL,
  type                  VARCHAR2(31)            ,
  status                VARCHAR2(10)    NOT NULL,
  description           VARCHAR2(1024)     
);

/*
** granule_sip
*/
CREATE TABLE granule_sip
(
  granule_id            NUMBER          NOT NULL,
  sip                   CLOB            NOT NULL,
  CONSTRAINT granule_sip_pk PRIMARY KEY (granule_id)
);


/*
** project
*/
CREATE TABLE project
(
  project_id            NUMBER          NOT NULL,
  short_name            VARCHAR2(31)    NOT NULL,
  long_name             VARCHAR2(80)            ,
  CONSTRAINT project_pk PRIMARY KEY (project_id)
);

/*
** provider
*/
CREATE TABLE provider
(
  provider_id           NUMBER          NOT NULL,
  short_name            VARCHAR2(31)    NOT NULL,
  long_name             VARCHAR2(160)   NOT NULL,
  type                  VARCHAR2(20)    NOT NULL,
  CONSTRAINT provider_pk PRIMARY KEY (provider_id)
);

/*
** provider_resource
*/
CREATE TABLE provider_resource
(
  provider_id           NUMBER          NOT NULL,
  path                  VARCHAR2(255)   NOT NULL
);
  
/*
** sensor
*/            
CREATE TABLE sensor
(
  sensor_id             NUMBER          NOT NULL,
  short_name            VARCHAR2(31)    NOT NULL,
  long_name             VARCHAR2(80)            ,
  swath_width           NUMBER                  ,
  description           VARCHAR2(1024)          ,
  sample_frequency	NUMBER,
  CONSTRAINT sensor_pk PRIMARY KEY (sensor_id)
);

/*
** source
*/
CREATE TABLE source
(
  source_id             NUMBER          NOT NULL,
  short_name            VARCHAR2(31)    NOT NULL,
  long_name             VARCHAR2(80)            ,
  type                  VARCHAR2(80)            ,
  orbit_period          NUMBER                  ,
  incl_angle            NUMBER                  ,
  description           VARCHAR2(1024)          ,
  CONSTRAINT source_pk PRIMARY KEY (source_id)
);

/*
** METADATA MANIFEST
*/
CREATE TABLE metadata_manifest 
(    
  manifest_id NUMBER NOT NULL,
  item_id 		NUMBER 			NOT NULL,
  type 			VARCHAR2(10 BYTE) 	NOT NULL,
  username	 	VARCHAR2(20) 		NOT NULL,
  submission_date_long 	NUMBER 			NOT NULL,
  manifest		 CLOB 			NOT NULL,
  diff			 CLOB				,
  CONSTRAINT metadata_manifest_pk PRIMARY KEY (manifest_id)
);

/*  
** Alter Tables by Adding Foreign Key References
*/

/*
** collection_contact
*/
ALTER TABLE collection_contact
   ADD CONSTRAINT collection_contact_fk1
   FOREIGN KEY (collection_id)
   REFERENCES collection;

ALTER TABLE collection_contact
   ADD CONSTRAINT collection_contact_fk2
   FOREIGN KEY (contact_id)
   REFERENCES contact;

/*
** collection_dataset
*/
ALTER TABLE collection_dataset
  ADD CONSTRAINT collection_dataset_fk1
  FOREIGN KEY (collection_id)
  REFERENCES collection;
 
ALTER TABLE collection_dataset
  ADD CONSTRAINT collection_dataset_fk2
  FOREIGN KEY (dataset_id)
  REFERENCES dataset;

/*
** collection_element
*/
ALTER TABLE collection_element
  ADD CONSTRAINT collection_element_fk1
  FOREIGN KEY (collection_id)
  REFERENCES collection;
 
ALTER TABLE collection_element
  ADD CONSTRAINT collection_element_fk2
  FOREIGN KEY (element_id)
  REFERENCES collection_element_dd;
 
/*
** collection_product
*/
ALTER TABLE collection_product
  ADD CONSTRAINT collection_product_fk1
  FOREIGN KEY (collection_id)
  REFERENCES collection;

/*
** contact
*/
ALTER TABLE contact
  ADD CONSTRAINT contact_fk1
  FOREIGN KEY (provider_id)
  REFERENCES provider;

ALTER TABLE COLLECTION_LEGACY_PRODUCT 
ADD CONSTRAINT COLLECTION_LEGACY_PRODUCT_FK1 
FOREIGN KEY (COLLECTION_ID) 
REFERENCES COLLECTION;


/*
** dataset
*/
ALTER TABLE dataset
  ADD CONSTRAINT dataset_fk1
  FOREIGN KEY (provider_id)
  REFERENCES provider;

/*
** dataset_citation
*/ 
ALTER TABLE dataset_citation
  ADD CONSTRAINT dataset_citation_fk1
  FOREIGN KEY (dataset_id)
  REFERENCES dataset;


/*
** dataset_contact
*/
ALTER TABLE dataset_contact
   ADD CONSTRAINT dataset_contact_fk1
   FOREIGN KEY (dataset_id)
   REFERENCES dataset;

ALTER TABLE dataset_contact
   ADD CONSTRAINT dataset_contact_fk2
   FOREIGN KEY (contact_id)
   REFERENCES contact;

/*
** dataset_coverage
*/
ALTER TABLE dataset_coverage
  ADD CONSTRAINT dataset_coverage_fk1
  FOREIGN KEY (dataset_id)
  REFERENCES dataset
  ON DELETE CASCADE;



/*
** dataset_location_policy
*/
ALTER TABLE dataset_location_policy
  ADD CONSTRAINT dataset_location_policy_fk1
  FOREIGN KEY (dataset_id)
  REFERENCES dataset;

/*
** dataset_meta_history
*/
ALTER TABLE dataset_meta_history
  ADD CONSTRAINT dataset_meta_history_fk1
  FOREIGN KEY (dataset_id)
  REFERENCES dataset;
 
/*
** dataset_parameter
*/
ALTER TABLE dataset_parameter
  ADD CONSTRAINT dataset_parameter_fk1
  FOREIGN KEY (dataset_id)
  REFERENCES dataset;
 
/*
** dataset_policy
*/
ALTER TABLE dataset_policy
  ADD CONSTRAINT dataset_policy_fk1
  FOREIGN KEY (dataset_id)
  REFERENCES dataset
  ON DELETE CASCADE;
 
/*
** dataset_project
*/
ALTER TABLE dataset_project
  ADD CONSTRAINT dataset_project_fk1
  FOREIGN KEY (dataset_id)
  REFERENCES dataset;
 
ALTER TABLE dataset_project
  ADD CONSTRAINT dataset_project_fk2
  FOREIGN KEY (project_id)
  REFERENCES project;
 
/*
** dataset_resource
*/
ALTER TABLE dataset_resource
  ADD CONSTRAINT dataset_resource_fk1
  FOREIGN KEY (dataset_id)
  REFERENCES dataset;
 
/*
** dataset_source
*/
ALTER TABLE dataset_source
  ADD CONSTRAINT dataset_source_fk1
  FOREIGN KEY (dataset_id)
  REFERENCES dataset;
 
ALTER TABLE dataset_source
  ADD CONSTRAINT dataset_source_fk2
  FOREIGN KEY (source_id)
  REFERENCES source;
 
ALTER TABLE dataset_source
  ADD CONSTRAINT dataset_source_fk3
  FOREIGN KEY (sensor_id)
  REFERENCES sensor;
 
/*
** dataset_software
*/
ALTER TABLE dataset_software
  ADD CONSTRAINT dataset_software_fk1
  FOREIGN KEY (dataset_id)
  REFERENCES dataset;
 
/*
** dataset_spatial
*/
ALTER TABLE dataset_spatial
  ADD CONSTRAINT dataset_spatial_fk1
  FOREIGN KEY (dataset_id)
  REFERENCES dataset
  ON DELETE CASCADE;

/*
** dataset_version
*/
ALTER TABLE dataset_version
  ADD CONSTRAINT dataset_version_fk1
  FOREIGN KEY (dataset_id)
  REFERENCES dataset;

/*
** granule
*/
ALTER TABLE granule
  ADD CONSTRAINT granule_fk1
  FOREIGN KEY (dataset_id)
  REFERENCES dataset;

/*
** granule_archive
*/
ALTER TABLE granule_archive
  ADD CONSTRAINT granule_archive_fk1
  FOREIGN KEY (granule_id)
  REFERENCES granule;

/*
** granule_character
*/
ALTER TABLE granule_character
  ADD CONSTRAINT granule_character_fk1
  FOREIGN KEY (granule_id)
  REFERENCES granule;

ALTER TABLE granule_character
  ADD CONSTRAINT granule_character_fk2
  FOREIGN KEY (de_id)
  REFERENCES dataset_element;

/*
** dataset_character
*/
ALTER TABLE dataset_character
  ADD CONSTRAINT dataset_character_fk1
  FOREIGN KEY (dataset_id)
  REFERENCES dataset;

ALTER TABLE dataset_character
  ADD CONSTRAINT dataset_character_fk2
  FOREIGN KEY (de_id)
  REFERENCES dataset_element;


/*
** granule_contact
*/
ALTER TABLE granule_contact
  ADD CONSTRAINT granule_contact_fk1
  FOREIGN KEY (granule_id)
  REFERENCES granule;

ALTER TABLE granule_contact
  ADD CONSTRAINT granule_contact_fk2
  FOREIGN KEY (contact_id)
  REFERENCES contact;
  
/*
** granule_datetime
*/
ALTER TABLE granule_datetime
  ADD CONSTRAINT granule_datetime_fk1
  FOREIGN KEY (granule_id)
  REFERENCES granule;

ALTER TABLE granule_datetime
  ADD CONSTRAINT granule_datetime_fk2
  FOREIGN KEY (de_id)
  REFERENCES dataset_element;
 
/*
** granule_integer
*/
ALTER TABLE granule_integer
  ADD CONSTRAINT granule_integer_fk1
  FOREIGN KEY (granule_id)
  REFERENCES granule;

ALTER TABLE granule_integer
  ADD CONSTRAINT granule_integer_fk2
  FOREIGN KEY (de_id)
  REFERENCES dataset_element;


/*
** dataset_datetime
*/
ALTER TABLE dataset_datetime
  ADD CONSTRAINT dataset_datetime_fk1
  FOREIGN KEY (dataset_id)
  REFERENCES dataset;

ALTER TABLE dataset_datetime
  ADD CONSTRAINT dataset_datetime_fk2
  FOREIGN KEY (de_id)
  REFERENCES dataset_element;
 
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

/*
** dataset_integer
*/
ALTER TABLE dataset_integer
  ADD CONSTRAINT dataset_integer_fk1
  FOREIGN KEY (dataset_id)
  REFERENCES dataset;

ALTER TABLE dataset_integer
  ADD CONSTRAINT dataset_integer_fk2
  FOREIGN KEY (de_id)
  REFERENCES dataset_element;




/*
** granule_meta_history
*/
ALTER TABLE granule_meta_history
  ADD CONSTRAINT granule_meta_history_fk1
  FOREIGN KEY (granule_id)
  REFERENCES granule
  ON DELETE CASCADE;
 
/*
** granule_real
*/
ALTER TABLE granule_real
  ADD CONSTRAINT granule_real_fk1
  FOREIGN KEY (granule_id)
  REFERENCES granule;

ALTER TABLE granule_real
  ADD CONSTRAINT granule_real_fk2
  FOREIGN KEY (de_id)
  REFERENCES dataset_element;
 
 /*
** dataset_real
*/
ALTER TABLE dataset_real
  ADD CONSTRAINT dataset_real_fk1
  FOREIGN KEY (dataset_id)
  REFERENCES dataset;

ALTER TABLE dataset_real
  ADD CONSTRAINT dataset_real_fk2
  FOREIGN KEY (de_id)
  REFERENCES dataset_element;
 
 
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

ALTER TABLE granule_spatial
  ADD CONSTRAINT granule_spatial_fk2
  FOREIGN KEY (de_id)
  REFERENCES dataset_element;

/*
** provider_resource
*/
ALTER TABLE provider_resource
  ADD CONSTRAINT provider_resource_fk1
  FOREIGN KEY (provider_id)
  REFERENCES provider;
 
/*
** UPDATES for version 1.3.1
*/

/*
** Add REMOTE-FTP and REMOTE-ODAP paths to the dataset_location_policy table
*/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AMSRE/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AMSRE';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AMSRE/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AMSRE';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/ATS_NR_2P/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-ATS_NR_2P';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/ATS_NR_2P/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-ATS_NR_2P';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AVHRR16_G/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR16_G';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AVHRR16_G/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR16_G';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AVHRR16_L/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR16_L';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AVHRR16_L/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR16_L';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AVHRR17_G/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR17_G';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AVHRR17_G/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR17_G';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AVHRR17_L/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR17_L';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AVHRR17_L/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR17_L';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/NAR16_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-NAR16_SST';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/NAR16_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-NAR16_SST';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/NAR17_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-NAR17_SST';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/NAR17_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-NAR17_SST';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/NAR18_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-NAR18_SST';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/NAR18_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-NAR18_SST';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/SEVIRI_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-SEVIRI_SST';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/SEVIRI_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-SEVIRI_SST';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/TMI/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-TMI';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/TMI/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-TMI';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/MODIS_A/JPL'
FROM dataset
WHERE short_name = 'JPL-L2P-MODIS_A';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/MODIS_A/JPL'
FROM dataset
WHERE short_name = 'JPL-L2P-MODIS_A';

/**/


INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/MODIS_T/JPL'
FROM dataset
WHERE short_name = 'JPL-L2P-MODIS_T';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/MODIS_T/JPL'
FROM dataset
WHERE short_name = 'JPL-L2P-MODIS_T';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AVHRR17_G/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR17_G';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AVHRR17_G/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR17_G';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AVHRR17_L/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR17_L';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AVHRR17_L/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR17_L';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AVHRR18_G/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR18_G';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AVHRR18_G/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR18_G';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AVHRR18_L/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR18_L';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AVHRR18_L/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR18_L';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AVHRRMTA_G/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRRMTA_G';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AVHRRMTA_G/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRRMTA_G';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AVHRR17_L/NEODAAS'
FROM dataset
WHERE short_name = 'NEODAAS-L2P-AVHRR17_L';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AVHRR17_L/NEODAAS'
FROM dataset
WHERE short_name = 'NEODAAS-L2P-AVHRR17_L';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AVHRR18_L/NEODAAS'
FROM dataset
WHERE short_name = 'NEODAAS-L2P-AVHRR18_L';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AVHRR18_L/NEODAAS'
FROM dataset
WHERE short_name = 'NEODAAS-L2P-AVHRR18_L';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/GOES11/OSDPD'
FROM dataset
WHERE short_name = 'OSDPD-L2P-GOES11';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/GOES11/OSDPD'
FROM dataset
WHERE short_name = 'OSDPD-L2P-GOES11';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/GOES12/OSDPD'
FROM dataset
WHERE short_name = 'OSDPD-L2P-GOES12';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/GOES12/OSDPD'
FROM dataset
WHERE short_name = 'OSDPD-L2P-GOES12';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AMSRE/REMSS'
FROM dataset
WHERE short_name = 'REMSS-L2P-AMSRE';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AMSRE/REMSS'
FROM dataset
WHERE short_name = 'REMSS-L2P-AMSRE';

/**/
INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/TMI/REMSS'
FROM dataset
WHERE short_name = 'REMSS-L2P-TMI';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/TMI/REMSS'
FROM dataset
WHERE short_name = 'REMSS-L2P-TMI';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/ATS-NR_2P/UPA'
FROM dataset
WHERE short_name = 'UPA-L2P-ATS-NR_2P';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/ATS-NR_2P/UPA'
FROM dataset
WHERE short_name = 'UPA-L2P-ATS-NR_2P';

/*
**
** Changes for the new Directory Structure only JASON-1_GDR_*, JASON-1_IGDR*, JASON-1_OSDR, and QSCAT_LEVEL_3 are affected
**
*/

/*Change DATASET_POLICY.ACCESS_TYPE from X to open*/
/*
** Note: This seemingly changes some Restricted items to 'open'
** based on new DIR structure. See JASON-1_GDR for example.
*/

UPDATE dataset_policy
SET ACCESS_TYPE='OPEN'
WHERE DATASET_ID IN(
SELECT DATASET_ID from DATASET WHERE SHORT_NAME LIKE 'JASON-1_GDR_%'
OR SHORT_NAME LIKE 'JASON-1_IGDR%'
OR SHORT_NAME LIKE 'JASON-1_OSDR'
OR SHORT_NAME LIKE 'QSCAT_LEVEL_3'
);

/*Change DATASET_LOCATION_POLICY type to ARCHIVE-OPEN*/

UPDATE dataset_location_policy
SET TYPE='ARCHIVE-OPEN'
WHERE TYPE='ARCHIVE-PUBLIC'
AND DATASET_ID IN(
SELECT DATASET_ID from DATASET WHERE SHORT_NAME LIKE 'JASON-1_GDR_%'
OR SHORT_NAME LIKE 'JASON-1_IGDR%'
OR SHORT_NAME LIKE 'JASON-1_OSDR'
OR SHORT_NAME LIKE 'QSCAT_LEVEL_3'
);

/*Change DATASET_LOCATION_POLICY type to ARCHIVE-CONTROLLED*/

UPDATE dataset_location_policy
SET TYPE='ARCHIVE-CONTROLLED'
WHERE TYPE='ARCHIVE-RESTRICTED'
AND DATASET_ID IN(
SELECT DATASET_ID from DATASET WHERE SHORT_NAME LIKE 'JASON-1_GDR_%'
OR SHORT_NAME LIKE 'JASON-1_IGDR%'
OR SHORT_NAME LIKE 'JASON-1_OSDR'
OR SHORT_NAME LIKE 'QSCAT_LEVEL_3'
);


/*Change GRANULE.ACCESS_TYPE type to OPEN*/

UPDATE granule
SET ACCESS_TYPE='OPEN'
WHERE DATASET_ID IN(
SELECT DATASET_ID from DATASET WHERE SHORT_NAME LIKE 'JASON-1_GDR_%'
OR SHORT_NAME LIKE 'JASON-1_IGDR%'
OR SHORT_NAME LIKE 'JASON-1_OSDR'
OR SHORT_NAME LIKE 'QSCAT_LEVEL_3'
);

/*change DATASET_LOCATION_POLICY.BASE_PATH to mimic new dir structure
** First we'll do "public" -> "open"
** i.e. file:///store/quikscat/public/L3/data -> file:///store/quikscat/open/L3/data
*/

UPDATE dataset_location_policy
SET BASE_PATH=REPLACE(base_path,'public','open')
WHERE TYPE='ARCHIVE-OPEN'
AND DATASET_ID IN(
SELECT DATASET_ID from DATASET WHERE SHORT_NAME LIKE 'JASON-1_GDR_%'
OR SHORT_NAME LIKE 'JASON-1_IGDR%'
OR SHORT_NAME LIKE 'JASON-1_OSDR'
OR SHORT_NAME LIKE 'QSCAT_LEVEL_3'
);

/*change DATASET_LOCATION_POLICY.BASE_PATH to mimic new dir structure
** First we'll do "restricted" -> "controlled"
** i.e. file:///store/quikscat/restricted/L3/data -> file:///store/quikscat/controlled/L3/data
*/

UPDATE dataset_location_policy
SET BASE_PATH=REPLACE(base_path,'restricted','controlled')
WHERE TYPE='ARCHIVE-CONTROLLED'
AND DATASET_ID IN(
SELECT DATASET_ID from DATASET WHERE SHORT_NAME LIKE 'JASON-1_GDR_%'
OR SHORT_NAME LIKE 'JASON-1_IGDR%'
OR SHORT_NAME LIKE 'JASON-1_OSDR'
OR SHORT_NAME LIKE 'QSCAT_LEVEL_3'
);

/*
** GHRSST ACCESS_TYPE
*/

/*
** GHRSST DATASET_POLICY ACCESS_TYPE fix
*/
UPDATE dataset_policy
SET ACCESS_TYPE='OPEN'
WHERE DATASET_ID IN(
select dataset_id from dataset
where SHORT_NAME not like 'JASON%'
AND SHORT_NAME not like 'QSCAT%'
AND SHORT_NAME not like 'OCO%'
);

/*
** And GHRSST associated granules
*/

/*Change GRANULE.ACCESS_TYPE type to OPEN*/

UPDATE granule
SET ACCESS_TYPE='OPEN'
WHERE DATASET_ID IN(
select dataset_id from dataset
where SHORT_NAME not like 'JASON%'
AND SHORT_NAME not like 'QSCAT%'
AND SHORT_NAME not like 'OCO%'
);

/*
** Create Indexes
*/

/*
** Create Sequences
*/

CREATE SEQUENCE metadata_manifest_id_seq
  NOCACHE;
CREATE SEQUENCE dataset_element_id_seq
  NOCACHE;
CREATE SEQUENCE collection_id_seq
  NOCACHE;
CREATE SEQUENCE contact_id_seq
  NOCACHE;
CREATE SEQUENCE dataset_id_seq
  NOCACHE;
CREATE SEQUENCE element_id_collection_seq
  NOCACHE;
CREATE SEQUENCE element_id_granule_seq
  NOCACHE;
CREATE SEQUENCE granule_id_seq
  CACHE 100;
CREATE SEQUENCE product_id_seq
  START WITH 300
  NOCACHE;
CREATE SEQUENCE project_id_seq
  NOCACHE;
CREATE SEQUENCE provider_id_seq
  NOCACHE;
CREATE SEQUENCE sensor_id_seq
  NOCACHE;
CREATE SEQUENCE source_id_seq
  NOCACHE;

