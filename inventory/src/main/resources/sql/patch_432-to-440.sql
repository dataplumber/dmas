set LIN 300
/*Add the 360 flag to DATASET_COVERAGE*/
alter table DATASET_COVERAGE ADD("GRANULE_RANGE_360" CHAR(1 BYTE) DEFAULT 'N' NOT NULL ENABLE);

/*Listing of datasets without collection information*/
select dataset_id, persistent_id, short_name  from dataset where dataset_id in (select dataset_id from dataset_coverage where dataset_id not in (select dataset_id from collection_Dataset));

BEGIN
   FOR colDs IN (
        SELECT dataset_id, Granule_range_360
          FROM collection_dataset
         )
   LOOP
      update dataset_coverage dc set dc.GRANULE_RANGE_360=colDs.GRanule_range_360 where dc.dataset_id = colDs.dataset_id;
   END LOOP;
END;

/
/*fields to move ECHO, GCMD submission tracking - these can be left blank as we will need to re-export them*/
alter table DATASET_META_HISTORY add "ECHO_SUBMIT_DATE_LONG" NUMBER;

alter table DATASET_RESOURCE modify PATH VARCHAR2(1024);

commit;
