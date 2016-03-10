ALTER TABLE DATASET_POLICY
ADD ("VIEW_ONLINE" VARCHAR2(1) DEFAULT 'Y');

ALTER TABLE DATASET
ADD ("REMOTE_DATASET" VARCHAR2(1) DEFAULT 'N' NOT NULL);

ALTER TABLE DATASET_RESOURCE MODIFY (NAME VARCHAR(90));
insert into dataset_resource (dataset_id,name,path,type,description) select dataset_id as did, short_name || '.jpg','/data/export/web/thumbnails', 'Thumbnail','Thumbnail image for Website' from dataset;

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

insert into dataset_region (dataset_id, region, region_detail) select dataset_id, region, region_detail from dataset;

ALTER TABLE DATASET 
DROP COLUMN "REGION"
;

ALTER TABLE DATASET 
DROP COLUMN "REGION_DETAIL"
;


ALTER TABLE DATASET_CITATION MODIFY (PUBLISHER VARCHAR(160));
ALTER TABLE DATASET_CITATION MODIFY (RELEASE_PLACE VARCHAR(160));

/*
process the newlat/long resolutions (float)
alter the tables
strip the "k/degrees"
enter into the new column
The new inventory package must be run before this can be done (needs the stringTofloat function)
*/

ALTER TABLE DATASET 
RENAME COLUMN "LATITUDE_RESOLUTION" TO "LATITUDE_RESOLUTION_CHAR" ;
ALTER TABLE DATASET 
RENAME COLUMN "LONGITUDE_RESOLUTION" TO "LONGITUDE_RESOLUTION_CHAR" ;

ALTER TABLE DATASET 
ADD ("LATITUDE_RESOLUTION" FLOAT)
ADD ("LONGITUDE_RESOLUTION" FLOAT)
;

update DATASET set latitude_resolution=Inventory.StringToFloat(latitude_resolution_char), longitude_resolution= Inventory.StringToFloat(longitude_resolution_char);


ALTER TABLE DATASET 
DROP COLUMN "LATITUDE_RESOLUTION_CHAR";

ALTER TABLE DATASET 
DROP COLUMN "LONGITUDE_RESOLUTION_CHAR";

ALTER TABLE COLLECTION 
ADD ("AGGREGATE" CHAR(1) DEFAULT 'N')
;


COMMIT;
