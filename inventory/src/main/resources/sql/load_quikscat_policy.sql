/*
** Copyright (c) 2008-2009, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: load_quikscat_policy.sql 4686 2010-04-02 00:02:50Z gangl $
*/

/*
** This script loads the QuikSCAT dataset policy for the Inventory 
** schema.
*/

/*
** Collection
*/

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'QUIKSCAT', 'Quick Scatterometer', 'PROJECT', 'This collection references all of the datasets associated with the QuikSCAT project.');

/*
** Project
*/

INSERT INTO project (project_id, short_name, long_name)
VALUES (project_id_seq.nextval, 'QUIKSCAT', 'Quick Scatterometer');

/*
** Provider
*/

INSERT INTO provider (provider_id, short_name, long_name, type)
VALUES (provider_id_seq.nextval, 'SeaPAC', 'SeaWinds Processing and Analysis Center (SeaPAC)', 'DATA-PROVIDER');

INSERT INTO provider_resource (provider_id, path)
VALUES (provider_id_seq.currval, 'http://winds.jpl.nasa.gov/');

INSERT INTO contact (contact_id, role, first_name, middle_name, last_name, email, phone, fax, address, provider_id, notify_type)
VALUES (contact_id_seq.nextval, 'Technical Contact', 'Scott', NULL, 'Dunbar', 'Roy.S.Dunbar@jpl.nasa.gov', '818-354-8329', '818-393-5184', '4800 Oak Grove Drive Pasadena, CA 91109-8099', provider_id_seq.currval, 'SILENT');

/*
** Source and Sensor
*/

INSERT INTO source (source_id, short_name, long_name, type, orbit_period, incl_angle, description)
VALUES (source_id_seq.nextval, 'QUIKSCAT', 'Quick Recovery Scatterometer', 'SPACECRAFT', 100.93, 98.6, 'The SeaWinds on QuikSCAT mission is a "quick recovery" mission to fill the gap created by the loss of data from the NASA Scatterometer (NSCAT), when the satellite it was flying on lost power in June 1997. The SeaWinds instrument on the QuikSCAT satellite is a specialized microwave radar that measures near-surface wind speed and direction under all weather and cloud conditions over Earth''s oceans.');

INSERT INTO sensor (sensor_id, short_name, long_name, swath_width, description)
VALUES (sensor_id_seq.nextval, 'SEAWINDS', 'SeaWinds', 1800, 'SeaWinds uses a rotating dish antenna with two spot beams that sweep in a circular pattern. The antenna radiates microwave pulses at a frequency of 13.4 gigahertz across broad regions on the Earth''s surface. The instrument will collect data over ocean, land, and ice in a continuous, 1,800-kilometer-wide band, making approximately 400,000 measurements and covering 90% of the Earth''s surface in one day.');

/*
** Datasets
*/

/*
** QSCAT_ANCILLARY
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'QSCAT_ANCILLARY', 'QuikSCAT Ancillary Data - Detailed', 'SeaWinds Processing and Analysis Center (SeaPAC)', 'QSCATLOD, QSCAT0QA, QSCATL1AQ, QSCATATT, QSCATCAL, QSCATICEM, QSCATEPHG, QSCATNWP1, QSCATNWP2, QSCATQARPT, QSCATREVTIME, QSCATTCD', NULL, 'N/A', NULL, 'N/A', 'N/A', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('22-DEC-1998 10:39:31'), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('07-JUL-1999'), inventory.timestampToLong(SYSDATE), 'Dataset policy entry migrated from the PO.DAAC Ingres database.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-ONLY', NULL, NULL, NULL, 'PRIVATE', 'BATCH', 'RAW', 'NONE', 'MD5', 'NONE', 'UNKNOWN', 'UNKNOWN');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PRIVATE', 'file:///store/quikscat/private/anc');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Winds', 'Surface Winds', 'Ancillary');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, '2.5.1-1/2006-11-14', inventory.timestampToLong('14-NOV-2006'), 'Latest processor version.');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'QUIKSCAT_ANCILLARY', 'QuikSCAT Ancillary Data - Detailed', 'PRODUCT', 'TBD');

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 110, 0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'QUIKSCAT';

/*
** QSCAT_RAW_TELEMETRY
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'QSCAT_RAW_TELEMETRY', 'SeaWinds Instrument on QuikSCAT Raw Science (L0) Data - Detailed', 'SeaWinds Processing and Analysis Center (SeaPAC)', 'QSCATQL0, QSCATHK1, QSCATHK2', '0', 'Global Ocean', NULL, '25 km', '25 km', NULL, NULL, NULL, 'Pass', NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('07-JUL-1999 11:39:31'), NULL, 75.000, -75.000, 180.000, -180.000, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('07-JUL-1999'), inventory.timestampToLong(SYSDATE), 'Dataset policy entry migrated from the PO.DAAC Ingres database.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-ONLY', NULL, NULL, NULL, 'PRIVATE', 'BATCH', 'RAW', 'GZIP', 'MD5', 'NONE', 'UNKNOWN', 'UNKNOWN');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PRIVATE', 'file:///store/quikscat/private/raw');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Winds', 'Surface Winds', 'Data Number');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, '2.5.1-1/2006-11-14', inventory.timestampToLong('14-NOV-2006'), 'Latest processor version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'version';

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'QSCAT_L0_RAW_SCIENCE', 'SeaWinds Instrument on QuikSCAT Raw Science (L0) Data - Detailed', 'PRODUCT', 'TBD');

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 103, 0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'QUIKSCAT';

/*
** QSCAT_LEVEL_0
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'QSCAT_LEVEL_0', 'QuikSCAT L0 Raw Telemetry Data - Detailed', 'SeaWinds Processing and Analysis Center (SeaPAC)', 'QSCATL0, QSCATBP', '0', 'Global Ocean', NULL, '25 km', '25 km', NULL, NULL, NULL, 'Pass', NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('07-JUL-1999 11:39:31'), NULL, 75.000, -75.000, 180.000, -180.000, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('07-JUL-1999'), inventory.timestampToLong(SYSDATE), 'Dataset policy entry migrated from the PO.DAAC Ingres database.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-ONLY', NULL, NULL, NULL, 'PRIVATE', 'BATCH', 'RAW', 'GZIP', 'MD5', 'NONE', 'UNKNOWN', 'UNKNOWN');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PRIVATE', 'file:///store/quikscat/private/L0');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Winds', 'Surface Winds', 'Data Number');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, '2.5.1-1/2006-11-14', inventory.timestampToLong('14-NOV-2006'), 'Latest processor version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'version';

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'QSCAT_L0_RAW_TELEMETRY', 'QuikSCAT L0 Raw Telemetry Data - Detailed', 'PRODUCT', 'TBD');

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 104, 0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'QUIKSCAT';

/*
** QSCAT_LEVEL_1A
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'QSCAT_LEVEL_1A', 'QuikSCAT Level 1A Engineering Unit Converted Telemetry - Detailed', 'SeaWinds Processing and Analysis Center (SeaPAC)', 'QSCATL1A', '1A', 'Global Ocean', NULL, '25 km', '25 km', NULL, NULL, NULL, 'Orbit', NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('07-JUL-1999 11:39:31'), NULL, 75.000, -75.000, 180.000, -180.000, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('07-JUL-1999'), inventory.timestampToLong(SYSDATE), 'Dataset policy entry migrated from the PO.DAAC Ingres database.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-ONLY', NULL, NULL, NULL, 'PRIVATE', 'BATCH', 'HDF', 'GZIP', 'MD5', 'BACKTRACK', 'UNKNOWN', 'UNKNOWN');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PRIVATE', 'file:///store/quikscat/private/L1A');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Winds', 'Surface Winds', 'Engineering Units');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, '2.5.1-1/2006-11-14', inventory.timestampToLong('14-NOV-2006'), 'Latest processor version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'revolution' OR short_name = 'version';

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'QSCAT_L1A_ENGINEERING_UNIT_CONVERTED_TELEMETRY', 'QuikSCAT Level 1A Engineering Unit Converted Telemetry - Detailed', 'PRODUCT', 'TBD');

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 105, 0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'QUIKSCAT';

/*
** QSCAT_LEVEL_1B
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'QSCAT_LEVEL_1B', 'QuikSCAT Level 1B Time-Ordered Earth-Located Sigma0s - Detailed', 'SeaWinds Processing and Analysis Center (SeaPAC)', 'QSCATL1B', '1B', 'Global Ocean', NULL, '25 km', '25 km', NULL, NULL, NULL, 'Orbit', NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('07-JUL-1999 11:39:31'), NULL, 75.000, -75.000, 180.000, -180.000, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('07-JUL-1999'), inventory.timestampToLong(SYSDATE), 'Dataset policy entry migrated from the PO.DAAC Ingres database.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'RESTRICTED', 'YEAR-DOY', 'HDF', 'GZIP', 'MD5', 'BACKTRACK', 'UNKNOWN', 'UNKNOWN');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-RESTRICTED', 'file:///store/quikscat/restricted/L1B/data/L1B');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Winds', 'Surface Winds', 'Sigma-Naught');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, '2.5.1-1/2006-11-14', inventory.timestampToLong('14-NOV-2006'), 'Latest processor version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'revolution' OR short_name = 'version';

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'QSCAT_L1B_TIME-ORDERED_EARTH-LOCATED_SIGMA0S', 'QuikSCAT Level 1B Time-Ordered Earth-Located Sigma0s - Detailed', 'PRODUCT', 'TBD');

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 106, 0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'QUIKSCAT';

/*
** QSCAT_LEVEL_1B_QA_REPORT
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'QSCAT_LEVEL_1B_QA_REPORT', 'QuikSCAT Level 1B Time-Ordered Earth-Located Sigma0s QA Report', 'SeaWinds Processing and Analysis Center (SeaPAC)', 'QSCATL1BQ', NULL, 'N/A', NULL, 'N/A', 'N/A', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('07-JUL-1999 11:39:31'), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('07-JUL-1999'), inventory.timestampToLong(SYSDATE), 'Dataset policy entry created for the new database.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'RESTRICTED', 'YEAR-DOY', 'RAW', 'GZIP', 'MD5', 'NONE', 'UNKNOWN', 'UNKNOWN');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-RESTRICTED', 'file:///store/quikscat/restricted/L1B/data/Q1B');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Winds', 'Surface Winds', 'Ancillary');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, '2.5.1-1/2006-11-14', inventory.timestampToLong('14-NOV-2006'), 'Latest processor version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'version';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'QUIKSCAT';

/*
** QSCAT_LEVEL_2A
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'QSCAT_LEVEL_2A', 'QuikSCAT Level 2A Surface Flagged Sigma0 and Atten. 25Km Swath Grid - Detailed', 'SeaWinds Processing and Analysis Center (SeaPAC)', 'QSCATL2A_PULS', '2A', 'Global Ocean', NULL, '25 km', '25 km', NULL, NULL, NULL, 'Orbit', NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('07-JUL-1999 11:39:31'), NULL, 75.000, -75.000, 180.000, -180.000, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('07-JUL-1999'), inventory.timestampToLong(SYSDATE), 'Dataset policy entry migrated from the PO.DAAC Ingres database.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'RESTRICTED', 'YEAR-DOY', 'HDF', 'GZIP', 'MD5', 'BACKTRACK', 'UNKNOWN', 'UNKNOWN');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-RESTRICTED', 'file:///store/quikscat/restricted/L2A/data/L2A');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Winds', 'Surface Winds', 'Sigma-Naught');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, '2.5.1-1/2006-11-14', inventory.timestampToLong('14-NOV-2006'), 'Latest processor version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'revolution' OR short_name = 'version';

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'QSCAT_L2A_SURFACE_FLAGGED_SIGMA0S_AND_ATTEN_25KM_SWATH_GRID', 'QuikSCAT Level 2A Surface Flagged Sigma0 and Atten. 25Km Swath Grid - Detailed', 'PRODUCT', 'TBD');

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 107, 0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'QUIKSCAT';

/*
** QSCAT_LEVEL_2A_QA_REPORT
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'QSCAT_LEVEL_2A_QA_REPORT', 'QuikSCAT Level 2A Surface Flagged Sigma0 and Atten. 25Km Swath Grid QA Report', 'SeaWinds Processing and Analysis Center (SeaPAC)', 'QSCATL2AQ_PULS', NULL, 'N/A', NULL, 'N/A', 'N/A', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('07-JUL-1999 11:39:31'), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('07-JUL-1999'), inventory.timestampToLong(SYSDATE), 'Dataset policy entry created for the new database.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'RESTRICTED', 'YEAR-DOY', 'RAW', 'GZIP', 'MD5', 'NONE', 'UNKNOWN', 'UNKNOWN');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-RESTRICTED', 'file:///store/quikscat/restricted/L2A/data/Q2A');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Winds', 'Surface Winds', 'Ancillary');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, '2.5.1-1/2006-11-14', inventory.timestampToLong('14-NOV-2006'), 'Latest processor version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'version';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'QUIKSCAT';

/*
** QSCAT_LEVEL_2A_COMP_12
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'QSCAT_LEVEL_2A_COMP_12', 'QuikSCAT L2A 12.5 km - Dataset', 'SeaWinds Processing and Analysis Center (SeaPAC)', 'QSCATL2A_CP12', '2A', 'Global Ocean', NULL, '12.5 km', '12.5 km', NULL, NULL, NULL, 'Orbit', NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('07-JUL-1999 11:39:31'), NULL, 75.000, -75.000, 180.000, -180.000, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('07-JUL-1999'), inventory.timestampToLong(SYSDATE), 'Dataset policy entry migrated from the PO.DAAC Ingres database.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'RESTRICTED', 'YEAR-DOY', 'HDF', 'GZIP', 'MD5', 'BACKTRACK', 'UNKNOWN', 'UNKNOWN');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-RESTRICTED', 'file:///store/quikscat/restricted/L2A/data/L2A12');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Winds', 'Surface Winds', 'Sigma-Naught');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, '2.5.1-1/2006-11-14', inventory.timestampToLong('14-NOV-2006'), 'Latest processor version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'revolution' OR short_name = 'version';

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'QSCAT_L2A_12.5KM', 'QuikSCAT L2A 12.5 km - Dataset', 'PRODUCT', 'TBD');

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 285, 0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'QUIKSCAT';

/*
** QSCAT_LEVEL_2A_COMP_12_QA_REPORT
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'QSCAT_LEVEL_2A_COMP_12QA_REPORT', 'QuikSCAT L2A 12.5 km QA Report', 'SeaWinds Processing and Analysis Center (SeaPAC)', 'QSCATL2AQ_CP12', NULL, 'N/A', NULL, 'N/A', 'N/A', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('07-JUL-1999 11:39:31'), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('07-JUL-1999'), inventory.timestampToLong(SYSDATE), 'Dataset policy entry created for the new database.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'RESTRICTED', 'YEAR-DOY', 'RAW', 'GZIP', 'MD5', 'NONE', 'UNKNOWN', 'UNKNOWN');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-RESTRICTED', 'file:///store/quikscat/restricted/L2A/data/Q2A12');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Winds', 'Surface Winds', 'Ancillary');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, '2.5.1-1/2006-11-14', inventory.timestampToLong('14-NOV-2006'), 'Latest processor version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'version';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'QUIKSCAT';

/*
** QSCAT_LEVEL_2B
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'QSCAT_LEVEL_2B', 'QuikSCAT Level 2B Ocean Wind Vectors in 25 Km Swath Grid - Detailed', 'SeaWinds Processing and Analysis Center (SeaPAC)', 'QSCATL2B_PULS', '2B', 'Global Ocean', NULL, '25 km', '25 km', NULL, NULL, NULL, 'Orbit', NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('07-JUL-1999 11:39:31'), NULL, 75.000, -75.000, 180.000, -180.000, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('07-JUL-1999'), inventory.timestampToLong(SYSDATE), 'Dataset policy entry migrated from the PO.DAAC Ingres database.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'PUBLIC', 'YEAR-DOY', 'HDF', 'GZIP', 'MD5', 'BACKTRACK', 'UNKNOWN', 'UNKNOWN');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PUBLIC', 'file:///store/quikscat/public/L2B/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/ocean_wind/quikscat/L2B/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/ocean_wind/quikscat/L2B/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Winds', 'Surface Winds', 'Ocean Wind Speed');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Winds', 'Surface Winds', 'Ocean Wind Vectors');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, '2.5.1-1/2006-11-14', inventory.timestampToLong('14-NOV-2006'), 'Latest processor version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'revolution' OR short_name = 'version';

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'QSCAT_L2B_OCEAN_WIND_25KM', 'SeaWinds on QuikSCAT Level 2B Ocean Wind Vectors in 25 Km Swath Grid', 'PRODUCT', 'The SeaWinds on QuikSCAT Level 2B data set consists of wind vector solutions for all four wind vector ambiguities as well as the Direction Interval Retrieval with Threshold Nudging (DIRTH) wind vector solution. Each Level 2B file contains data for one full orbital revolution of the spacecraft or rev. These data are grouped by rows of 25km wind vector cells (WVC). Each row contains a total of 76 WVC values and corresponds to a single cross-track cut of the SeaWinds swath. 1624 wind vector cell rows are required to complete a rev. Data are currently available in Hierarchical Data Format (HDF) and exist from 19 July 1999 to present.' || CHR(10) || CHR(10) || 'The Level 2B data are derived from QuikSCAT Level 2A sigma-0 measurements. When rain is present, the measurements of the ocean surface sigma-0 are affected in several ways. The radar signal may be scattered by the raindrops and never reach the ocean''s surface. The radar signal may be attenuated by the rain as it travels to and from the Earth''s surface. The roughness of the sea surface due to the splashing of the raindrops may also affect the sigma-0 measurement and thus the wind vector solution. In an effort to indicate the presence of rain, the Level 2B data include the Multidimensional Histogram Rain Flag SDS (mp_rain_probability) and the Normalized Objective Function Rain Flag SDS (nof_rain_index). In addition, wvc_quality_flag bit 13 indicates whether or not the rain flag algorithm indicates rain.' || CHR(10) || CHR(10) || 'These data are available via anonymous FTP. Documentation, read software, and further information may also be obtained from the PO.DAAC QuikSCAT Web site, http://podaac.jpl.nasa.gov/quikscat/.');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'David' AND last_name = 'Moroni';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 108, 0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'QUIKSCAT';

/*
** QSCAT_LEVEL_2B_QA_REPORT
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'QSCAT_LEVEL_2B_QA_REPORT', 'QuikSCAT Level 2B Ocean Wind Vectors in 25 Km Swath Grid QA Report', 'SeaWinds Processing and Analysis Center (SeaPAC)', 'QSCATL2BQ_PULS', NULL, 'N/A', NULL, 'N/A', 'N/A', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('07-JUL-1999 11:39:31'), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('07-JUL-1999'), inventory.timestampToLong(SYSDATE), 'Dataset policy entry created for the new database.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'PUBLIC', 'YEAR-DOY', 'RAW', 'GZIP', 'MD5', 'NONE', 'UNKNOWN', 'UNKNOWN');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PUBLIC', 'file:///store/quikscat/public/L2B/Q2B');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/ocean_wind/quikscat/L2B/Q2B');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Winds', 'Surface Winds', 'Ancillary');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, '2.5.1-1/2006-11-14', inventory.timestampToLong('14-NOV-2006'), 'Latest processor version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'version';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'QUIKSCAT';

/*
** QSCAT_LEVEL_2B_COMP_12
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'QSCAT_LEVEL_2B_COMP_12', 'QuikSCAT L2B 12.5 km - Dataset', 'SeaWinds Processing and Analysis Center (SeaPAC)', 'QSCATL2B_CP12', '2B', 'Global Ocean', NULL, '12.5 km', '12.5 km', NULL, NULL, NULL, 'Orbit', NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('07-JUL-1999 11:39:31'), NULL, 75.000, -75.000, 180.000, -180.000, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('07-JUL-1999'), inventory.timestampToLong(SYSDATE), 'Dataset policy entry migrated from the PO.DAAC Ingres database.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'PUBLIC', 'YEAR-DOY', 'HDF', 'GZIP', 'MD5', 'BACKTRACK', 'UNKNOWN', 'UNKNOWN');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PUBLIC', 'file:///store/quikscat/public/L2B12/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/ocean_wind/quikscat/L2B12/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/ocean_wind/quikscat/L2B12/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Winds', 'Surface Winds', 'Ocean Wind Speed');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Winds', 'Surface Winds', 'Ocean Wind Vectors');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, '2.5.1-1/2006-11-14', inventory.timestampToLong('14-NOV-2006'), 'Latest processor version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'revolution' OR short_name = 'version';

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'QSCAT_L2B_OCEAN_WIND_12.5KM', 'SeaWinds on QuikSCAT Level 2B Ocean Wind Vectors in 12.5 Km Swath Grid', 'PRODUCT', 'This product is very similar to Product 108, QuikSCAT Level 2B Ocean Wind Vectors, 25km Swath, except that the resolution has improved from 25km to 12.5km.' || CHR(10) || CHR(10) || 'These data are available via anonymous FTP to podaac.jpl.nasa.gov in the pub/ocean_wind/quikscat/L2B12 directory. These data are also available via aspera (HVDT) http://aspera.jpl.nasa.gov/download/pub/ocean_wind/quikscat/L2B12. Documentation, read software, and further information may also be obtained from the PO.DAAC QuikSCAT Web site, http://podaac.jpl.nasa.gov/quikscat/.');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'David' AND last_name = 'Moroni';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 286, 0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'QUIKSCAT';

/*
** QSCAT_LEVEL_2B_COMP_12_QA_REPORT
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'QSCAT_LEVEL_2B_COMP_12_QA_REPORT', 'QuikSCAT L2B 12.5 km QA Report', 'SeaWinds Processing and Analysis Center (SeaPAC)', 'QSCATL2BQ_CP12', NULL, 'N/A', NULL, 'N/A', 'N/A', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('07-JUL-1999 11:39:31'), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('07-JUL-1999'), inventory.timestampToLong(SYSDATE), 'Dataset policy entry created for the new database.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'PUBLIC', 'YEAR-DOY', 'RAW', 'GZIP', 'MD5', 'NONE', 'UNKNOWN', 'UNKNOWN');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PUBLIC', 'file:///store/quikscat/public/L2B12/Q2B12');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/ocean_wind/quikscat/L2B12/Q2B12');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Winds', 'Surface Winds', 'Ancillary');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, '2.5.1-1/2006-11-14', inventory.timestampToLong('14-NOV-2006'), 'Latest processor version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'version';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'QUIKSCAT';

/*
** QSCAT_LEVEL_3
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'QSCAT_LEVEL_3', 'QuikSCAT Level 3 - Detailed', 'SeaWinds Processing and Analysis Center (SeaPAC)', 'QSCATL3', '3', 'Global Ocean', NULL, '0.25 degrees', '0.25 degrees', NULL, NULL, NULL, 'Daily', NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('07-JUL-1999 11:39:31'), NULL, 75.000, -75.000, 180.000, -180.000, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('07-JUL-1999'), inventory.timestampToLong(SYSDATE), 'Dataset policy entry migrated from the PO.DAAC Ingres database.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'PUBLIC', 'YEAR', 'HDF', 'GZIP', 'MD5', 'ORACLE', 'UNKNOWN', 'UNKNOWN');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PUBLIC', 'file:///store/quikscat/public/L3/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/ocean_wind/quikscat/L3/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/ocean_wind/quikscat/L3/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Winds', 'Surface Winds', 'Ocean Wind Speed');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Winds', 'Surface Winds', 'Ocean Wind Vectors');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, '2.5.1-1/2006-11-14', inventory.timestampToLong('14-NOV-2006'), 'Latest processor version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'dayOfYearStart' OR short_name = 'version';

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'QSCAT_L3', 'SeaWinds on QuikSCAT Level 3 Daily Gridded Ocean Wind Vectors', 'PRODUCT', 'The SeaWinds on QuikSCAT Level 3 data set consists of gridded values of scalar wind speed, meridional and zonal components of wind velocity, wind speed squared and time given in fraction of a day. Rain probability determined using the Multidimensional Histogram (MUDH) Rain Flagging technique is also included as an indicator of wind values that may have degraded accuracy due to the presence of rain. Data are currently available in Hierarchical Data Format (HDF) and exist from 19 July 1999 to present.' || CHR(10) || CHR(10) || 'The Level 3 data were obtained from the Direction Interval Retrieval with Threshold Nudging (DIRTH) wind vector solutions contained in the QuikSCAT Level 2B data and are provided on an approximately 0.25 degrees x 0.25 degrees global grid. Separate maps are provided for both the ascending pass (6AM LST equator crossing) and descending pass (6PM LST equator crossing). By maintaining the data at nearly the original Level 2B sampling resolution and separating the ascending and descending passes, very little overlap occurs in one day. However, when overlap between subsequent swaths does occur, the values are over-written, not averaged. Therefore, a SeaWinds on QuikSCAT Level 3 file contains only the latest measurement for each day.' || CHR(10) || CHR(10) || 'This product is also referred to as JPL PO.DAAC product 109.');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'David' AND last_name = 'Moroni';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 109, 0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'QUIKSCAT';

COMMIT;
