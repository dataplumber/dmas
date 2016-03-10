ALTER TABLE DATASET
ADD ("METADATA" VARCHAR2(1024),
     "SAMPLE_FREQUENCY" 	NUMBER 	NULL,
     "SWATH_WIDTH"		NUMBER	NULL,
     "TEMPORAL_REPEAT"		VARCHAR2(1024)	NULL,
     "TEMPORAL_REPEAT_MIN"          VARCHAR2(1024)  NULL,
     "TEMPORAL_REPEAT_MAX"          VARCHAR2(1024)  NULL
);

ALTER TABLE SENSOR
ADD("SAMPLE_FREQUENCY" NUMBER NULL);

delete from element_dd where element_id in (Select element_id from element_dd where short_name in (Select short_name from (select element_dd.short_name, count(1) as c from element_dd group by short_name) where c > 1) AND element_id > 60);

ALTER TABLE DATASET 
MODIFY ("REMOTE_DATASET" DEFAULT 'L')
;
