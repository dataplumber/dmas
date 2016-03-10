/*
** Copyright (c) 2008, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: load_jason_policy.sql 4686 2010-04-02 00:02:50Z gangl $
*/

/*
** This script loads the Jason-1 dataset policy for the Inventory schema.
*/

/*
** Collection
*/

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'JASON-1', 'Jason-1', 'PROJECT', 'This collection references all of the datasets associated with the Jason-1 project.');

/*
** Project
*/

INSERT INTO project (project_id, short_name, long_name)
VALUES (project_id_seq.nextval, 'JASON-1', 'Jason-1');

/*
** Provider
*/

INSERT INTO provider (provider_id, short_name, long_name, type)
VALUES (provider_id_seq.nextval, 'JASON-1', 'Jason-1', 'DATA-PROVIDER');

INSERT INTO provider_resource (provider_id, path)
VALUES (provider_id_seq.currval, 'http://topex-www.jpl.nasa.gov/mission/jason-1.html');

INSERT INTO provider_resource (provider_id, path)
VALUES (provider_id_seq.currval, 'http://www.cnes.fr/web/CNES-en/1441-jason.php');

/*
** Source and Sensor
*/

INSERT INTO source (source_id, short_name, long_name, type, orbit_period, incl_angle, description)
VALUES (source_id_seq.nextval, 'JASON-1', 'JASON-1', 'SPACECRAFT', NULL, NULL, 'Jason-1 continues the task of providing the important oceanographic data time-series originated by TOPEX/Poseidon, carrying updated versions of the same instruments.');

INSERT INTO sensor (sensor_id,  short_name, long_name, swath_width, description)
VALUES (sensor_id_seq.nextval, 'POSEIDON-2', 'JASON-1 RADAR ALTIMETER', NULL, 'TBD');

/*
** Datasets
*/

/*
** JASON-1_ATG_SSHA
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'JASON-1_ATG_SSHA', 'Jason-1 Along Track Gridded (ATG) Sea Surface Height Anomaly (SSHA)', 'NASA/JPL/PODAAC', NULL, '2', 'Global Ocean', NULL, '0.0001 decimal degrees', '0.05 decimal degrees', NULL, NULL, NULL, '10 days', NULL, NULL, NULL, NULL, NULL, 'The Jason-1 Along Track Gridded Sea Surface Height Anomaly (J1ATG_SSHA_V2) product is generated from the Jason-1 Sea Surface Height Anomaly (J1SSHA) product. There are 127 orbits per cycle and each orbit has a period of 112 minutes. The ground track of the orbits are about 315 km apart at the equator. Along track measurements are approximately 1 sec and 6 km apart.  Each orbit is composed of 2 passes, one ascending from -66 deg to +66 deg latitude and the other descending from +66 deg to - 66 deg. The passes are organized this way to avoid data boundaries in the middle of the oceans. All odd-numbered passes are ascending and all even-numbered passes are descending. There are a maximum of 254 passes per cycle.  It is organized as 1 file per cycle and contains a header record followed by 254 records of SSHA parameters. Each file record represents a pass file of the cycle and the SSHA parameters correspond to measurement values taken from the Jason-1 Sea Surface Height Anomaly (J1SSHA) product. The times and SSHA values from the J1SSHA products are gridded along the satellite ground track. Each SSHA value in the ATG was derived using a set of cubic spline coefficients with reference latitudes and longitudes as the independent variable. Thus, these interpreted SSHA values are tied to a fixed set of reference latitudes and longitudes. Also, before interpretation, the ionospheric correction used in the SSHA was replaced by a smoothed value by averaging 8 iono values before and after the given iono. The 8 iono values correspond to approximately 50 km along track on either side of the given location of the iono data point.  The Sea Surface Height Anomaly, the only parameter in this product, represents the difference between the best estimate of the sea surface height and a mean sea surface. The sea surface height used was corrected for atmospheric effects (ionosphere, wet and dry troposphere), effects due to surface conditions (electromagnetic bias), and other contributions (ocean tides, pole tide, and inverse barometer).');

UPDATE dataset SET provider_id = 
  (SELECT provider_id FROM provider WHERE short_name = 'NASA/JPL/PODAAC')
WHERE short_name = 'JASON-1_ATG_SSHA';

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('14-JAN-2002 10:07:06 PM'), NULL, 66.2, -66.2, 180, -180, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('14-JAN-2002'), inventory.timestampToLong(SYSDATE), 'Dataset policy entry migrated from the PO.DAAC Ingres database.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'PUBLIC', 'CYCLE', 'NETCDF', 'NONE', 'MD5', 'BACKTRACK', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PUBLIC', 'file:///store/jason1/public/j1_atg/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/sea_surface_height/jason/j1_atg/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Eddies', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Gyres', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Ocean Currents', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Sea Surface Topography', 'Sea Surface Height', NULL);

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_resource (dataset_id, name, type, path, description)
VALUES (dataset_id_seq.currval, 'Jason-1 Family Page', 'Project Web Site', 'http://podaac.jpl.nasa.gov/DATA_CATALOG/jason1info.html', NULL);

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'A', inventory.timestampToLong('14-JAN-2002'), 'Initial processor version.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 2, 'B', NULL, 'Version B.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 3, 'C', inventory.timestampToLong('27-AUG-2008'), 'Version C.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'cycle' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'version';

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'JASON-1_ALONG_TRACK_GRIDDED_SEA_SURFACE_HEIGHT_ANOMALY', 'Jason-1 Sea Surface Height Anomaly (SSHA)', 'PRODUCT', 'The Jason-1 Along Track Gridded Sea Surface Height Anomaly (J1ATG_SSHA_V2) product is generated from the Jason-1 Sea Surface Height Anomaly (J1SSHA) product. There are 127 orbits per cycle and each orbit has a period of 112 minutes. The ground track of the orbits are about 315 km apart at the equator. Along track measurements are approximately 1 sec and 6 km apart.  Each orbit is composed of 2 passes, one ascending from -66 deg to +66 deg latitude and the other descending from +66 deg to - 66 deg. The passes are organized this way to avoid data boundaries in the middle of the oceans. All odd-numbered passes are ascending and all even-numbered passes are descending. There are a maximum of 254 passes per cycle.  It is organized as 1 file per cycle and contains a header record followed by 254 records of SSHA parameters. Each file record represents a pass file of the cycle and the SSHA parameters correspond to measurement values taken from the Jason-1 Sea Surface Height Anomaly (J1SSHA) product. The times and SSHA values from the J1SSHA products are gridded along the satellite ground track. Each SSHA value in the ATG was derived using a set of cubic spline coefficients with reference latitudes and longitudes as the independent variable. Thus, these interpreted SSHA values are tied to a fixed set of reference latitudes and longitudes. Also, before interpretation, the ionospheric correction used in the SSHA was replaced by a smoothed value by averaging 8 iono values before and after the given iono. The 8 iono values correspond to approximately 50 km along track on either side of the given location of the iono data point.  The Sea Surface Height Anomaly, the only parameter in this product, represents the difference between the best estimate of the sea surface height and a mean sea surface. The sea surface height used was corrected for atmospheric effects (ionosphere, wet and dry troposphere), effects due to surface conditions (electromagnetic bias), and other contributions (ocean tides, pole tide, and inverse barometer).');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Jessica' AND last_name = 'Hausman';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 133, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'JASON-1';

/*
** JASON-1_AUX
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'JASON-1_AUX', 'Jason-1 Auxiliary Data', 'Jason-1 NASA', 'JASON_1_AUX', NULL, 'N/A', NULL, 'N/A', 'N/A', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('14-JAN-2002 10:07:06 PM'), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('14-JAN-2002'), inventory.timestampToLong(SYSDATE), 'Dataset policy entry migrated from the PO.DAAC Ingres database.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-ONLY', NULL, NULL, NULL, 'PRIVATE', 'CYCLE', 'RAW', 'NONE', 'MD5', 'NONE', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PRIVATE', 'file:///store/jason1/private/aux/data');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'A', inventory.timestampToLong('14-JAN-2002'), 'Initial processor version.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 2, 'B', NULL, 'Version B.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 3, 'C', inventory.timestampToLong('27-AUG-2008'), 'Version C.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'cycle' OR short_name = 'version';

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'JASON-1_AUXILIARY_DATA', 'Jason-1 Auxiliary Data', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Jessica' AND last_name = 'Hausman';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 128, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'JASON-1';

/*
** JASON-1_GDR_CNES
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'JASON-1_GDR_CNES', 'Jason-1 Geophysical Data Record (GDR) CNES', 'Jason-1 CNES', NULL, '2', 'Global Ocean', NULL, '0.0001 decimal degrees', '0.05 decimal degrees', NULL, NULL, NULL, '10 days', NULL, NULL, NULL, NULL, NULL, 'GDR files contain full accuracy altimeter data, with a high precision orbit (accuracy ~2.5 cm), provided approximately 35 days after data collection. The instruments on Jason-1 make direct observations of the following quantities: altimeter range, significant wave height, ocean radar backscatter cross-section (a measure of wind speed), ionospheric electron content (derived by a simple formula), tropospheric water content, mean sea surface, and position relative to the GPS satellite constellation. The GDR contain all relevant corrections needed to calculate the sea surface height. Sea surface height anomalies are derived from the 1/second GDRs and are based on the recommended data edit criteria specified in the Jason-1 I/GDR User Handbook located at ftp://podaac.jpl.nasa.gov/pub/sea_surface_height/jason/gdr/doc.');

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('14-JAN-2002 10:07:06 PM'), NULL, 66.2, -66.2, 180, -180, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('14-JAN-2002'), inventory.timestampToLong(SYSDATE), 'Initial entry into the PO.DAAC system.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'RESTRICTED', 'CYCLE', 'RAW', 'NONE', 'MD5', 'BACKTRACK', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-RESTRICTED', 'file:///store/jason1/restricted/gdr_cnes_c/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PUBLIC', 'file:///store/jason1/public/gdr_c/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/sea_surface_height/jason/gdr/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Bathymetry/Seafloor Topography', 'Bathymetry', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Eddies', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Gyres', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Ocean Currents', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Waves', 'Significant Wave Height', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Sea Surface Topography', 'Sea Surface Height', NULL);

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_resource (dataset_id, name, type, path, description)
VALUES (dataset_id_seq.currval, 'Jason-1 Family Page', 'Project Web Site', 'http://podaac.jpl.nasa.gov/DATA_CATALOG/jason1info.html', NULL);

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'A', inventory.timestampToLong('14-JAN-2002'), 'Initial processor version.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 2, 'B', NULL, 'Version B.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 3, 'C', inventory.timestampToLong('27-AUG-2008'), 'Version C.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'cycle' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'version';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'JASON-1';

/*
** JASON-1_GDR_NASA
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'JASON-1_GDR_NASA', 'Jason-1 Geophysical Data Record (GDR) NASA', 'Jason-1 NASA', 'JASON_1_GDR', '2', 'Global Ocean', NULL, '0.0001 decimal degrees', '0.05 decimal degrees', NULL, NULL, NULL, '10 days', NULL, NULL, NULL, NULL, NULL, 'GDR files contain full accuracy altimeter data, with a high precision orbit (accuracy ~2.5 cm), provided approximately 35 days after data collection. The instruments on Jason-1 make direct observations of the following quantities: altimeter range, significant wave height, ocean radar backscatter cross-section (a measure of wind speed), ionospheric electron content (derived by a simple formula), tropospheric water content, mean sea surface, and position relative to the GPS satellite constellation. The GDR contain all relevant corrections needed to calculate the sea surface height. Sea surface height anomalies are derived from the 1/second GDRs and are based on the recommended data edit criteria specified in the Jason-1 I/GDR User Handbook located at ftp://podaac.jpl.nasa.gov/pub/sea_surface_height/jason/gdr/doc.');

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('14-JAN-2002 10:07:06 PM'), NULL, 66.2, -66.2, 180, -180, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('14-JAN-2002'), inventory.timestampToLong(SYSDATE), 'Dataset policy entry migrated from the PO.DAAC Ingres database.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'RESTRICTED', 'CYCLE', 'RAW', 'NONE', 'MD5', 'BACKTRACK', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-RESTRICTED', 'file:///store/jason1/restricted/gdr_nasa_c/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PUBLIC', 'file:///store/jason1/public/gdr_c/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/sea_surface_height/jason/gdr/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Bathymetry/Seafloor Topography', 'Bathymetry', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Eddies', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Gyres', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Ocean Currents', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Waves', 'Significant Wave Height', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Sea Surface Topography', 'Sea Surface Height', NULL);

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_resource (dataset_id, name, type, path, description)
VALUES (dataset_id_seq.currval, 'Jason-1 Family Page', 'Project Web Site', 'http://podaac.jpl.nasa.gov/DATA_CATALOG/jason1info.html', NULL);

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'A', inventory.timestampToLong('14-JAN-2002'), 'Initial processor version.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 2, 'B', NULL, 'Version B.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 3, 'C', inventory.timestampToLong('27-AUG-2008'), 'Version C.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'cycle' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'version';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'JASON-1';

/*
** JASON-1_GDR Collection
**
** Combination of JASON-1_GDR_CNES and JASON-1_GDR_NASA datasets.
*/

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'JASON-1_GEOPHYSICAL_DATA_RECORD', 'Jason-1 Geophysical Data Record (GDR)', 'PRODUCT', 'GDR files contain full accuracy altimeter data, with a high precision orbit (accuracy ~2.5 cm), provided approximately 35 days after data collection. The instruments on Jason-1 make direct observations of the following quantities: altimeter range, significant wave height, ocean radar backscatter cross-section (a measure of wind speed), ionospheric electron content (derived by a simple formula), tropospheric water content, mean sea surface, and position relative to the GPS satellite constellation. The GDR contain all relevant corrections needed to calculate the sea surface height. Sea surface height anomalies are derived from the 1/second GDRs and are based on the recommended data edit criteria specified in the Jason-1 I/GDR User Handbook located at ftp://podaac.jpl.nasa.gov/pub/sea_surface_height/jason/gdr/doc.');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Jessica' AND last_name = 'Hausman';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'JASON-1_GDR_CNES' OR short_name = 'JASON-1_GDR_NASA';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 131, 0, 0);

/*
** JASON-1_GDR_NETCDF
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'JASON-1_GDR_NETCDF', 'Jason-1 Geophysical Data Record (GDR) NetCDF', 'Jason-1 CNES', NULL, '2', 'Global Ocean', NULL, '0.0001 decimal degrees', '0.05 decimal degrees', NULL, NULL, NULL, '10 days', NULL, NULL, NULL, NULL, NULL, 'GDR files contain full accuracy altimeter data, with a high precision orbit (accuracy ~2.5 cm), provided approximately 35 days after data collection. The instruments on Jason-1 make direct observations of the following quantities: altimeter range, significant wave height, ocean radar backscatter cross-section (a measure of wind speed), ionospheric electron content (derived by a simple formula), tropospheric water content, mean sea surface, and position relative to the GPS satellite constellation. The GDR contain all relevant corrections needed to calculate the sea surface height. Sea surface height anomalies are derived from the 1/second GDRs and are based on the recommended data edit criteria specified in the Jason-1 I/GDR User Handbook located at ftp://podaac.jpl.nasa.gov/pub/sea_surface_height/jason/gdr/doc.');

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('27-MAY-2008 12:00:00'), NULL, 66.2, -66.2, 180, -180, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('27-MAY-2008'), inventory.timestampToLong(SYSDATE), 'Initial entry into the PO.DAAC system.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'DIST-ONLY', NULL, NULL, NULL, 'RESTRICTED', 'CYCLE', 'NETCDF', 'NONE', 'MD5', 'BACKTRACK', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-RESTRICTED', 'file:///store/jason1/restricted/gdr_netcdf_c/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PUBLIC', 'file:///store/jason1/public/gdr_netcdf_c/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/sea_surface_height/jason/gdr_netcdf_c/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Bathymetry/Seafloor Topography', 'Bathymetry', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Eddies', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Gyres', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Ocean Currents', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Waves', 'Significant Wave Height', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Sea Surface Topography', 'Sea Surface Height', NULL);

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'A', inventory.timestampToLong('14-JAN-2002'), 'Initial processor version.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 2, 'B', NULL, 'Version B.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 3, 'C', inventory.timestampToLong('27-AUG-2008'), 'Version C.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'cycle' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'version';

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'JASON-1_GEOPHYSICAL_DATA_RECORD_NETCDF', 'Jason-1 Geophysical Data Record (GDR) NetCDF', 'PRODUCT', 'GDR files contain full accuracy altimeter data, with a high precision orbit (accuracy ~2.5 cm), provided approximately 35 days after data collection. The instruments on Jason-1 make direct observations of the following quantities: altimeter range, significant wave height, ocean radar backscatter cross-section (a measure of wind speed), ionospheric electron content (derived by a simple formula), tropospheric water content, mean sea surface, and position relative to the GPS satellite constellation. The GDR contain all relevant corrections needed to calculate the sea surface height. Sea surface height anomalies are derived from the 1/second GDRs and are based on the recommended data edit criteria specified in the Jason-1 I/GDR User Handbook located at ftp://podaac.jpl.nasa.gov/pub/sea_surface_height/jason/gdr/doc.');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Jessica' AND last_name = 'Hausman';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'JASON-1_GDR_NETCDF';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', NULL, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'JASON-1';

/*
** JASON-1_GDR_SSHA_NETCDF
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'JASON-1_GDR_SSHA_NETCDF', 'Jason-1 Geophysical Data Record (GDR) Sea Surface Height Anomaly (SSHA) NetCDF', 'Jason-1 CNES', NULL, '2', 'Global Ocean', NULL, '0.0001 decimal degrees', '0.05 decimal degrees', NULL, NULL, NULL, '10 days', NULL, NULL, NULL, NULL, NULL, 'The SSHA are derived from the Jason-1 GDR. The instruments on Jason-1 make direct observations of the following quantities: altimeter range, significant wave height, ocean radar backscatter cross-section (a measure of wind speed), ionospheric electron content (derived by a simple formula), tropospheric water content, and position relative to the GPS satellite constellation.');

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('27-MAY-2008 12:00:00'), NULL, 66.2, -66.2, 180, -180, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('27-MAY-2008'), inventory.timestampToLong(SYSDATE), 'Initial entry into the PO.DAAC system.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'DIST-ONLY', NULL, NULL, NULL, 'RESTRICTED', 'CYCLE', 'NETCDF', 'NONE', 'MD5', 'BACKTRACK', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-RESTRICTED', 'file:///store/jason1/restricted/gdr_ssha_netcdf_c/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PUBLIC', 'file:///store/jason1/public/gdr_ssha_netcdf_c/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/sea_surface_height/jason/gdr_ssha_netcdf_c/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Bathymetry/Seafloor Topography', 'Bathymetry', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Eddies', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Gyres', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Ocean Currents', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Waves', 'Significant Wave Height', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Sea Surface Topography', 'Sea Surface Height', NULL);

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'A', inventory.timestampToLong('14-JAN-2002'), 'Initial processor version.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 2, 'B', NULL, 'Version B.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 3, 'C', inventory.timestampToLong('27-AUG-2008'), 'Version C.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'cycle' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'version';

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'JASON-1_GEOPHYSICAL_DATA_RECORD_SEA_SURFACE_HEIGHT_ANOMALY_NETCDF', 'Jason-1 Geophysical Data Record (GDR) Sea Surface Height Anomaly (SSHA) NetCDF', 'PRODUCT', 'The SSHA are derived from the Jason-1 GDR. The instruments on Jason-1 make direct observations of the following quantities: altimeter range, significant wave height, ocean radar backscatter cross-section (a measure of wind speed), ionospheric electron content (derived by a simple formula), tropospheric water content, and position relative to the GPS satellite constellation.');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Jessica' AND last_name = 'Hausman';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'JASON-1_GDR_SSHA_NETCDF';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', NULL, 0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'JASON-1';

/*
** JASON-1_IGDR
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'JASON-1_IGDR', 'Jason-1 Interim Geophysical Data Record (IGDR)', 'Jason-1 CNES', NULL, '2', 'Global Ocean', NULL, '0.0001 decimal degrees', '0.05 decimal degrees', NULL, NULL, NULL, '3 days', NULL, NULL, NULL, NULL, NULL, 'IGDR files contain full accuracy altimeter data, with the exception of an interim orbit (accuracy < 4 cm), provided within 3 days of data collection. The instruments on Jason-1 make direct observations of the following quantities: altimeter range, significant wave height, ocean radar backscatter cross-section (a measure of wind speed), ionospheric electron content (derived by a simple formula), tropospheric water content, and position relative to the GPS satellite constellation. The IGDR contain all relevant corrections needed to calculate the sea surface height. Sea surface height anomalies are derived from the 1/second IGDRs and are based on the recommended data edit criteria specified in the Jason-1 I/GDR User Handbook located at ftp://podaac.jpl.nasa.gov/pub/sea_surface_height/jason/gdr/doc.');

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('14-JAN-2002 10:07:06 PM'), NULL, 66.2, -66.2, 180, -180, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('14-JAN-2002'), inventory.timestampToLong(SYSDATE), 'Initial entry into the PO.DAAC system.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'PUBLIC', 'CYCLE', 'RAW', 'NONE', 'MD5', 'BACKTRACK', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PUBLIC', 'file:///store/jason1/public/igdr/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/sea_surface_height/jason/igdr/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Bathymetry/Seafloor Topography', 'Bathymetry', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Eddies', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Gyres', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Ocean Currents', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Waves', 'Significant Wave Height', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Sea Surface Topography', 'Sea Surface Height', NULL);

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_resource (dataset_id, name, type, path, description)
VALUES (dataset_id_seq.currval, 'Jason-1 Family Page', 'Project Web Site', 'http://podaac.jpl.nasa.gov/DATA_CATALOG/jason1info.html', NULL);

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'A', inventory.timestampToLong('14-JAN-2002'), 'Initial processor version.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 2, 'B', NULL, 'Version B.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 3, 'C', inventory.timestampToLong('27-AUG-2008'), 'Version C.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'cycle' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'version';

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'JASON-1_INTERIM_GEOPHYSICAL_DATA_RECORD', 'Jason-1 Interim Geophysical Data Record (IGDR)', 'PRODUCT', 'IGDR files contain full accuracy altimeter data, with the exception of an interim orbit (accuracy < 4 cm), provided within 3 days of data collection. The instruments on Jason-1 make direct observations of the following quantities: altimeter range, significant wave height, ocean radar backscatter cross-section (a measure of wind speed), ionospheric electron content (derived by a simple formula), tropospheric water content, and position relative to the GPS satellite constellation. The IGDR contain all relevant corrections needed to calculate the sea surface height. Sea surface height anomalies are derived from the 1/second IGDRs and are based on the recommended data edit criteria specified in the Jason-1 I/GDR User Handbook located at ftp://podaac.jpl.nasa.gov/pub/sea_surface_height/jason/gdr/doc.');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Jessica' AND last_name = 'Hausman';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 168, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'JASON-1';

/*
** JASON-1_IGDR_NETCDF
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'JASON-1_IGDR_NETCDF', 'Jason-1 Interim Geophysical Data Record (IGDR) NetCDF', 'Jason-1 CNES', NULL, '2', 'Global Ocean', NULL, '0.0001 decimal degrees', '0.05 decimal degrees', NULL, NULL, NULL, '10 days', NULL, NULL, NULL, NULL, NULL, 'IGDR files contain full accuracy altimeter data, with the exception of an interim orbit (accuracy < 4 cm), provided within 3 days of data collection. The instruments on Jason-1 make direct observations of the following quantities: altimeter range, significant wave height, ocean radar backscatter cross-section (a measure of wind speed), ionospheric electron content (derived by a simple formula), tropospheric water content, and position relative to the GPS satellite constellation. The IGDR contain all relevant corrections needed to calculate the sea surface height. Sea surface height anomalies are derived from the 1/second IGDRs and are based on the recommended data edit criteria specified in the Jason-1 I/GDR User Handbook located at ftp://podaac.jpl.nasa.gov/pub/sea_surface_height/jason/gdr/doc.');

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('27-MAY-2008 12:00:00'), NULL, 66.2, -66.2, 180, -180, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('27-MAY-2008'), inventory.timestampToLong(SYSDATE), 'Initial entry into the PO.DAAC system.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'DIST-ONLY', NULL, NULL, NULL, 'PUBLIC', 'CYCLE', 'NETCDF', 'NONE', 'MD5', 'BACKTRACK', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PUBLIC', 'file:///store/jason1/public/igdr_netcdf/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/sea_surface_height/jason/igdr_netcdf/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Bathymetry/Seafloor Topography', 'Bathymetry', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Eddies', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Gyres', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Ocean Currents', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Waves', 'Significant Wave Height', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Sea Surface Topography', 'Sea Surface Height', NULL);

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'A', inventory.timestampToLong('14-JAN-2002'), 'Initial processor version.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 2, 'B', NULL, 'Version B.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 3, 'C', inventory.timestampToLong('27-AUG-2008'), 'Version C.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'cycle' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'version';

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'JASON-1_INTERIM_GEOPHYSICAL_DATA_RECORD_NETCDF', 'Jason-1 Interim Geophysical Data Record (IGDR) NetCDF', 'PRODUCT', 'IGDR files contain full accuracy altimeter data, with the exception of an interim orbit (accuracy < 4 cm), provided within 3 days of data collection. The instruments on Jason-1 make direct observations of the following quantities: altimeter range, significant wave height, ocean radar backscatter cross-section (a measure of wind speed), ionospheric electron content (derived by a simple formula), tropospheric water content, and position relative to the GPS satellite constellation. The IGDR contain all relevant corrections needed to calculate the sea surface height. Sea surface height anomalies are derived from the 1/second IGDRs and are based on the recommended data edit criteria specified in the Jason-1 I/GDR User Handbook located at ftp://podaac.jpl.nasa.gov/pub/sea_surface_height/jason/gdr/doc.');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Jessica' AND last_name = 'Hausman';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', NULL, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'JASON-1';

/*
** JASON-1_IGDR_SSHA_NETCDF
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'JASON-1_IGDR_SSHA_NETCDF', 'Jason-1 Interim Geophysical Data Record (IGDR) Sea Surface Height Anomaly (SSHA) NetCDF', 'Jason-1 CNES', NULL, '2', 'Global Ocean', NULL, '0.0001 decimal degrees', '0.05 decimal degrees', NULL, NULL, NULL, '10 days', NULL, NULL, NULL, NULL, NULL, 'The SSHA are derived from the Jason-1 IGDR. The instruments on Jason-1 make direct observations of the following quantities: altimeter range, significant wave height, ocean radar backscatter cross-section (a measure of wind speed), ionospheric electron content (derived by a simple formula), tropospheric water content, and position relative to the GPS satellite constellation.');

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('27-MAY-2008 12:00:00'), NULL, 66.2, -66.2, 180, -180, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('27-MAY-2008'), inventory.timestampToLong(SYSDATE), 'Initial entry into the PO.DAAC system.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'DIST-ONLY', NULL, NULL, NULL, 'PUBLIC', 'CYCLE', 'NETCDF', 'NONE', 'MD5', 'BACKTRACK', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PUBLIC', 'file:///store/jason1/public/igdr_ssha_netcdf/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/sea_surface_height/jason/igdr_ssha_netcdf/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Bathymetry/Seafloor Topography', 'Bathymetry', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Eddies', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Gyres', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Ocean Currents', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Waves', 'Significant Wave Height', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Sea Surface Topography', 'Sea Surface Height', NULL);

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'A', inventory.timestampToLong('14-JAN-2002'), 'Initial processor version.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 2, 'B', NULL, 'Version B.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 3, 'C', inventory.timestampToLong('27-AUG-2008'), 'Version C.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'cycle' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'version';

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'JASON-1_INTERIM_GEOPHYSICAL_DATA_RECORD_SEA_SURFACE_HEIGHT_ANOMALY_NETCDF', 'Jason-1 Interim Geophysical Data Record (IGDR) Sea Surface Height Anomaly (SSHA) NetCDF', 'PRODUCT', 'The SSHA are derived from the Jason-1 IGDR. The instruments on Jason-1 make direct observations of the following quantities: altimeter range, significant wave height, ocean radar backscatter cross-section (a measure of wind speed), ionospheric electron content (derived by a simple formula), tropospheric water content, and position relative to the GPS satellite constellation.');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Jessica' AND last_name = 'Hausman';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', NULL, 0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'JASON-1';

/*
** JASON-1_JMR
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'JASON-1_JMR', 'Jason-1 Microwave Radiometer (JMR)', 'Jason-1 NASA', 'JASON_1_JMRL1', '1', 'N/A', NULL, 'N/A', 'N/A', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('14-JAN-2002 10:07:06 PM'), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('14-JAN-2002'), inventory.timestampToLong(SYSDATE), 'Dataset policy entry migrated from the PO.DAAC Ingres database.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-ONLY', NULL, NULL, NULL, 'RESTRICTED', 'CYCLE', 'RAW', 'NONE', 'MD5', 'NONE', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-RESTRICTED', 'file:///store/jason1/restricted/jmr_a_b/data');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'A', inventory.timestampToLong('14-JAN-2002'), 'Initial processor version.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 2, 'B', NULL, 'Version B.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 3, 'C', inventory.timestampToLong('27-AUG-2008'), 'Version C.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'cycle' OR short_name = 'version';

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'JASON-1_MICROWAVE_RADIOMETER', 'Jason-1 Microwave Radiometer (JMR)', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Jessica' AND last_name = 'Hausman';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 214, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'JASON-1';

/*
** JASON-1_OSDR
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'JASON-1_OSDR', 'Jason-1 Operational Sensor Data Record (OSDR)', 'Jason-1 NASA', NULL, '2', 'Global Ocean', NULL, '0.0001 decimal degrees', '0.05 decimal degrees', NULL, NULL, NULL, '3 hours', NULL, NULL, NULL, NULL, NULL, 'OSDR files are provided every 2 hours and within 3 hours of data collection. They contain wave height, wind speed and water vapor measurements computed using real-time on-board DORIS/DIODE orbits (better than accuracy 20 cm) and other rapidly available corrections.');

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('14-JAN-2002 10:07:06 PM'), NULL, 66.2, -66.2, 180, -180, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('14-JAN-2002'), inventory.timestampToLong(SYSDATE), 'Initial entry into the PO.DAAC system.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'PUBLIC', 'CYCLE', 'RAW', 'NONE', 'MD5', 'BACKTRACK', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PUBLIC', 'file:///store/jason1/public/osdr/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/sea_surface_height/jason/osdr/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Bathymetry/Seafloor Topography', 'Bathymetry', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Eddies', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Gyres', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Ocean Currents', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Waves', 'Significant Wave Height', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Sea Surface Topography', 'Sea Surface Height', NULL);

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_resource (dataset_id, name, type, path, description)
VALUES (dataset_id_seq.currval, 'Jason-1 Family Page', 'Project Web Site', 'http://podaac.jpl.nasa.gov/DATA_CATALOG/jason1info.html', NULL);

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'A', inventory.timestampToLong('14-JAN-2002'), 'Initial processor version.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 2, 'B', NULL, 'Version B.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 3, 'C', inventory.timestampToLong('27-AUG-2008'), 'Version C.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'cycle' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'version';

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'JASON-1_OPERATIONAL_SENSOR_DATA_RECORD', 'Jason-1 Operational Sensor Data Record (OSDR)', 'PRODUCT', 'OSDR files are provided every 2 hours and within 3 hours of data collection. They contain wave height, wind speed and water vapor measurements computed using real-time on-board DORIS/DIODE orbits (better than accuracy 20 cm) and other rapidly available corrections.');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Jessica' AND last_name = 'Hausman';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 167, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'JASON-1';

/*
** JASON-1_PLTM
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'JASON-1_PLTM', 'Jason-1 Payload Telemetry', 'Jason-1 NASA', 'JASON_1_PLTM', NULL, 'N/A', NULL, 'N/A', 'N/A', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('14-JAN-2002 10:07:06 PM'), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('14-JAN-2002'), inventory.timestampToLong(SYSDATE), 'Dataset policy entry migrated from the PO.DAAC Ingres database.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-ONLY', NULL, NULL, NULL, 'PRIVATE', 'CYCLE', 'RAW', 'NONE', 'MD5', 'NONE', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PRIVATE', 'file:///store/jason1/private/pltm/data');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'A', inventory.timestampToLong('14-JAN-2002'), 'Initial processor version.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 2, 'B', NULL, 'Version B.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 3, 'C', inventory.timestampToLong('27-AUG-2008'), 'Version C.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'cycle' OR short_name = 'version';

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'JASON-1_PAYLOAD_TELEMETRY', 'Jason-1 Payload Telemetry', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Jessica' AND last_name = 'Hausman';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 129, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'JASON-1';

/*
** JASON-1_SGDR
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'JASON-1_SGDR', 'Jason-1 Sensor Geophysical Data Record (SGDR)', 'Jason-1 NASA', 'JASON_1_SDR', '2', 'Global Ocean', NULL, '0.0001 decimal degrees', '0.05 decimal degrees', NULL, NULL, NULL, '10 days', NULL, NULL, NULL, NULL, NULL, 'SGDR files contain full accuracy altimeter data, with a high precision orbit (accuracy ~2.5 cm), provided approximately 35 days after data collection. The instruments on Jason-1 make direct observations of the following quantities: altimeter range, significant wave height, ocean radar backscatter cross-section (a measure of wind speed), ionospheric electron content (derived by a simple formula), tropospheric water content, mean sea surface, and position relative to the GPS satellite constellation. The SGDR contain all relevant corrections needed to calculate the sea surface height. It also contains the waveforms that are required for retracking.');

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('14-JAN-2002 10:07:06 PM'), NULL, 66.2, -66.2, 180, -180, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('14-JAN-2002'), inventory.timestampToLong(SYSDATE), 'Dataset policy entry migrated from the PO.DAAC Ingres database.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'PRIVATE', 'CYCLE', 'RAW', 'NONE', 'MD5', 'BACKTRACK', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PRIVATE', 'file:///store/jason1/private/sgdr_c/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-RESTRICTED', 'file:///store/jason1/restricted/sgdr_c/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Bathymetry/Seafloor Topography', 'Bathymetry', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Eddies', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Gyres', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Ocean Currents', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Waves', 'Significant Wave Height', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Sea Surface Topography', 'Sea Surface Height', NULL);

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'A', inventory.timestampToLong('14-JAN-2002'), 'Initial processor version.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 2, 'B', NULL, 'Version B.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 3, 'C', inventory.timestampToLong('27-AUG-2008'), 'Version C.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'cycle' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'version';

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'JASON-1_SENSOR_GEOPHYSICAL_DATA_RECORD', 'Jason-1 Sensor Geophysical Data Record (SGDR)', 'PRODUCT', 'SGDR files contain full accuracy altimeter data, with a high precision orbit (accuracy ~2.5 cm), provided approximately 35 days after data collection. The instruments on Jason-1 make direct observations of the following quantities: altimeter range, significant wave height, ocean radar backscatter cross-section (a measure of wind speed), ionospheric electron content (derived by a simple formula), tropospheric water content, mean sea surface, and position relative to the GPS satellite constellation. The SGDR contain all relevant corrections needed to calculate the sea surface height. It also contains the waveforms that are required for retracking.');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Jessica' AND last_name = 'Hausman';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 130, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'JASON-1';

/*
** JASON-1_SGDR_NETCDF
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'JASON-1_SGDR_NETCDF', 'Jason-1 Sensor Geophysical Data Record (SGDR) NetCDF', 'Jason-1 NASA', 'JASON_NCDF', '2', 'Global Ocean', NULL, '0.0001 decimal degrees', '0.05 decimal degrees', NULL, NULL, NULL, '10 days', NULL, NULL, NULL, NULL, NULL, 'SGDR files contain full accuracy altimeter data, with a high precision orbit (accuracy ~2.5 cm), provided approximately 35 days after data collection. The instruments on Jason-1 make direct observations of the following quantities: altimeter range, significant wave height, ocean radar backscatter cross-section (a measure of wind speed), ionospheric electron content (derived by a simple formula), tropospheric water content, mean sea surface, and position relative to the GPS satellite constellation. The SGDR contain all relevant corrections needed to calculate the sea surface height. It also contains the waveforms that are required for retracking.');

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('27-MAY-2008 12:00:00'), NULL, 66.2, -66.2, 180, -180, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('27-MAY-2008'), inventory.timestampToLong(SYSDATE), 'Initial entry into the PO.DAAC system.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'PRIVATE', 'CYCLE', 'NETCDF', 'NONE', 'MD5', 'BACKTRACK', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PRIVATE', 'file:///store/jason1/private/sgdr_netcdf_c/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-RESTRICTED', 'file:///store/jason1/restricted/sgdr_netcdf_c/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Bathymetry/Seafloor Topography', 'Bathymetry', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Eddies', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Gyres', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Ocean Currents', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Waves', 'Significant Wave Height', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Sea Surface Topography', 'Sea Surface Height', NULL);

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'A', inventory.timestampToLong('14-JAN-2002'), 'Initial processor version.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 2, 'B', NULL, 'Version B.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 3, 'C', inventory.timestampToLong('27-AUG-2008'), 'Version C.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'cycle' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'version';

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'JASON-1_SENSOR_GEOPHYSICAL_DATA_RECORD_NETCDF', 'Jason-1 Sensor Geophysical Data Record (SGDR) NetCDF', 'PRODUCT', 'SGDR files contain full accuracy altimeter data, with a high precision orbit (accuracy ~2.5 cm), provided approximately 35 days after data collection. The instruments on Jason-1 make direct observations of the following quantities: altimeter range, significant wave height, ocean radar backscatter cross-section (a measure of wind speed), ionospheric electron content (derived by a simple formula), tropospheric water content, mean sea surface, and position relative to the GPS satellite constellation. The SGDR contain all relevant corrections needed to calculate the sea surface height. It also contains the waveforms that are required for retracking.');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Jessica' AND last_name = 'Hausman';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', NULL, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'JASON-1';

/*
** JASON-1_SIGDR_NETCDF
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'JASON-1_SIGDR_NETCDF', 'Jason-1 Sensor Interim Geophysical Data Record (SGDR) NetCDF', 'Jason-1 CNES', NULL, '2', 'Global Ocean', NULL, '0.0001 decimal degrees', '0.05 decimal degrees', NULL, NULL, NULL, '10 days', NULL, NULL, NULL, NULL, NULL, 'SGDR files contain full accuracy altimeter data, with a high precision orbit (accuracy ~2.5 cm), provided approximately 35 days after data collection. The instruments on Jason-1 make direct observations of the following quantities: altimeter range, significant wave height, ocean radar backscatter cross-section (a measure of wind speed), ionospheric electron content (derived by a simple formula), tropospheric water content, mean sea surface, and position relative to the GPS satellite constellation. The SGDR contain all relevant corrections needed to calculate the sea surface height. It also contains the waveforms that are required for retracking.');

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('27-MAY-2008 12:00:00'), NULL, 66.2, -66.2, 180, -180, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('27-MAY-2008'), inventory.timestampToLong(SYSDATE), 'Initial entry into the PO.DAAC system.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'RESTRICTED', 'CYCLE', 'NETCDF', 'NONE', 'MD5', 'BACKTRACK', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-RESTRICTED', 'file:///store/jason1/restricted/sigdr_netcdf/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Bathymetry/Seafloor Topography', 'Bathymetry', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Eddies', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Gyres', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Ocean Currents', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Waves', 'Significant Wave Height', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Sea Surface Topography', 'Sea Surface Height', NULL);

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'A', inventory.timestampToLong('14-JAN-2002'), 'Initial processor version.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 2, 'B', NULL, 'Version B.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 3, 'C', inventory.timestampToLong('27-AUG-2008'), 'Version C.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'cycle' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'version';

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'JASON-1_INTERIM_SENSOR_GEOPHYSICAL_DATA_RECORD_NETCDF', 'Jason-1 Interim Sensor Geophysical Data Record (SGDR) NetCDF', 'PRODUCT', 'SGDR files contain full accuracy altimeter data, with a high precision orbit (accuracy ~2.5 cm), provided approximately 35 days after data collection. The instruments on Jason-1 make direct observations of the following quantities: altimeter range, significant wave height, ocean radar backscatter cross-section (a measure of wind speed), ionospheric electron content (derived by a simple formula), tropospheric water content, mean sea surface, and position relative to the GPS satellite constellation. The SGDR contain all relevant corrections needed to calculate the sea surface height. It also contains the waveforms that are required for retracking.');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Jessica' AND last_name = 'Hausman';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', NULL, 0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'JASON-1';

/*
** JASON-1_SSHA
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'JASON-1_SSHA', 'Jason-1 Sea Surface Height Anomaly (SSHA)', 'NASA/JPL/PODAAC', NULL, '2', 'Global Ocean', NULL, '0.0001 decimal degrees', '0.05 decimal degrees', NULL, NULL, NULL, '10 days', NULL, NULL, NULL, NULL, NULL, 'The Jason-1 Sea Surface Height Anomaly product is generated from the Jason-1 Geophysical Data Record (J1 GDR). There are 127 orbits per cycle and each orbit has a period of 112 minutes. The ground track of the orbits are about 315 km apart at the equator. Along track measurements are approximately 1 sec and 6 km apart.  Each orbit is composed of 2 passes, one ascending from -66 deg to +66 deg latitude and the other descending from +66 deg to - 66 deg. The passes are organized this way to avoid data boundaries in the middle of the oceans. All odd-numbered passes are ascending and all even-numbered passes are descending.  It is organized as 10 day repeat cycles containing a maximum of 254 pass files per cycle, the same as the J1 GDR. There is a one to one correspondence between the SSHA and J1 GDR cycles and pass files. Each pass file of the SSHA consists of header records followed by data records containing 11 parameters: time of record in days and milliseconds, latitude, longitude, sea surface height anomaly, significant wave height, inverse barometer, sigma0, total electron content of the atmosphere, ocean depth, and mean sea surface. It is swath data and no images are provided with this product.  The Sea Surface Height Anomaly, the principal parameter in this product, represents the difference between the best estimate of the sea surface height and a mean sea surface. The sea surface height used was corrected for atmospheric effects (ionosphere, wet and dry troposphere), effects due to surface conditions (electromagnetic bias), and other contributions (ocean tides, pole tide, and inverse barometer). Because some users may want to provide their own values for the mean sea surface and inverse barometer effect, the values used in calculating this SSHA are included in the data product.');

UPDATE dataset SET provider_id = 
  (SELECT provider_id FROM provider WHERE short_name = 'NASA/JPL/PODAAC')
WHERE short_name = 'JASON-1_SSHA';

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('14-JAN-2002 10:07:06 PM'), NULL, 66.2, -66.2, 180, -180, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('14-JAN-2002'), inventory.timestampToLong(SYSDATE), 'Dataset policy entry migrated from the PO.DAAC Ingres database.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', NULL, NULL, NULL, 'PUBLIC', 'CYCLE', 'NETCDF', 'NONE', 'MD5', 'BACKTRACK', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PUBLIC', 'file:///store/jason1/public/j1_ssha/data');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/sea_surface_height/jason/j1_ssha/data');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Bathymetry/Seafloor Topography', 'Bathymetry', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Eddies', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Gyres', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Ocean Currents', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Waves', 'Significant Wave Height', NULL);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Sea Surface Topography', 'Sea Surface Height', NULL);

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_resource (dataset_id, name, type, path, description)
VALUES (dataset_id_seq.currval, 'Jason-1 Family Page', 'Project Web Site', 'http://podaac.jpl.nasa.gov/DATA_CATALOG/jason1info.html', NULL);

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'A', inventory.timestampToLong('14-JAN-2002'), 'Initial processor version.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 2, 'B', NULL, 'Version B.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 3, 'C', inventory.timestampToLong('27-AUG-2008'), 'Version C.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'cycle' OR short_name = 'equatorCrossingLongitude' OR short_name = 'equatorCrossingTime' OR short_name = 'version';

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'JASON-1_SEA_SURFACE_HEIGHT_ANOMALY', 'Jason-1 Sea Surface Height Anomaly (SSHA)', 'PRODUCT', 'The Jason-1 Sea Surface Height Anomaly product is generated from the Jason-1 Geophysical Data Record (J1 GDR). There are 127 orbits per cycle and each orbit has a period of 112 minutes. The ground track of the orbits are about 315 km apart at the equator. Along track measurements are approximately 1 sec and 6 km apart.  Each orbit is composed of 2 passes, one ascending from -66 deg to +66 deg latitude and the other descending from +66 deg to - 66 deg. The passes are organized this way to avoid data boundaries in the middle of the oceans. All odd-numbered passes are ascending and all even-numbered passes are descending.  It is organized as 10 day repeat cycles containing a maximum of 254 pass files per cycle, the same as the J1 GDR. There is a one to one correspondence between the SSHA and J1 GDR cycles and pass files. Each pass file of the SSHA consists of header records followed by data records containing 11 parameters: time of record in days and milliseconds, latitude, longitude, sea surface height anomaly, significant wave height, inverse barometer, sigma0, total electron content of the atmosphere, ocean depth, and mean sea surface. It is swath data and no images are provided with this product.  The Sea Surface Height Anomaly, the principal parameter in this product, represents the difference between the best estimate of the sea surface height and a mean sea surface. The sea surface height used was corrected for atmospheric effects (ionosphere, wet and dry troposphere), effects due to surface conditions (electromagnetic bias), and other contributions (ocean tides, pole tide, and inverse barometer). Because some users may want to provide their own values for the mean sea surface and inverse barometer effect, the values used in calculating this SSHA are included in the data product.');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Jessica' AND last_name = 'Hausman';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 132, 0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'JASON-1';

/*
** JASON-1_TRSR
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'JASON-1_TRSR', 'Jason-1 Turbo Rogue Space Receiver', 'Jason-1 NASA', 'JASON_1_TRSR1', '1', 'N/A', NULL, 'N/A', 'N/A', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'TBD');

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('14-JAN-2002 10:07:06 PM'), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('14-JAN-2002'), inventory.timestampToLong(SYSDATE), 'Dataset policy entry migrated from the PO.DAAC Ingres database.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-ONLY', NULL, NULL, NULL, 'PRIVATE', 'CYCLE', 'RAW', 'NONE', 'MD5', 'NONE', 'TBD', 'TBD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PRIVATE', 'file:///store/jason1/private/trsr/data');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, project_id
FROM project
WHERE short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'A', inventory.timestampToLong('14-JAN-2002'), 'Initial processor version.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 2, 'B', NULL, 'Version B.');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 3, 'C', inventory.timestampToLong('27-AUG-2008'), 'Version C.');

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, element_id, 'O'
FROM element_dd
WHERE short_name = 'cycle' OR short_name = 'version';

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'JASON-1_TURBO_ROGUE_SPACE_RECEIVER', 'Jason-1 Turbo Rogue Space Receiver (TRSR)', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Jessica' AND last_name = 'Hausman';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 169,  0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'JASON-1';

COMMIT;

