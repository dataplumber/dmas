/*
** Copyright (c) 2008-2009, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: patch_111-to-120_2.sql 2673 2009-02-18 01:18:26Z shardman $
*/

/*
** This script patches the 1.1.1 Inventory schema for the 1.2.0 release. 
*/

/*
** granule_archive
**
** Load the granule_archive table for the REMSS-L4HRfnd-GLOB-mw_ir_rt_OI
** dataset, which was missed the first time around.
*/

DECLARE
  var_dataset_id     dataset.dataset_id%TYPE;
  var_base_path      dataset_location_policy.base_path%TYPE;
  var_compress_type  dataset_policy.compress_type%TYPE;
  var_extension      VARCHAR2(10);

CURSOR Dcursor IS
  SELECT d.dataset_id, dl.base_path, dp.compress_type
  FROM dataset d, dataset_location_policy dl, dataset_policy dp
  WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_rt_OI' AND d.dataset_id = dl.dataset_id AND d.dataset_id = dp.dataset_id AND dl.type = 'ARCHIVE-PUBLIC';

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

  INSERT INTO granule_archive (granule_id, type, file_size, compress_flag, checksum, path, status)
  SELECT g.granule_id, 'DATA', 0, 'Y', NULL, var_base_path || '/' || TO_CHAR(g.start_time, 'YYYY') || '/' || TO_CHAR(g.start_time, 'DDD') || '/' || g.name || var_extension, 'ONLINE'
  FROM granule g
  WHERE g.dataset_id = var_dataset_id;

  INSERT INTO granule_archive (granule_id, type, file_size, compress_flag, checksum, path, status)
  SELECT g.granule_id, 'CHECKSUM', 0, 'N', NULL, var_base_path || '/' || TO_CHAR(g.start_time, 'YYYY') || '/' || TO_CHAR(g.start_time, 'DDD') || '/' || g.name || var_extension || '.md5', 'ONLINE'
  FROM granule g
  WHERE g.dataset_id = var_dataset_id;

  INSERT INTO granule_archive (granule_id, type, file_size, compress_flag, checksum, path, status)
  SELECT g.granule_id, 'METADATA', 0, 'N', NULL, var_base_path || '/' || TO_CHAR(g.start_time, 'YYYY') || '/' || TO_CHAR(g.start_time, 'DDD') || '/FR-' || SUBSTR(g.name, 1, INSTR(g.name, '.nc', 1) - 1) || '.xml', 'ONLINE'
  FROM granule g
  WHERE g.dataset_id = var_dataset_id;

  INSERT INTO granule_archive (granule_id, type, file_size, compress_flag, checksum, path, status)
  SELECT g.granule_id, 'CHECKSUM', 0, 'N', NULL, var_base_path || '/' || TO_CHAR(g.start_time, 'YYYY') || '/' || TO_CHAR(g.start_time, 'DDD') || '/FR-' || SUBSTR(g.name, 1, INSTR(g.name, '.nc', 1) - 1) || '.xml.md5', 'ONLINE'
  FROM granule g
  WHERE g.dataset_id = var_dataset_id;

  END LOOP;
  CLOSE DCursor;
END;
/
