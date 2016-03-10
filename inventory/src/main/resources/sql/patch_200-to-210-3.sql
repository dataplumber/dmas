/*
**This patch file corrects some legacy datasets missing required tables such as granule_metadata
*/

ALTER SESSION SET time_zone = 'GMT';

insert into dataset_meta_history 
select dataset_id,1,'REPAIRED META_HISTORY ENTRY', inventory.timestampToLong(CURRENT_TIMESTAMP), inventory.timestampToLong(CURRENT_TIMESTAMP)
from dataset where dataset_id not in (select dataset_id from dataset_meta_history);

insert into dataset_coverage
select dataset_id,-999, -999,-999,-999, -999, -999, -999,-999, -999, -999
from dataset where dataset_id not in (select dataset_id from dataset_coverage);

COMMIT;
