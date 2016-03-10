/*
** Copyright (c) 2007-2008, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: load_ghrsst_dsd_data_5.sql 4477 2010-01-20 18:00:11Z gangl $
*/

/*
** This script loads the GHRSST data from the dsd_mmr database into 
** the Inventory schema. This script requires a schema named 
** "ghrsst_migration" to be located on the same Oracle database server 
** which contains the GHRSST dsd_mmr and fr_mmr schemas.
*/

/*
** contact
**
** Drop the temp table and relate Ed's entries to the PO.DAAC provider.
*/

DROP TABLE contact_temp CASCADE CONSTRAINTS PURGE;

UPDATE contact SET provider_id = 
  (SELECT provider_id
  FROM provider
  WHERE short_name = 'PO.DAAC')
WHERE last_name = 'Armstrong';

/*
** dataset
**
** Drop the previously created sequence, load the dataset table and
** recreate the sequence. We need to compare on both provider.short_name
** and provider.long_name because of the current duplication with short_name
** values.
*/
DROP SEQUENCE dataset_id_seq;

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description) 
SELECT m.dsd_id, i.provider_id, m.dsd_entry_id, m.entry_title, c.originating_center, d.data_set_id, NULL, l.location_name, l.detailed_location, r.latitude_resolution, r.longitude_resolution, NULL, r.altitude_resolution, r.depth_resolution, r.temporal_resolution, NULL, p.ellipsoid_type, p.projection_type, p.other_projection_details, s.reference, s.summary
FROM ghrsst_migration.dsd_data_center d, ghrsst_migration.dsd_master m, ghrsst_migration.dsd_constraints c, ghrsst_migration.dsd_location l, ghrsst_migration.dsd_data_resolution r, ghrsst_migration.dsd_projection_information p, ghrsst_migration.dsd_summary s, provider i
WHERE d.dsd_id=m.dsd_id AND m.dsd_id=c.dsd_id AND c.dsd_id=l.dsd_id AND l.dsd_id=r.dsd_id AND r.dsd_id=p.dsd_id AND p.dsd_id=s.dsd_id AND i.short_name = d.short_name AND i.long_name = d.long_name;

CREATE SEQUENCE dataset_id_seq
  START WITH 43
  NOCACHE;

/*
** Update the provider association for each dataset that references the
** duplicate providers. Delete the duplicate providers and their resources.
*/

UPDATE dataset SET provider_id =
  (SELECT provider_id
   FROM provider
   WHERE short_name = 'IFREMER/CERSAT')
WHERE provider_id = 
  (SELECT provider_id
   FROM provider
   WHERE short_name = 'Medspiration IFREMER / CERSAT' AND long_name = 'Francais de Recherche pour l''Exploitation de la Mer / Centre ERS d''Archivage et de Traitement');

UPDATE dataset SET provider_id =
  (SELECT provider_id
   FROM provider
   WHERE short_name = 'IFREMER/CERSAT')
WHERE provider_id = 
  (SELECT provider_id
   FROM provider
   WHERE short_name = 'Medspiration IFREMER / CERSAT' AND long_name = 'Institute Francais de Recherche pour l''Exploitation de la Mer / Centre ERS d''Archivage et de Traitement');

DELETE FROM provider_resource
WHERE provider_id = 
  (SELECT provider_id
   FROM provider
   WHERE short_name = 'Medspiration IFREMER / CERSAT' AND long_name = 'Francais de Recherche pour l''Exploitation de la Mer / Centre ERS d''Archivage et de Traitement');

DELETE FROM provider_resource
WHERE provider_id = 
  (SELECT provider_id
   FROM provider
   WHERE short_name = 'Medspiration IFREMER / CERSAT' AND long_name = 'Institute Francais de Recherche pour l''Exploitation de la Mer / Centre ERS d''Archivage et de Traitement');

DELETE FROM provider
WHERE short_name = 'Medspiration IFREMER / CERSAT';

/*
** dataset_citation
*/

INSERT INTO dataset_citation (dataset_id, title, creator, version, publisher, series_name, release_date_long, release_place, citation_detail, online_resource)
SELECT dsd_id, dataset_title, dataset_creator, version, dataset_publisher, dataset_series_name, inventory.timestampToLong(dataset_release_date), dataset_release_place, other_citation_details, online_resources 
FROM ghrsst_migration.dsd_data_set_citation;

/*
** dataset_contact
**
** For some reason, this statement is not picking up the same number of hits 
** as in the GHRSST database. The following statements clean up those missing
** entries.
*/

INSERT INTO dataset_contact (dataset_id, contact_id)
SELECT i.dsd_id, c.contact_id
FROM ghrsst_migration.DSD_PERSONNEL_ID i, contact c, ghrsst_migration.DSD_PERSONNEL p
WHERE i.person_id=p.person_id AND c.role=p.role AND c.first_name=p.first_name AND c.last_name=p.last_name AND c.email=p.email AND c.phone=p.phone AND c.fax=p.fax AND c.address=TO_CHAR(p.address);

INSERT INTO dataset_contact (dataset_id, contact_id)
SELECT d.dataset_id, c.contact_id
FROM dataset d, contact c
WHERE d.short_name = 'OSDPD-L2P-GOES11' AND c.role = 'Technical Contact' AND c.first_name = 'Robert' AND c.last_name = 'Potash';

INSERT INTO dataset_contact (dataset_id, contact_id)
SELECT d.dataset_id, c.contact_id
FROM dataset d, contact c
WHERE d.short_name = 'OSDPD-L2P-GOES12' AND c.role = 'Technical Contact' AND c.first_name = 'Robert' AND c.last_name = 'Potash';

INSERT INTO dataset_contact (dataset_id, contact_id)
SELECT d.dataset_id, c.contact_id
FROM dataset d, contact c
WHERE d.short_name = 'NAVO-L4HR1m-GLOB-K10_SST' AND role = 'Technical Contact' AND first_name = 'Doug' AND last_name = 'May' AND email = 'doug.may@navy.mil';

/*
** dataset_coverage
*/

INSERT INTO dataset_coverage (dataset_id, start_time_long,stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth) 
SELECT s.dsd_id, inventory.timestampToLong(t.start_date),inventory.timestampToLong( t.stop_date), s.n_lat, s.s_lat, s.e_long, s.w_long, s.min_altitude, s.max_altitude, s.min_depth, s.max_depth 
FROM ghrsst_migration.dsd_temporal_coverage t, ghrsst_migration.dsd_spatial_coverage s
WHERE t.dsd_id=s.dsd_id;

/*
** dataset_resource
*/

INSERT INTO dataset_resource (dataset_id, name, path, type, description) 
SELECT dsd_id, NULL, url, url_content_type,description 
FROM ghrsst_migration.dsd_related_url;

/*
** dataset_meta_history
*/

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history) 
SELECT dsd_id, meta_version, inventory.timestampToLong(creation_date), inventory.timestampToLong(last_revision_date), revision_history 
FROM ghrsst_migration.dsd_metadata_history;

/*
** dataset_parameter
*/

INSERT INTO dataset_parameter (dataset_id, category,topic, term, variable, variable_detail)
SELECT dsd_id, category,topic, term, variable, detailed_variable 
FROM ghrsst_migration.dsd_parameters;

/*
** dataset_policy
*/

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
SELECT d.dataset_id, 'UNKNOWN', NULL, NULL, NULL, 'UNKNOWN', 'UNKNOWN', 'UNKNOWN', 'UNKNOWN', 'UNKNOWN', 'UNKNOWN', c.access_constraints, c.use_constraints
FROM dataset d, ghrsst_migration.dsd_constraints c
WHERE c.dsd_id=d.dataset_id;

/*
** Commit all of the above inserts.
*/
COMMIT;

 
