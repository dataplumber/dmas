/*
** Copyright (c) 2008, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: load_ghrsst_fr_data_1.sql 4477 2010-01-20 18:00:11Z gangl $
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
** Drop the previously created sequence and load the granule table. Load
** granules that are associated with renamed datasets and associate them
** with the correct dataset. Recreate the sequence.
*/

DROP SEQUENCE granule_id_seq;

/* All granules that match existing datasets. */

INSERT INTO granule (granule_id, dataset_id, name, start_time_long,stop_time_long,ingest_time_long,archive_time_long,create_time_long,verify_time_long,version, access_type, data_format, compress_type, checksum_type, status)
SELECT DISTINCT m.fr_id, d.dsd_id, m.file_name, inventory.timestampToLong(t.start_date), inventory.timestampToLong(t.stop_date), inventory.timestampToLong(m.ingest_date), inventory.timestampToLong(m.ingest_date), inventory.timestampToLong(m.file_release_date), 0, m.file_version, 'PUBLIC', 'NETCDF', 'UNKNOWN', 'MD5', 'ONLINE'
FROM ghrsst_migration.fr_master m, ghrsst_migration.fr_temporal_coverage t, ghrsst_migration.dsd_master d 
WHERE m.fr_entry_id=d.dsd_entry_id AND m.fr_id=t.fr_id
ORDER BY m.fr_id;

/* Dataset EUR-L4UHFnd-v01-MED was renamed to EUR-L4UHFnd-MED-v01. */

INSERT INTO granule (granule_id, dataset_id, name, start_time_long,stop_time_long,ingest_time_long,archive_time_long,create_time_long,verify_time_long,version, access_type, data_format, compress_type, checksum_type, status)
SELECT m.fr_id, d.dsd_id, m.file_name, inventory.timestampToLong(t.start_date), inventory.timestampToLong(t.stop_date), inventory.timestampToLong(m.ingest_date), inventory.timestampToLong(m.ingest_date), inventory.timestampToLong(m.file_release_date), 0, m.file_version, 'PUBLIC', 'NETCDF', 'UNKNOWN', 'MD5', 'ONLINE'
FROM ghrsst_migration.fr_master m, ghrsst_migration.fr_temporal_coverage t, ghrsst_migration.dsd_master d 
WHERE m.fr_entry_id = 'EUR-L4UHFnd-v01-MED' AND m.fr_id = t.fr_id AND d.dsd_entry_id = 'EUR-L4UHFnd-MED-v01'
ORDER BY m.fr_id;

/* Dataset REMSS-L2P-AMSRE_SST was renamed to REMSS-L2P-AMSRE. */

INSERT INTO granule (granule_id, dataset_id, name, start_time_long,stop_time_long,ingest_time_long,archive_time_long,create_time_long,verify_time_long,version, access_type, data_format, compress_type, checksum_type, status)
SELECT m.fr_id, d.dsd_id, m.file_name, inventory.timestampToLong(t.start_date), inventory.timestampToLong(t.stop_date), inventory.timestampToLong(m.ingest_date), inventory.timestampToLong(m.ingest_date), inventory.timestampToLong(m.file_release_date), 0, m.file_version, 'PUBLIC', 'NETCDF', 'UNKNOWN', 'MD5', 'ONLINE'
FROM ghrsst_migration.fr_master m, ghrsst_migration.fr_temporal_coverage t, ghrsst_migration.dsd_master d 
WHERE m.fr_entry_id = 'REMSS-L2P-AMSRE_SST' AND m.fr_id = t.fr_id AND d.dsd_entry_id = 'REMSS-L2P-AMSRE'
ORDER BY m.fr_id;

/* Dataset REMSS-L2P-TMI_SST was renamed to REMSS-L2P-TMI. */

INSERT INTO granule (granule_id, dataset_id, name, start_time_long,stop_time_long,ingest_time_long,archive_time_long,create_time_long,verify_time_long,version, access_type, data_format, compress_type, checksum_type, status)
SELECT m.fr_id, d.dsd_id, m.file_name, inventory.timestampToLong(t.start_date), inventory.timestampToLong(t.stop_date), inventory.timestampToLong(m.ingest_date), inventory.timestampToLong(m.ingest_date), inventory.timestampToLong(m.file_release_date), 0, m.file_version, 'PUBLIC', 'NETCDF', 'UNKNOWN', 'MD5', 'ONLINE'
FROM ghrsst_migration.fr_master m, ghrsst_migration.fr_temporal_coverage t, ghrsst_migration.dsd_master d 
WHERE m.fr_entry_id = 'REMSS-L2P-TMI_SST' AND m.fr_id = t.fr_id AND d.dsd_entry_id = 'REMSS-L2P-TMI'
ORDER BY m.fr_id;

/* Dataset REMSS-L4HRfnd-GLOB-mw_ir_rt_OI not defined in MMR. */

INSERT INTO granule (granule_id, dataset_id, name, start_time_long,stop_time_long,ingest_time_long,archive_time_long,create_time_long,verify_time_long,version, access_type, data_format, compress_type, checksum_type, status)
SELECT m.fr_id, d.dataset_id, m.file_name, inventory.timestampToLong(t.start_date), inventory.timestampToLong(t.stop_date), inventory.timestampToLong(m.ingest_date), inventory.timestampToLong(m.ingest_date), inventory.timestampToLong(m.file_release_date), 0, m.file_version, 'PUBLIC', 'NETCDF', 'GZIP', 'MD5', 'ONLINE'
FROM ghrsst_migration.fr_master m, ghrsst_migration.fr_temporal_coverage t, dataset d 
WHERE m.fr_entry_id = 'REMSS-L4HRfnd-GLOB-mw_ir_rt_OI' AND m.fr_id = t.fr_id AND d.short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_rt_OI'
ORDER BY m.fr_id;

/* Dataset UPA-L2P-ATS-NR_2P not defined in MMR. */

INSERT INTO granule (granule_id, dataset_id, name, start_time_long,stop_time_long,ingest_time_long,archive_time_long,create_time_long,verify_time_long,version, access_type, data_format, compress_type, checksum_type, status)
SELECT m.fr_id, d.dataset_id, m.file_name, inventory.timestampToLong(t.start_date), inventory.timestampToLong(t.stop_date), inventory.timestampToLong(m.ingest_date), inventory.timestampToLong(m.ingest_date), inventory.timestampToLong(m.file_release_date), 0, m.file_version, 'PUBLIC', 'NETCDF', 'BZIP2', 'MD5', 'ONLINE'
FROM ghrsst_migration.fr_master m, ghrsst_migration.fr_temporal_coverage t, dataset d 
WHERE m.fr_entry_id = 'UPA-L2P-ATS-NR_2P' AND m.fr_id = t.fr_id AND d.short_name = 'UPA-L2P-ATS-NR_2P'
ORDER BY m.fr_id;

CREATE SEQUENCE granule_id_seq
  START WITH 1600000
  CACHE 100;

COMMIT;

 
