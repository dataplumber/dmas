create or replace TRIGGER GR_ARC_UP_TRIG
AFTER UPDATE ON GRANULE_ARCHIVE
FOR EACH ROW
BEGIN
 update granule_meta_history set LAST_REVISION_DATE_LONG=INVENTORY.PSTTimeStampToLong(CURRENT_DATE) where granule_id=:new.granule_id;
END GR_ARC_UP_TRIG;
/

create or replace TRIGGER GR_REF_UP_TRIG
AFTER UPDATE ON GRANULE_REFERENCE
FOR EACH ROW
BEGIN
 update granule_meta_history set LAST_REVISION_DATE_LONG=INVENTORY.PSTTimeStampToLong(CURRENT_DATE) where granule_id=:new.granule_id;
END GR_REF_UP_TRIG;
/

create or replace TRIGGER DS_LP_UP_TRIG
AFTER UPDATE ON DATASET_LOCATION_POLICY
FOR EACH ROW
BEGIN
 update DATASET_META_HISTORY set LAST_REVISION_DATE_LONG=INVENTORY.PSTTimeStampToLong(CURRENT_DATE) where dataset_id=:new.dataset_id;
END DS_LP_UP_TRIG;
/

create or replace TRIGGER GR_UP_TRIG
AFTER UPDATE ON GRANULE
FOR EACH ROW
BEGIN
 update granule_meta_history set LAST_REVISION_DATE_LONG=INVENTORY.PSTTimeStampToLong(CURRENT_DATE) where granule_id=:new.granule_id;
END GR_UP_TRIG;
/
