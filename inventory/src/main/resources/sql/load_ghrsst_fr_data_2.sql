/*
** Copyright (c) 2008, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: load_ghrsst_fr_data_2.sql 4716 2010-04-08 16:53:35Z gangl $
*/

/*
** This script loads the GHRSST data from the fr_mmr database into 
** the Inventory schema. This script requires a schema named 
** "ghrsst_migration" to be located on the same Oracle database server 
** which contains the GHRSST dsd_mmr and fr_mmr schemas.
*/

/*
** granule
**
** Then update the compress_type column in the granule table to 
** correspond with the dataset policy as specified above.
*/

DECLARE
  var_dataset_id     dataset_policy.dataset_id%TYPE;
  var_compress_type  dataset_policy.compress_type%TYPE;

CURSOR DCursor IS
  SELECT dataset_id, compress_type
  FROM dataset_policy;

BEGIN
  OPEN DCursor;
  LOOP

  FETCH DCursor INTO var_dataset_id, var_compress_type;
  EXIT WHEN DCursor%NOTFOUND;

  UPDATE granule SET compress_type = var_compress_type
  WHERE dataset_id = var_dataset_id;

  END LOOP;
  CLOSE DCursor;
END;
/

/*
** granule_archive
**
** Load the granule_archive table for each dataset by concatenating
** the path for the data file based on dataset policy and metadata
** for each specific granule. The file_size and checksum values will
** need to be filled in by another application later.
*/

DECLARE
  var_dataset_id     dataset.dataset_id%TYPE;
  var_base_path      dataset_location_policy.base_path%TYPE;
  var_compress_type  dataset_policy.compress_type%TYPE;
  var_extension      VARCHAR2(10);

CURSOR Dcursor IS
  SELECT d.dataset_id, dl.base_path, dp.compress_type
  FROM dataset d, dataset_location_policy dl, dataset_policy dp, collection c, collection_dataset cd
  WHERE d.dataset_id = cd.dataset_id AND c.collection_id = cd.collection_id AND c.short_name = 'GHRSST' AND d.dataset_id = dl.dataset_id AND d.dataset_id = dp.dataset_id AND dl.type = 'ARCHIVE-OPEN';

BEGIN
  OPEN DCursor;
  LOOP

  FETCH DCursor INTO var_dataset_id, var_base_path, var_compress_type;
  EXIT WHEN DCursor%NOTFOUND;

  IF var_compress_type = 'BZIP2' THEN
    var_extension := '.bz2';
  ELSIF var_compress_type = 'GZIP' THEN
    var_extension := '.gz';
  ELSE
    var_extension := '';
  END IF;

  INSERT INTO granule_archive (granule_id, type, file_size, compress_flag, checksum, name, status)
  SELECT g.granule_id, 'DATA', 0, 'Y', NULL, g.name || var_extension, 'ONLINE'
  FROM granule g
  WHERE g.dataset_id = var_dataset_id;

  UPDATE GRANULE SET ROOT_PATH=(var_base_path), REL_PATH=(TO_CHAR(inventory.longToTimestamp(GRANULE.start_time_long), 'YYYY') || '/' || TO_CHAR(inventory.longTotimestamp(GRANULE.start_time_long), 'DDD')) where GRANULE.dataset_id = var_dataset_id; 


  INSERT INTO granule_archive (granule_id, type, file_size, compress_flag, checksum, name, status)
  SELECT g.granule_id, 'CHECKSUM', 0, 'N', NULL,  g.name || var_extension || '.md5', 'ONLINE'
  FROM granule g
  WHERE g.dataset_id = var_dataset_id;

  INSERT INTO granule_archive (granule_id, type, file_size, compress_flag, checksum, name, status)
  SELECT g.granule_id, 'METADATA', 0, 'N', NULL, 'FR-' || SUBSTR(g.name, 1, INSTR(g.name, '.nc', 1) - 1) || '.xml', 'ONLINE'
  FROM granule g
  WHERE g.dataset_id = var_dataset_id;

  INSERT INTO granule_archive (granule_id, type, file_size, compress_flag, checksum, name, status)
  SELECT g.granule_id, 'CHECKSUM', 0, 'N', NULL, 'FR-' || SUBSTR(g.name, 1, INSTR(g.name, '.nc', 1) - 1) || '.xml.md5', 'ONLINE'
  FROM granule g
  WHERE g.dataset_id = var_dataset_id;

  END LOOP;
  CLOSE DCursor;
END;
/
