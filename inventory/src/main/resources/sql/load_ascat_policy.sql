/*
** Copyright (c) 2008-2009, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: load_quikscat_policy.sql 2471 2009-01-21 14:59:28Z shardman $
*/

/*
** This script loads the ASCAT dataset policy for the Inventory 
** schema.
*/

/*
** Collection
*/

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'ASCAT', 'ADVANCED SCATTEROMETER', 'PROJECT', 'This collection references all of the datasets associated with the ASCAT project.');

/*
** Project
*/

INSERT INTO project (project_id, short_name, long_name)
VALUES (project_id_seq.nextval, 'ASCAT', 'Advanced Scatterometer');

/*
** Provider
*/

INSERT INTO provider (provider_id, short_name, long_name, type)
VALUES (provider_id_seq.nextval, 'KNMI', 'ROYAL NETHERLANDS METEOROLOGICAL INSTITUTE', 'DATA-PROVIDER');

INSERT INTO provider_resource (provider_id, path)
VALUES (provider_id_seq.currval, 'http://www.knmi.nl/scatterometer/');

INSERT INTO contact (contact_id, role, first_name, middle_name, last_name, email, phone, fax, address, provider_id, notify_type)
VALUES (contact_id_seq.nextval, 'Technical Contact', 'David', 'F', 'Moroni', 'David.F.Moroni@jpl.nasa.gov', '818-354-2038', '818-393-2718', '4800 Oak Grove Drive Pasadena, CA 91109-8099', provider_id_seq.currval, 'SILENT');

/*
** Source and Sensor
*/

INSERT INTO source (source_id, short_name, long_name, type, orbit_period, incl_angle, description)
VALUES (source_id_seq.nextval, 'MetOp-A ASCAT', 'MetOp-A Advanced Scatterometer', 'SPACECRAFT', 6081.7, 98.7, ' MetOp-A is the first in a series of three polar-orbiting European meteorological satellites, launched on 19 October 2006 as part of the EUMETSAT Polar System. ASCAT data are provided through EUMETSATs EUMETCast system where they are processed at the Ocean and Sea Ice Satellite Application Facility (OSI SAF) and distributed through KNMI.');

INSERT INTO sensor (sensor_id, short_name, long_name, swath_width, description)
VALUES (sensor_id_seq.nextval, 'MetOp-A ASCAT', 'MetOp-A Advanced Scatterometer', 1800, 'ASCAT, representing 1 out of 13 instruments on board MetOp-A, is a dual fan beam C-band (5.25 GHz) vertically polarized Scatterometer, providing measurements across two 550 km swaths, separated by a 700 km nadir blind region. This dual swath configuration allows ASCAT to provide more than twice the daily coverage of the ERS 1 and 2 predecessors with a coverage of nearly 70% of the ice-free oceans each day.');

/*
** Datasets
*/

/*
** ASCAT_12.5KM
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'ASCAT-L2-12.5km', 'MetOp-A ASCAT Level 2 12.5km Ocean Surface Wind Vector Product', 'EUMETSAT/OSI SAF/KNMI', 'MetOp-A ASCAT Level 2 12.5km Ocean Surface Wind Vectors in Full Orbit Swath', 2, 'GLOBAL OCEAN', NULL, 'N/A', 'N/A', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ASCAT Wind Product User Manual, http://www.knmi.nl/scatterometer/publications/pdf/ASCAT_Product_Manual.pdf, http://www.osi-saf.org/, http://www.kmni.nl/scatterometer/', 'KNMIs operational Near-Real-Time ASCAT Level 2 ocen surface wind vector product provided in full orbit files.');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('1-SEP-09'), NULL, 90, -90, 180, -180, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong(SYSDATE), inventory.timestampToLong(SYSDATE), 'Dataset Policy created for ASCAT Ingestion');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', 3, 2, NULL, 'PREVIEW', 'YEAR-DOY', 'NETCDF', 'GZIP', 'MD5', 'Oracle', 'none', 'All users publishing with PO.DAAC data from our providers and partners, please add a citation as outlined here: http://podaac.jpl.nasa.gov/WEB_INFO/citations.html. This product is intended for operational use only and has not been thoroughly evaluated for scientific applications. Please use with caution.');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PREVIEW', 'file:///store/ascat/preview/L2/data/125');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Winds', 'Surface Winds', '10 Meter Ocean Wind Speed');
INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Winds', 'Surface Winds', '10 Meter Ocean Wind Direction');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Atmosphere', 'Atmospheric Winds', 'Surface Winds', '10 Meter Ocean Wind Speed');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Atmosphere', 'Atmospheric Winds', 'Surface Winds', '10 Meter Ocean Wind Direction');


INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'TBD', inventory.timestampToLong('22-JUL-2009'), 'Initial processor version.');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'ASCAT-L2-12.5km', 'MetOp-A ASCAT Level 2 12.5km Ocean Surface Wind Vectors in Full Orbit Swath', 'PRODUCT', 'TBD');

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 316, 0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'ASCAT';

/*
** ASCAT_25.0KM
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'ASCAT-L2-25km', 'MetOp-A ASCAT Level 2 25.0 km Ocean Surface Wind Vector Product', 'EUMETSAT/OSI SAF/KNMI', 'MetOp-A ASCAT Level 2 25.0km Ocean Surface Wind Vectors in Full Orbit Swath', 2, 'GLOBAL OCEAN', NULL, 'N/A', 'N/A', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ASCAT Wind Product User Manual, http://www.knmi.nl/scatterometer/publications/pdf/ASCAT_Product_Manual.pdf, , http://www.osi-saf.org/, http://www.kmni.nl/scatterometer/', 'KNMIs operational Near-Real-Time ASCAT Level 2 ocen surface wind vector product provided in full orbit files.');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, contact_id_seq.currval);

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('1-SEP-09'), NULL, 90, -90, 180, -180, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong(SYSDATE), inventory.timestampToLong(SYSDATE), 'Dataset Policy created for ASCAT Ingestion');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ARCHIVE-DIST', 3, 2, NULL, 'PREVIEW', 'YEAR-DOY', 'NETCDF', 'GZIP', 'MD5', 'Oracle', 'none', 'All users publishing with PO.DAAC data from our providers and partners, please add a citation as outlined here: http://podaac.jpl.nasa.gov/WEB_INFO/citations.html. This product is intended for operational use only and has not been thoroughly evaluated for scientific applications. Please use with caution.');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PREVIEW', 'file:///store/ascat/preview/L2/data/250');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Winds', 'Surface Winds', '10 Meter Ocean Wind Speed');
INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Winds', 'Surface Winds', '10 Meter Ocean Wind Direction');
INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Atmosphere', 'Atmospheric Winds', 'Surface Winds', '10 Meter Ocean Wind Speed');
INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Atmosphere', 'Atmospheric Winds', 'Surface Winds', '10 Meter Ocean Wind Direction');


INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, project_id_seq.currval);

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'TBD', inventory.timestampToLong('22-JUL-2009'), 'Initial processor version.');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'ASCAT-L2-25km', 'MetOp-A ASCAT Level 2 25km Ocean Surface Wind Vectors in Full Orbit Swath', 'PRODUCT', 'TBD');

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval, dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 315, 0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'ASCAT';

COMMIT;
