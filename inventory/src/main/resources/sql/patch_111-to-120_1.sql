/*
** Copyright (c) 2008-2009, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: patch_111-to-120_1.sql 2673 2009-02-18 01:18:26Z shardman $
*/

/*
** This script patches the 1.1.1 Inventory schema for the 1.2.0 release. 
*/

/*
** Add REMSS-L4HRfnd-GLOB-mw_ir_rt_OI to the GHRSST collection.
*/

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_rt_OI';

/*
** Add a long name for the Jason-1 source entry since ECHO 
** requires one. Add a long name for the project for consistency.
** Capitalize provider short name for consistency.
*/

UPDATE source SET long_name = 'Jason-1'
WHERE short_name = 'JASON-1';

UPDATE project SET long_name = 'Jason-1'
WHERE short_name = 'JASON-1';

UPDATE provider SET short_name = 'JASON-1'
WHERE short_name = 'Jason-1';

/*
** Correct the dataset location policy for the ABOM-L4LRfnd-GLOB-GAMSSA_28km 
** dataset. Basically replace "AUS" with "GLOB" for the associated base paths.
*/

UPDATE dataset_location_policy SET base_path = SUBSTR(base_path, 1, INSTR(base_path, '/AUS', 1, 1)) || 'GLOB' || SUBSTR(base_path, INSTR(base_path, '/AUS', 1, 1) + 4, LENGTH(base_path) - INSTR(base_path, '/AUS', 1, 1) - 3)
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'ABOM-L4LRfnd-GLOB-GAMSSA_28km');

/*
** Correct the dataset location policy for the REMSS-L4HRfnd-GLOB-mw_ir_rt_OI 
** dataset. Basically replace "rt_" with "" for the associated base paths.
*/

UPDATE dataset_location_policy SET base_path = SUBSTR(base_path, 1, INSTR(base_path, '_rt', 1, 1)) || SUBSTR(base_path, INSTR(base_path, '_rt', 1, 1) + 4, LENGTH(base_path) - INSTR(base_path, '_rt', 1, 1) - 3)
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_rt_OI');

/*
** Add QSCATLOD to the original provider dataset name for the QSCAT_ANCILLARY 
** dataset.
*/

UPDATE dataset SET provider_dataset_name = 'QSCATLOD, QSCAT0QA, QSCATL1AQ, QSCATATT, QSCATCAL, QSCATICEM, QSCATEPHG, QSCATNWP1, QSCATNWP2, QSCATQARPT, QSCATREVTIME, QSCATTCD'
WHERE short_name = 'QSCAT_ANCILLARY';

COMMIT;
