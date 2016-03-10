/*
** Copyright (c) 2008, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: load_ghrsst_fr_data_3.sql 5003 2010-06-02 15:45:26Z gangl $
*/

/*
** This script loads the GHRSST data from the fr_mmr database into 
** the Inventory schema. This script requires a schema named 
** "ghrsst_migration" to be located on the same Oracle database server 
** which contains the GHRSST dsd_mmr and fr_mmr schemas.
*/

/*
** granule_contact
**
** For some reason, this statement is not picking up the same number of hits 
** as in the GHRSST database. We are about 80,000 short. Need to investigate
** more. It may be because there are entries in fr_personnel_id that don't 
** have a corresponding entry in fr_master.
*/

INSERT INTO granule_contact (granule_id, contact_id)
SELECT DISTINCT g.granule_id, c.contact_id
FROM granule g, contact c, ghrsst_migration.FR_PERSONNEL p, ghrsst_migration.FR_PERSONNEL_ID f
WHERE f.person_id=p.person_id AND c.role=p.role AND c.first_name=p.first_name AND c.last_name=p.last_name AND c.email=p.email AND c.phone=p.phone AND c.fax=p.fax AND c.address=TO_CHAR(p.address) AND f.fr_id=g.granule_id;

/* 
** granule_meta_history
*/

INSERT INTO granule_meta_history (granule_id, version_id, creation_date_long, last_revision_date_long, revision_history, echo_submit_date_long)
SELECT g.granule_id, fr.meta_version, inventory.timestampToLong(fr.creation_date), inventory.timestampToLong(fr.last_revision_date), fr.revision_history, 0
FROM granule g, ghrsst_migration.fr_metadata_history fr
WHERE g.granule_id=fr.fr_id;

/* 
** granule_real
**
** Load the lat/lon values into the granule_real table.
*/
INSERT INTO granule_real (granule_id, de_id, value)
SELECT g.granule_id,(select de_id from dataset_element d where d.dataset_id=g.dataset_id and d.element_id in (select element_id from element_dd where short_name='westernmostLongitude')), fr.w_long
FROM granule g, ghrsst_migration.fr_spatial_coverage fr
WHERE g.granule_id=fr.fr_id;

INSERT INTO granule_real (granule_id, de_id, value)
SELECT g.granule_id,(select de_id from dataset_element d where d.dataset_id=g.dataset_id and d.element_id in (select element_id from element_dd where short_name='easternmostLongitude')), fr.e_long
FROM granule g, ghrsst_migration.fr_spatial_coverage fr
WHERE g.granule_id=fr.fr_id;


INSERT INTO granule_real (granule_id, de_id, value)
SELECT g.granule_id,(select de_id from dataset_element d where d.dataset_id=g.dataset_id and d.element_id in (select element_id from element_dd where short_name='southernmostLatitude')), fr.s_lat
FROM granule g, ghrsst_migration.fr_spatial_coverage fr
WHERE g.granule_id=fr.fr_id;

INSERT INTO granule_real (granule_id, de_id, value)
SELECT g.granule_id,(select de_id from dataset_element d where d.dataset_id=g.dataset_id and d.element_id in (select element_id from element_dd where short_name='northernmostLatitude')), fr.n_lat
FROM granule g, ghrsst_migration.fr_spatial_coverage fr
WHERE g.granule_id=fr.fr_id;

/*
** granule_reference
*/

INSERT INTO granule_reference (granule_id, path, type, status, description)
SELECT g.granule_id, u.url,'LOCAL-FTP', 'ONLINE', u.description
FROM granule g, ghrsst_migration.fr_related_url u
WHERE g.granule_id = u.fr_id AND u.url LIKE '%podaac%' AND (u.url_content_type LIKE 'FTP%' OR u.url_content_type LIKE 'Ftp%');

INSERT INTO granule_reference (granule_id, path, type, status, description)
SELECT g.granule_id, u.url,'LOCAL-FTP', 'ONLINE', u.description
FROM granule g, ghrsst_migration.fr_related_url u
WHERE g.granule_id = u.fr_id AND u.url LIKE 'ftp%' AND u.url LIKE '%podaac%' AND u.url_content_type is NULL;

INSERT INTO granule_reference (granule_id, path, type, status, description)
SELECT g.granule_id, u.url,'REMOTE-FTP', 'ONLINE', u.description
FROM granule g, ghrsst_migration.fr_related_url u
WHERE g.granule_id = u.fr_id AND u.url NOT LIKE '%podaac%' AND (u.url_content_type LIKE 'FTP%' OR u.url_content_type LIKE 'Ftp%');

INSERT INTO granule_reference (granule_id, path, type, status, description)
SELECT g.granule_id, u.url,'REMOTE-FTP', 'ONLINE', u.description
FROM granule g, ghrsst_migration.fr_related_url u
WHERE g.granule_id = u.fr_id AND u.url LIKE 'ftp%' AND u.url NOT LIKE '%podaac%' AND u.url_content_type is NULL;

INSERT INTO granule_reference (granule_id, path, type, status, description)
SELECT g.granule_id, u.url,'LOCAL-OPENDAP', 'ONLINE', u.description
FROM granule g, ghrsst_migration.fr_related_url u
WHERE g.granule_id = u.fr_id AND u.url LIKE '%podaac%' AND (u.url_content_type LIKE 'OpenDAP%' OR u.url_content_type LIKE 'DODS%');

INSERT INTO granule_reference (granule_id, path, type, status, description)
SELECT g.granule_id, u.url,'REMOTE-OPENDAP', 'ONLINE', u.description
FROM granule g, ghrsst_migration.fr_related_url u
WHERE g.granule_id = u.fr_id AND u.url NOT LIKE '%podaac%' AND (u.url_content_type LIKE 'OpenDAP%' OR u.url_content_type LIKE 'DODS%');

INSERT INTO granule_reference (granule_id, path, type, status, description)
SELECT g.granule_id, u.url,'LOCAL-HTTP', 'ONLINE', u.description
FROM granule g, ghrsst_migration.fr_related_url u
WHERE g.granule_id = u.fr_id AND u.url LIKE 'http%' AND u.url LIKE '%jpl%' AND u.url_content_type is NULL;

INSERT INTO granule_reference (granule_id, path, type, status, description)
SELECT g.granule_id, u.url,'REMOTE-HTTP', 'ONLINE', u.description
FROM granule g, ghrsst_migration.fr_related_url u
WHERE g.granule_id = u.fr_id AND u.url LIKE 'http%' AND u.url NOT LIKE '%jpl%' AND u.url_content_type is NULL;

UPDATE granule_reference set path = 'http://www.ncdc.noaa.gov/oa/climate/research/sst/oi-daily.php'
WHERE path = 'http://http://www.ncdc.noaa.gov/oa/climate/research/sst/oi-daily.php';

COMMIT;
