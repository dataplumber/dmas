/*
** Copyright (c) 2008-2009, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: load_oco_policy.sql 4686 2010-04-02 00:02:50Z gangl $
*/

/*
** This script loads the OCO dataset policy for the Inventory schema.
*/

/*
** Collection
*/

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'OCO', 'Orbiting Carbon Observatory', 'PROJECT', 'This collection references all of the datasets associated with the OCO project.');

/*
** Project
*/

INSERT INTO project (project_id, short_name, long_name)
VALUES (project_id_seq.nextval, 'OCO', 'Orbiting Carbon Observatory');

/*
** Provider
*/

INSERT INTO provider (provider_id, short_name, long_name, type)
VALUES (provider_id_seq.nextval, 'OCO MOS', 'Orbiting Carbon Observatory (OCO) Mission Operations System (MOS)', 'DATA-PROVIDER');

INSERT INTO provider_resource (provider_id, path)
VALUES (provider_id_seq.currval, 'http://oco.jpl.nasa.gov/');

INSERT INTO contact (contact_id, role, first_name, middle_name, last_name, email, phone, fax, address, provider_id, notify_type)
VALUES (contact_id_seq.nextval, 'User Services Contact', 'OCO Data Center', NULL, 'User Services', 'help@oco-dc.jpl.nasa.gov', NULL, NULL, 'MS 300-320, 4800 Oak Grove Dr, Pasadena, CA  91109-8099', provider_id_seq.currval, NULL);

INSERT INTO contact (contact_id, role, first_name, middle_name, last_name, email, phone, fax, address, provider_id, notify_type)
VALUES (contact_id_seq.nextval, 'Investigator Contact', 'David', NULL, 'Crisp', 'David.Crisp@jpl.nasa.gov', '818-354-2224', '818-354-0966', '4800 Oak Grove Drive Pasadena, CA 91109-8099', provider_id_seq.currval, NULL);

INSERT INTO contact (contact_id, role, first_name, middle_name, last_name, email, phone, fax, address, provider_id, notify_type)
VALUES (contact_id_seq.nextval, 'Science Contact', 'Charles', 'E', 'Miller', 'Charles.E.Miller@jpl.nasa.gov', '818-393-6294', '818-354-5148', '4800 Oak Grove Drive Pasadena, CA 91109-8099', provider_id_seq.currval, NULL);

/*
** Source and Sensor
**
** Short and long names don't necessarily match GCMD valids.
*/

INSERT INTO source (source_id, short_name, long_name, type, orbit_period, incl_angle, description)
VALUES (source_id_seq.nextval, 'OCO', 'Orbiting Carbon Observatory', 'SPACECRAFT', 98.88, 98, 'The Orbiting Carbon Observatory (OCO) is a dedicated spacecraft that carries a single instrument comprised of three high-resolution grating spectrometers. The instrument, developed by Hamilton Sundstrand Sensor Systems, will acquire the most precise measurements of atmospheric CO2 ever made from space. The spacecraft, developed by Orbital Sciences Corporation, is based upon the LeoStar-2 architecture. The Observatory will be launched from the Vandenberg Air Force Base in California on a dedicated Taurus XL rocket in February 2009. The Observatory will fly in a near polar orbit that enables the instrument to observe most of the Earth''s surface at least once every sixteen days.');

INSERT INTO sensor (sensor_id, short_name, long_name, swath_width, description)
VALUES (sensor_id_seq.nextval, 'OCO', 'Orbiting Carbon Observatory', NULL, 'OCO uses three high-resolution grating spectrometers.  Each spectrometer measures light in one specific region of the spectrum. The spectrometers are designed to measure the absorption of reflected sunlight at near-infrared wavelengths by CO2 and molecular oxygen (O2) in the Earth''s atmosphere. The space-based instrument will acquire spatially and temporally coincident measurements of reflected sunlight in the CO2 bands centered at wavelengths near 1.61 mm and 2.06 mm and in the O2 A-band centered near 0.76 mm.');

/*
** Datasets
*/

/*
** OCO_ACS
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'OCO_ACS', 'OCO Attitude Control System (ACS)', 'Orbiting Carbon Observatory (OCO) Mission Operations System (MOS)', 'OCO_ACS', '0', 'Global', NULL, 'TBD', 'TBD', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong(SYSDATE), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong(SYSDATE), inventory.timestampToLong(SYSDATE), 'Initial entry into the PO.DAAC system.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-ONLY', NULL, NULL, NULL, 'PRIVATE', 'YEAR-DOY', 'HDF', 'NONE', 'MD5', 'NONE', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PRIVATE', 'file:///store/oco/private/acs/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Atmosphere', 'Atmospheric Chemistry', 'Carbon and Hydrocarbon Compounds', 'Carbon Dioxide');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'TBD', inventory.timestampToLong(SYSDATE), 'Initial version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'version';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'OCO';

/*
** OCO_Anc
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'OCO_Anc', 'OCO Ancillary', 'Orbiting Carbon Observatory (OCO) Mission Operations System (MOS)', 'OCO_Anc', '0', 'Global', NULL, 'TBD', 'TBD', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong(SYSDATE), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong(SYSDATE), inventory.timestampToLong(SYSDATE), 'Initial entry into the PO.DAAC system.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-ONLY', NULL, NULL, NULL, 'PRIVATE', 'YEAR-DOY', 'RAW', 'NONE', 'MD5', 'NONE', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PRIVATE', 'file:///store/oco/private/anc/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Atmosphere', 'Atmospheric Chemistry', 'Carbon and Hydrocarbon Compounds', 'Carbon Dioxide');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'TBD', inventory.timestampToLong(SYSDATE), 'Initial version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'version';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'OCO';

/*
** OCO_FTS_Anc
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'OCO_FTS_Anc', 'OCO Fourier Transform Spectrometer (FTS) Ancillary', 'Orbiting Carbon Observatory (OCO) Mission Operations System (MOS)', 'OCO_FTS_Anc', '0', 'Global', NULL, 'TBD', 'TBD', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong(SYSDATE), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong(SYSDATE), inventory.timestampToLong(SYSDATE), 'Initial entry into the PO.DAAC system.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-ONLY', NULL, NULL, NULL, 'PRIVATE', 'YEAR-DOY', 'RAW', 'NONE', 'MD5', 'NONE', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PRIVATE', 'file:///store/oco/private/fts_anc/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Atmosphere', 'Atmospheric Chemistry', 'Carbon and Hydrocarbon Compounds', 'Carbon Dioxide');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'TBD', inventory.timestampToLong(SYSDATE), 'Initial version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'version';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'OCO';

/*
** OCO_FTS_Igram
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'OCO_FTS_Igram', 'OCO Fourier Transform Spectrometer (FTS) Interferogram', 'Orbiting Carbon Observatory (OCO) Mission Operations System (MOS)', 'OCO_FTS_Igram', '1', 'Global', NULL, 'TBD', 'TBD', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong(SYSDATE), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong(SYSDATE), inventory.timestampToLong(SYSDATE), 'Initial entry into the PO.DAAC system.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-ONLY', NULL, NULL, NULL, 'PRIVATE', 'YEAR-DOY', 'RAW', 'NONE', 'MD5', 'NONE', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PRIVATE', 'file:///store/oco/private/fts_igram/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Atmosphere', 'Atmospheric Chemistry', 'Carbon and Hydrocarbon Compounds', 'Carbon Dioxide');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'TBD', inventory.timestampToLong(SYSDATE), 'Initial version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'version';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'OCO';

/*
** OCO_FTS_Spectra
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'OCO_FTS_Spectra', 'OCO Fourier Transform Spectrometer (FTS) Spectral', 'Orbiting Carbon Observatory (OCO) Mission Operations System (MOS)', 'OCO_FTS_Spectra', '2', 'Global', NULL, 'TBD', 'TBD', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong(SYSDATE), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong(SYSDATE), inventory.timestampToLong(SYSDATE), 'Initial entry into the PO.DAAC system.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'RESTRICTED', 'YEAR-DOY', 'RAW', 'NONE', 'MD5', 'NONE', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-RESTRICTED', 'file:///store/oco/restricted/fts_spectra/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Atmosphere', 'Atmospheric Chemistry', 'Carbon and Hydrocarbon Compounds', 'Carbon Dioxide');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'TBD', inventory.timestampToLong(SYSDATE), 'Initial version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'version';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'OCO';

/*
** OCO_L1A
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'OCO_L1A', 'OCO Level 1A', 'Orbiting Carbon Observatory (OCO) Mission Operations System (MOS)', 'OCO_L1A', '1A', 'Global', NULL, '2.25 km', '1.29 km', NULL, NULL, NULL, '0.333 seconds', NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong(SYSDATE), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong(SYSDATE), inventory.timestampToLong(SYSDATE), 'Initial entry into the PO.DAAC system.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'RESTRICTED', 'YEAR-DOY', 'HDF', 'NONE', 'MD5', 'BACKTRACK', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-RESTRICTED', 'file:///store/oco/restricted/L1A/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Atmosphere', 'Atmospheric Chemistry', 'Carbon and Hydrocarbon Compounds', 'Carbon Dioxide');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'TBD', inventory.timestampToLong(SYSDATE), 'Initial version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'version';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'OCO';

/*
** OCO_L1B
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'OCO_L1B', 'OCO Level 1B', 'Orbiting Carbon Observatory (OCO) Mission Operations System (MOS)', 'OCO_L1B', '1B', 'Global', NULL, '2.25 km', '1.29 km', NULL, NULL, NULL, '0.333 seconds', NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong(SYSDATE), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong(SYSDATE), inventory.timestampToLong(SYSDATE), 'Initial entry into the PO.DAAC system.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'RESTRICTED', 'YEAR-DOY', 'HDF', 'NONE', 'MD5', 'BACKTRACK', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-RESTRICTED', 'file:///store/oco/restricted/L1B/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PUBLIC', 'file:///store/oco/public/L1B/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/atmosphere/oco/L1B/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Atmosphere', 'Atmospheric Chemistry', 'Carbon and Hydrocarbon Compounds', 'Carbon Dioxide');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'TBD', inventory.timestampToLong(SYSDATE), 'Initial version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'version';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'OCO';

/*
** OCO_L2
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'OCO_L2', 'OCO Level 2 Apparent Optical Path Difference (AOPD)', 'Orbiting Carbon Observatory (OCO) Mission Operations System (MOS)', 'OCO_L2_AOPD', '2', 'Global', NULL, '2.25 km', '1.29 km', NULL, NULL, NULL, '0.333 seconds', NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong(SYSDATE), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong(SYSDATE), inventory.timestampToLong(SYSDATE), 'Initial entry into the PO.DAAC system.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'RESTRICTED', 'YEAR-DOY', 'HDF', 'NONE', 'MD5', 'BACKTRACK', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-RESTRICTED', 'file:///store/oco/restricted/L2_AOPD/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PUBLIC', 'file:///store/oco/public/L2_AOPD/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/atmosphere/oco/L2_AOPD/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Atmosphere', 'Atmospheric Chemistry', 'Carbon and Hydrocarbon Compounds', 'Carbon Dioxide');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'TBD', inventory.timestampToLong(SYSDATE), 'Initial version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'version';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'OCO';

/*
** OCO_L2_FullPhysics
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'OCO_L2_FullPhysics', 'OCO Level 2 Full Physics', 'Orbiting Carbon Observatory (OCO) Mission Operations System (MOS)', 'OCO_L2_FullPhysics', '2', 'Global', NULL, '2.25 km', '1.29 km', NULL, NULL, NULL, '0.333 seconds', NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong(SYSDATE), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong(SYSDATE), inventory.timestampToLong(SYSDATE), 'Initial entry into the PO.DAAC system.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ROLLING-STORE', NULL, NULL, 180, 'RESTRICTED', 'YEAR-DOY', 'HDF', 'NONE', 'MD5', 'BACKTRACK', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-RESTRICTED', 'file:///store/oco/restricted/L2_FULL/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Atmosphere', 'Atmospheric Chemistry', 'Carbon and Hydrocarbon Compounds', 'Carbon Dioxide');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'TBD', inventory.timestampToLong(SYSDATE), 'Initial version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'version';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'OCO';

/*
** OCO_MOC
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'OCO_MOC', 'OCO Meridional Overturning Circulation (MOC)', 'Orbiting Carbon Observatory (OCO) Mission Operations System (MOS)', 'OCO_MOC', NULL, 'Global', NULL, 'TBD', 'TBD', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong(SYSDATE), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong(SYSDATE), inventory.timestampToLong(SYSDATE), 'Initial entry into the PO.DAAC system.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-ONLY', NULL, NULL, NULL, 'PRIVATE', 'YEAR-DOY', 'HDF', 'NONE', 'MD5', 'NONE', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-RESTRICTED', 'file:///store/oco/restricted/moc/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Atmosphere', 'Atmospheric Chemistry', 'Carbon and Hydrocarbon Compounds', 'Carbon Dioxide');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'TBD', inventory.timestampToLong(SYSDATE), 'Initial version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'version';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'OCO';

/*
** OCO_RICA
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'OCO_RICA', 'OCO Level 1A Residual Image Correction Algorithm (RICA)', 'Orbiting Carbon Observatory (OCO) Mission Operations System (MOS)', 'OCO_RICA', '1A', 'Global', NULL, '2.25 km', '1.29 km', NULL, NULL, NULL, '0.333 seconds', NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong(SYSDATE), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong(SYSDATE), inventory.timestampToLong(SYSDATE), 'Initial entry into the PO.DAAC system.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'RESTRICTED', 'YEAR-DOY', 'HDF', 'NONE', 'MD5', 'BACKTRACK', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-RESTRICTED', 'file:///store/oco/restricted/RICA/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Atmosphere', 'Atmospheric Chemistry', 'Carbon and Hydrocarbon Compounds', 'Carbon Dioxide');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'TBD', inventory.timestampToLong(SYSDATE), 'Initial version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'version';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'OCO';

/*
** OCO_Telem
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'OCO_Telem', 'OCO Science and Housekeeping Telemetry', 'Orbiting Carbon Observatory (OCO) Mission Operations System (MOS)', 'OCO_Telem', '0', 'Global', NULL, '2.25 km', '1.29 km', NULL, NULL, NULL, '0.333 seconds', NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong(SYSDATE), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong(SYSDATE), inventory.timestampToLong(SYSDATE), 'Initial entry into the PO.DAAC system.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-ONLY', NULL, NULL, NULL, 'PRIVATE', 'YEAR-DOY', 'RAW', 'NONE', 'MD5', 'NONE', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PRIVATE', 'file:///store/oco/private/telem/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Atmosphere', 'Atmospheric Chemistry', 'Carbon and Hydrocarbon Compounds', 'Carbon Dioxide');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'TBD', inventory.timestampToLong(SYSDATE), 'Initial version.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'batch' OR short_name = 'version';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'OCO';

COMMIT;

