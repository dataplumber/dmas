UPDATE granule set create_time=(create_time - interval '8' HOUR) where ingest_time >= TO_DATE('01-NOV-2009 02.00.00', 'dd-MON-YYYY HH24.MI.SS') AND (dataset_id in (select dataset_id from dataset where short_name like '%ASCAT%'));
UPDATE granule set create_time=(create_time - interval '7' HOUR) where ingest_time < TO_DATE('01-NOV-2009 02.00.00', 'dd-MON-YYYY HH24.MI.SS') AND (dataset_id in (select dataset_id from dataset where short_name like '%ASCAT%'));


UPDATE granule set start_time=(start_time - interval '8' HOUR) where ingest_time >= TO_DATE('01-NOV-2009 02.00.00', 'dd-MON-YYYY HH24.MI.SS') AND (dataset_id in (select dataset_id from dataset where short_name like '%ASCAT%'));
UPDATE granule set start_time=(start_time - interval '7' HOUR) where ingest_time < TO_DATE('01-NOV-2009 02.00.00', 'dd-MON-YYYY HH24.MI.SS') AND (dataset_id in (select dataset_id from dataset where short_name like '%ASCAT%'));

UPDATE granule set stop_time=(stop_time - interval '8' HOUR) where ingest_time >= TO_DATE('01-NOV-2009 02.00.00', 'dd-MON-YYYY HH24.MI.SS') AND (dataset_id in (select dataset_id from dataset where short_name like '%ASCAT%'));
UPDATE granule set stop_time=(stop_time - interval '7' HOUR) where ingest_time < TO_DATE('01-NOV-2009 02.00.00', 'dd-MON-YYYY HH24.MI.SS') AND (dataset_id in (select dataset_id from dataset where short_name like '%ASCAT%'));

/*
GHRSST TIME
*/

UPDATE granule set create_time=(create_time - interval '8' HOUR) where granule_id >= 2185832 AND ingest_time >= TO_DATE('01-NOV-2009 02.00.00', 'dd-MON-YYYY HH24.MI.SS') AND (dataset_id in (select dataset_id from collection_dataset, collection where collection.collection_id = collection_dataset.collection_id and short_name = 'GHRSST'));
UPDATE granule set create_time=(create_time - interval '7' HOUR) where granule_id >= 2185832 AND ingest_time < TO_DATE('01-NOV-2009 02.00.00', 'dd-MON-YYYY HH24.MI.SS') AND (dataset_id in (select dataset_id from collection_dataset, collection where collection.collection_id = collection_dataset.collection_id and short_name = 'GHRSST'));

UPDATE granule set start_time=(start_time - interval '8' HOUR) where granule_id >= 2185832 AND ingest_time >= TO_DATE('01-NOV-2009 02.00.00', 'dd-MON-YYYY HH24.MI.SS') AND (dataset_id in (select dataset_id from collection_dataset, collection where collection.collection_id = collection_dataset.collection_id and short_name = 'GHRSST'));
UPDATE granule set start_time=(start_time - interval '7' HOUR) where granule_id >= 2185832 AND ingest_time < TO_DATE('01-NOV-2009 02.00.00', 'dd-MON-YYYY HH24.MI.SS') AND (dataset_id in (select dataset_id from collection_dataset, collection where collection.collection_id = collection_dataset.collection_id and short_name = 'GHRSST'));

UPDATE granule set stop_time=(stop_time - interval '8' HOUR) where granule_id >= 2185832 AND ingest_time >= TO_DATE('01-NOV-2009 02.00.00', 'dd-MON-YYYY HH24.MI.SS') AND (dataset_id in (select dataset_id from collection_dataset, collection where collection.collection_id = collection_dataset.collection_id and short_name = 'GHRSST'));
UPDATE granule set stop_time=(stop_time - interval '7' HOUR) where granule_id >= 2185832 AND ingest_time < TO_DATE('01-NOV-2009 02.00.00', 'dd-MON-YYYY HH24.MI.SS') AND (dataset_id in (select dataset_id from collection_dataset, collection where collection.collection_id = collection_dataset.collection_id and short_name = 'GHRSST'));

UPDATE granule_meta_history set creation_date=(creation_date - interval '8' HOUR) where granule_id >= 2185832 AND granule_id in (select granule_id from granule where ingest_time >= TO_DATE('01-NOV-2009 02.00.00', 'dd-MON-YYYY HH24.MI.SS') AND (dataset_id in (select dataset_id from collection_dataset, collection where collection.collection_id = collection_dataset.collection_id and short_name = 'GHRSST')));
UPDATE granule_meta_history set creation_date=(creation_date - interval '7' HOUR) where granule_id >= 2185832 AND granule_id in (select granule_id from granule where granule.ingest_time < TO_DATE('01-NOV-2009 02.00.00', 'dd-MON-YYYY HH24.MI.SS') AND (dataset_id in (select dataset_id from collection_dataset, collection where collection.collection_id = collection_dataset.collection_id and short_name = 'GHRSST')));

UPDATE granule_meta_history set last_revision_date=(last_revision_date - interval '8' HOUR) where granule_id >= 2185832 AND granule_id in (select granule_id from granule where ingest_time >= TO_DATE('01-NOV-2009 02.00.00', 'dd-MON-YYYY HH24.MI.SS')) AND (granule_id in (select granule_id from granule where dataset_id in (select dataset_id from collection_dataset, collection where collection.collection_id = collection_dataset.collection_id and short_name = 'GHRSST')));
UPDATE granule_meta_history set last_revision_date=(last_revision_date - interval '7' HOUR) where granule_id >= 2185832 AND granule_id in (select granule_id from granule where ingest_time < TO_DATE('01-NOV-2009 02.00.00', 'dd-MON-YYYY HH24.MI.SS')) AND (granule_id in (select granule_id from granule where dataset_id in (select dataset_id from collection_dataset, collection where collection.collection_id = collection_dataset.collection_id and short_name = 'GHRSST')));


/*
FTP Links
*/

update dataset_location_policy set base_path=replace(base_path, 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/', 'ftp://podaac-ftp.jpl.nasa.gov/byProject/ghrsst/data/')  where base_path like 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/%';
update granule_reference set path=replace(path, 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/', 'ftp://podaac-ftp.jpl.nasa.gov/byProject/ghrsst/data/') where path like 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/%';

