/*EUR-L2P-AVHRR_METOP_A*/
/*** Provider
*/

INSERT INTO provider (provider_id, short_name, long_name, type)
VALUES (provider_id_seq.nextval, 'EUMETSAT/OSI-SAF', 'European Organisation for the Exploitation of Meteorological Satellites', 'DATA-PROVIDER');

INSERT INTO provider_resource (provider_id, path)
VALUES (provider_id_seq.currval, 'http://www.eumetsat.int/Home/index.htm');

INSERT INTO contact (contact_id, role, first_name, middle_name, last_name, email, phone, fax, address, provider_id, notify_type)
VALUES (contact_id_seq.nextval, 'Investigator', 'Guenole', '', 'Guevel', 'guenole.guevel@meteo.fr', '+ 33 2 9605-6791', '+ 33 2 9605-6737', 'Meteo-France/CMS, Avenue de Lorraine, BP50747 22307 Lannion, France', provider_id_seq.currval, 'SILENT');

/*
** Source and Sensor
*/

INSERT INTO sensor (sensor_id, short_name, long_name, swath_width, description)
VALUES (sensor_id_seq.nextval, 'AVHRR-3', 'Advanced Very High Resolution Radiometer-3', 2900, 'The AVHRR is a radiation-detection imager that can be used for remotely determining cloud cover and the surface temperature.
 Note that the term surface can mean the surface of the Earth, the upper surfaces of clouds, or the surface of a body of water.');

/*
** Datasets
*/

/*
** EUR-L2P-AVHRR_METOP_A 
*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'EUR-L2P-AVHRR_METOP_A', 'L2P Sea Surface Temperature observations from the METOP_A Advanced Very High Resolution Radiometer', 'EUMETSAT', 'METOP_A AVHRR swath L2P SST data set', 2, 'GLOBAL OCEAN', NULL, 'N/A', 'N/A', NULL, NULL, NULL, NULL, NULL, NULL, 'Satellite native swath', 'Geolocation information included for each pixel', 'Information on the the METOP SST algorithm can be found in the following documents: http://www.osi-saf.org/biblio/docs/ss1_pum_leo_sst_2_0.pdf', 'The production of the METOP_A L2P SST data is done by the EUMETSAT/OSI-SAF at Météo-France/CMS. Global AVHRR level 1B Data are acquired at CMS through the EUMETSAT/EUMETCAST system. A cloud mask is applied and SST is retrieved from the AVHRR IR channels by using  a multispectral technique.Data coo
nsists of global coverage at 1 km resolution in three minute observation granules. The METOP_A SST L2P data are compliant with the Group for High Resolution SST (GHRSST) Data Specification (GDS) version 1.7.');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, (Select contact_id from contact where last_name = 'Piolle'  AND rownum<=1));

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, (Select contact_id from contact where last_name = 'Guevel'  AND rownum<=1));


INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('17-APR-2008'), NULL, 90, -90, 180, -180, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong(SYSDATE), inventory.timestampToLong(SYSDATE), 'Dataset Policy created for EUMETSAT METOP_A AVHRR SST Ingestion');


INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ROLLING_STORE', 10, 4, NULL, 'OPEN', 'YEAR-DOY', 'NETCDF', 'BZIP2', 'MD5', 'BACKTRACK', 'None','None');

/*!!!!!!!*/
INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-OPEN', 'file:///store/ghrsst/open/data/L2P/AVHRR_METOP_A/EUR');
/*!!!!!!!*/

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Temperature', 'Sea Surface Temperature', 'Skin Sea Surface Temperature');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, (SELECT PROJECT_ID FROM PROJECT where SHORT_NAME='GHRSST'));

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, (SELECT source_id from source where short_name='METOP-A'), (SELECT SENSOR_ID FROM SENSOR where short_name='AVHRR-3' AND rownum<=1));

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'TBD', inventory.timestampToLong('20-SEP-2009'), 'Initial processor version.');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P European Medspiration AVHRR Global Subskin SST', 'L2P Sea Surface Temperature observations from the METOP_A Advanced Very High Resolution Radiometer', 'PRODUCT', 'GHRSST L2P Subskin Sea Surface Temperature from the Advanced Very High Resolution Radiometer (AVHRR) on the EUMETSAT METOP_A satellite');

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES ((SELECT collection_id from collection where short_name = 'GHRSST Level 2P European Medspiration AVHRR Global Subskin SST'),dataset_id_seq.currval, 'A');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE last_name = 'Piolle' OR last_name = 'Guevel';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 306, 0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'GHRSST';

/*EUR-L3P-GLOB_AVHRR_METOP_A*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'EUR-L3P-GLOB_AVHRR_METOP_A', 'L3P global Sea Surface Temperature observations from the METOP_A Advanced Very High Resolution Radiometer', 'EUMETSAT', 'METOP_A AVHRR swath L2P SST data set', 2, 'GLOBAL OCEAN', NULL, 'N/A', 'N/A', NULL, NULL, NULL, NULL, NULL, NULL, 'Satellite native swath', 'Geolocation information included for each pixel', 'Information on the the METOP SST algorithm can be found in the following documents: http://www.osi-saf.org/biblio/docs/ss1_pum_leo_sst_2_0.pdf', 'The production of the METOP_A L3P SST data is done by the EUMETSAT/OSI-SAF at Météo-France/CMS. Global AVHRR level 1B Data are acquired at CMS through the EUMETSAT/EUMETCAST system. A cloud mask is applied and SST is retrieved from the AVHRR IR channels by using  a multispectral technique.Data coo
nsists of global coverage at 1 km resolution in three minute observation granules. The METOP_A SST L2P data are compliant with the Group for High Resolution SST (GHRSST) Data Specification (GDS) version 1.7.');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, (Select contact_id from contact where last_name = 'Piolle'  AND rownum<=1));

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, (Select contact_id from contact where last_name = 'Guevel'  AND rownum<=1));


INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('17-APR-2008'), NULL, 90, -90, 180, -180, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong(SYSDATE), inventory.timestampToLong(SYSDATE), 'Dataset Policy created for EUMETSAT METOP_A AVHRR SST L3P Ingestion');


INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ROLLING_STORE', 10, 4, NULL, 'OPEN', 'YEAR-DOY', 'NETCDF', 'BZIP2', 'MD5', 'BACKTRACK', 'None','None');

/*!!!!!!!*/
INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-OPEN', 'file:///store/ghrsst/open/data/L3P/GLOB/AVHRR_METOP_A/EUR');
/*!!!!!!!*/

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Temperature', 'Sea Surface Temperature', 'Skin Sea Surface Temperature');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, (SELECT PROJECT_ID FROM PROJECT where SHORT_NAME='GHRSST'));

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, (SELECT source_id from source where short_name='METOP-A'), (SELECT SENSOR_ID FROM SENSOR where short_name='AVHRR-3' AND rownum<=1));

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'TBD', inventory.timestampToLong('01-OCT-2009'), 'Initial processor version.');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 3P European Medspiration AVHRR Global Subskin SST', 'L3P Global Sea Surface Temperature observations from the METOP_A Advanced Very High Resolution Radiometer', 'PRODUCT', 'GHRSST L3P Global Subskin Sea Surface Temperature from the Advanced Very High Resolution Radiometer (AVHRR) on the EUMETSAT METOP_A satellite');

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval,dataset_id_seq.currval, 'A');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE last_name = 'Piolle' OR last_name = 'Guevel';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 313, 0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'GHRSST';

/*EUR-L3P-NAR_AVHRR_METOP_A*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, provider_id_seq.currval, 'EUR-L3P-NAR_AVHRR_METOP_A', 'L3P North Atlantic Regional (NAR) Sea Surface Temperature observations from the METOP_A Advanced Very High Resolution Radiometer', 'EUMETSAT', 'METOP_A AVHRR swath L2P SST data set', 2, 'GLOBAL OCEAN', NULL, 'N/A', 'N/A', NULL, NULL, NULL, NULL, NULL, NULL, 'Satellite native swath', 'Geolocation information included for each pixel', 'Information on the the METOP SST algorithm can be found in the following documents: http://www.osi-saf.org/biblio/docs/ss1_pum_leo_sst_2_0.pdf', 'The production of the METOP_A L3P SST data is done by the EUMETSAT/OSI-SAF at Météo-France/CMS. Global AVHRR level 1B Data are acquired at CMS through the EUMETSAT/EUMETCAST system. A cloud mask is applied and SST is retrieved from the AVHRR IR channels by using  a multispectral technique.Data coo
nsists of global coverage at 1 km resolution in three minute observation granules. The METOP_A SST L3P data are compliant with the Group for High Resolution SST (GHRSST) Data Specification (GDS) version 1.7.');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, (Select contact_id from contact where last_name = 'Piolle'  AND rownum<=1));

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, (Select contact_id from contact where last_name = 'Guevel'  AND rownum<=1));


INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('17-APR-2008'), NULL, 78.24, 23.59, 72.97, -76.02, NULL, NULL, NULL, NULL);

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong(SYSDATE), inventory.timestampToLong(SYSDATE), 'Dataset Policy created for EUMETSAT METOP_A AVHRR SST L3P Ingestion');


INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ROLLING_STORE', 10, 4, NULL, 'OPEN', 'YEAR-DOY', 'NETCDF', 'BZIP2', 'MD5', 'BACKTRACK', 'None','None');

/*!!!!!!!*/
INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-OPEN', 'file:///store/ghrsst/open/data/L3P/NAR/AVHRR_METOP_A/EUR');
/*!!!!!!!*/

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Temperature', 'Sea Surface Temperature', 'Skin Sea Surface Temperature');

INSERT INTO dataset_project (dataset_id, project_id)
VALUES (dataset_id_seq.currval, (SELECT PROJECT_ID FROM PROJECT where SHORT_NAME='GHRSST'));

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, (SELECT source_id from source where short_name='METOP-A'), (SELECT SENSOR_ID FROM SENSOR where short_name='AVHRR-3' AND rownum<=1));

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'TBD', inventory.timestampToLong('01-OCT-2009'), 'Initial processor version.');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 3P European Medspiration AVHRR North Atlantic Region Subskin SST', 'L3P NAR Sea Surface Temperature observations from the METOP_A Advanced Very High Resolution Radiometer', 'PRODUCT', 'GHRSST L3P NAR Subskin Sea Surface Temperature from the Advanced Very High Resolution Radiometer (AVHRR) on the EUMETSAT METOP_A satellite');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE last_name = 'Piolle' OR last_name = 'Guevel';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
VALUES (collection_id_seq.currval,dataset_id_seq.currval, 'A');

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 314, 0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id, dataset_id_seq.currval, 'A'
FROM collection
WHERE short_name = 'GHRSST';



/*NAVO Source Sensor*/
/*
** Source and Sensor
*/

INSERT INTO source (source_id, short_name, long_name, type, orbit_period, incl_angle, description)
VALUES (source_id_seq.nextval, 'NOAA-19', 'National Oceanic and Atmospheric Administration-19', 'SPACECRAFT', 101, 98.7, 'The POES satellite system offers the advantage of daily global coverage, with morning and afternoon orbits that deliver global data, for improvement of weather forecasting. The information received includes cloud cover, storm location, temperature, and heat balance in the earths atmosphere.');

/*NAVO-L2P-AVHRR19_L*/ 

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, (SELECT provider_id from provider where short_name='NAVO'), 'NAVO-L2P-AVHRR19_L', 'Bulk Sea Surface Temperature from the NOAA-19 POES Advanced Very High Resolution Radiometer, 2.2 km resolution, regional coverage, beginning in the year 2009, from the Naval Oceanographic Office, Stennis Space Center, MS, USA', 'Naval Oceanographic Office', 'AVHRR19_L', 2, 'GLOBAL OCEAN', NULL, '2.2km', '2.2km', NULL, NULL, NULL, 'Revisit determined by recorder scheduling', NULL, NULL, 'Satellite native swath', 'Geolocation information included for each pixel','ftp://podaac.jpl.nasa.gov/pub/sea_surface_temperature/avhrr/navoceano_hrpt_lac/doc/avhrr_hrpt_lac_13.html','Multi-Channel SST retreivals generated in real-time from NOAA-19 AVHRR Local Area Coverage data.  Used operationally in Navy oceanographic analyses and forecasts.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ROLLING_STORE', 8, 40, 60, 'OPEN', 'YEAR-DOY', 'NETCDF', 'BZIP2', 'MD5', 'BACKTRACK', 'None','None');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES(dataset_id_seq.currval, 'ARCHIVE-OPEN', 'file:///store/ghrsst/open/data/L2P/AVHRR19_L/NAVO');
INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/AVHRR19_L/NAVO');
INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/AVHRR19_L/NAVO');


INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, p.project_id
FROM project p
WHERE  p.short_name = 'GHRSST';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Temperature', 'Sea Surface Temperature', 'Bulk Sea Surface Temperature');

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('01-OCT-2009'), NULL, 80.00, -70.00, 180.00, -180.00, NULL, NULL, NULL, NULL);

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES(dataset_id_seq.currval, source_id_seq.currval,(select sensor_id from sensor where short_name = 'AVHRR-3' AND rownum <=1) );

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'N/A', NULL, NULL);

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'NAVO-L2P-AVHRR19_L' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P USA NAVOCEANO AVHRR19 Regional SST', 'Bulk Sea Surface Temperature from the NOAA-19 POES Advanced Very High Resolution Radiometer, 2.2 km resolution, regional coverage, beginning in the year 2009, from the Naval Oceanographic Office, Stennis Space Center, MS, USA', 'PRODUCT', 'TBD');

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, (Select contact_id from contact where last_name = 'May'  AND rownum<=1));

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Doug' AND last_name = 'May';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR19_L';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 309, 0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'NAVO-L2P-AVHRR19_L';

/*NAVO-L2P-AVHRR19_G*/


INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, (SELECT provider_id from provider where short_name='NAVO'), 'NAVO-L2P-AVHRR19_G', 'Bulk Sea Surface Temperature from the NOAA-19 POES Advanced Very High Resolution Radiometer, 2.2 km resolution, global coverage, beginning in the year 2009, from the Naval Oceanographic Office, Stennis Space Center, MS, USA', 'Naval Oceanographic Office', 'AVHRR19_G', 2, 'GLOBAL OCEAN', NULL, '8.8km', '8.8km', NULL, NULL, NULL, 'Revisit ~twice each day (night and daytime passes)', NULL, NULL, 'Satellite native swath', 'Geolocation information included for each pixel','ftp://podaac.jpl.nasa.gov/pub/sea_surface_temperature/avhrr/navoceano_mcsst/doc/avhrr_navoceano.html','Multi-Channel SST retreivals generated in real-time from NOAA-19 AVHRR Global Area Coverage data.  Used operationally in Navy oceanographic analyses and forecasts.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ROLLING_STORE', 8, 40, 90, 'OPEN', 'YEAR-DOY', 'NETCDF', 'BZIP2', 'MD5', 'BACKTRACK', 'None','None');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES(dataset_id_seq.currval, 'ARCHIVE-OPEN', 'file:///store/ghrsst/open/data/L2P/AVHRR19_G/NAVO');
INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/AVHRR19_G/NAVO');
INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/AVHRR19_G/NAVO');


INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, p.project_id
FROM project p
WHERE  p.short_name = 'GHRSST';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES(dataset_id_seq.currval, source_id_seq.currval,(select sensor_id from sensor where short_name = 'AVHRR-3' AND rownum <=1) );


INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Temperature', 'Sea Surface Temperature', 'Bulk Sea Surface Temperature');

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('01-OCT-2009'), NULL, 80.00, -70.00, 180.00, -180.00, NULL, NULL, NULL, NULL);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR19_G';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'NAVO-L2P-AVHRR19_G' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P USA NAVOCEANO AVHRR19 Global SST', 'Bulk Sea Surface Temperature from the NOAA-19 POES Advanced Very High Resolution Radiometer, 2.2 km resolution, global coverage, beginning in the year 2009, from the Naval Oceanographic Office, Stennis Space Center, MS, USA', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Doug' AND last_name = 'May';

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, (Select contact_id from contact where last_name = 'May'  AND rownum<=1));


INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR19_G';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 310, 0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'NAVO-L2P-AVHRR19_G';

/*OSDPD policies*/

INSERT INTO source (source_id, short_name, long_name, type, orbit_period, incl_angle, description)
VALUES (source_id_seq.nextval, 'MTSAT1R', 'Multifunctional Transport Satellites 1R', 'Geostationary', 0, 0, 'The Multi-functional Transport Satellite (MTSAT) series fulfills a meteorological function for the Japan Meteorological Agency and an aviation control function for the Civil Aviation Bureau of the Ministry of Land, Infrastructure and Transport.');

INSERT INTO sensor (sensor_id,  short_name, long_name, swath_width, description)
VALUES (sensor_id_seq.nextval, 'MTSAT 1R Imager', 'Multifunctional Transport Satellites 1R Imager', 12000, 'MTSAT 1R carries a 5 channel scanning radiometer with four IR channels and one visible channel.');

/*OSDPD-L2P-MTSAT1R*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, (SELECT provider_id from provider where short_name='OSDPD'), 'OSDPD-L2P-MTSAT1R', 'Sea Surface Temperature, 80E - -165E and 55S-55N, at 0.05 degree resolution from MTSAT-1R Imager Sectors', 'NOAA/NESDIS', 'MTSAT-1R', 2, 'West Pacific', 'International Date line to mid-Indian Ocea, above Japan to below Australia', '4.0 km', '4.0 km', NULL, 0.0, NULL, '60 minutes', NULL, NULL, '', '','GOES-SST Programmers Reference Manual','SST calculated from the IR channels of MTSAT-1R  Full Disk images at full resolution on an hourly basis. In HRIT satellite projection, read out at every pixel L2P data products are then derived following the GHRSST-PP Data Processing Specification (GDS) version 1.5 available from http://ghrsst-pp.metoffice.com/documents/GDS-v1.0-rev1.5.pdf.  Single Sensor Error Statistics (SSES) are provided based on the GDS-v1.5 specification.');

/*!!!!!!!*/
INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ROLLING_STORE', 8, 40, 45, 'OPEN', 'YEAR-DOY', 'NETCDF', 'BZIP2', 'MD5', 'BACKTRACK', 'None','None');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES(dataset_id_seq.currval, 'ARCHIVE-OPEN', 'file:///store/ghrsst/open/data/L2P/MTSAT_1R/OSDPD');
INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/MTSAT_1R/OSDPD');
INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/MTSAT_1R/OSDPD');


INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, p.project_id
FROM project p
WHERE  p.short_name = 'GHRSST';


INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Temperature', 'Sea Surface Temperature', 'Sea Surface Skin Temperature');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'OSDPD-L2P-MTSAT1R';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'OSDPD-L2P-MTSAT1R' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P OSDPD MTSAT-1R Regional SST', 'Sea Surface Temperature, 80E - -165E and 55S-55N, at 0.05 degree resolution from MTSAT-1R Imager Sectors', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Eileen' AND last_name = 'Maturi';

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, (Select contact_id from contact where last_name = 'Maturi'  AND rownum<=1));


INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'OSDPD-L2P-MTSAT1R';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 311, 0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'OSDPD-L2P-MTSAT1R';

/*MGG02 dataset info*/

INSERT INTO source (source_id, short_name, long_name, type, orbit_period, incl_angle, description)
VALUES (source_id_seq.nextval, 'MSG02', 'Meteosat Second Generation', 'Geostationary', 0, 0, 'European meteorological satellites');

/*OSDPD-L2P-MSG02*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, (SELECT provider_id from provider where short_name='OSDPD'), 'OSDPD-L2P-MSG02', 'Sea Surface Temperature, 90W-180W and 55S-55N, at 0.05 degree resolution from MSG-02 Imager Sectors', 'NOAA/NESDIS', 'MSG-02', 2, 'EAST ATLANTIC', 'Mid-Atlantic to East Indian Ocean - Baltic Sea to below Africa ', '3.0 km', '3.0 km', NULL, 0.00, NULL, '15 minutes', NULL, NULL, '', '','GOES-SST Programmers Reference Manual','SST calculated from the IR channels of MSG-02  full disk images at full resolution every 15 minutes. In HRIT satellite projection, read out at every pixel L2P data products are then derived following the GHRSST-PP Data Processing Specification (GDS) version 1.5 available from http://ghrsst-pp.metoffice.com/documents/GDS-v1.0-rev1.5.pdf.  Single Sensor Error Statistics (SSES) are provided based on the GDS-v1.5 specification.');

/*!!!!!!!*/
INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ROLLING_STORE', 8, 90, 60, 'OPEN', 'YEAR-DOY', 'NETCDF', 'BZIP2', 'MD5', 'BACKTRACK', 'None','None');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES(dataset_id_seq.currval, 'ARCHIVE-OPEN', 'file:///store/ghrsst/open/data/L2P/MSG_02/OSDPD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/MSG_02/OSDPD');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/MSG_02/OSDPD');

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, p.project_id
FROM project p
WHERE  p.short_name = 'GHRSST';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES (dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Temperature', 'Sea Surface Temperature', 'Sea Surface Skin Temperature');

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'OSDPD-L2P-MSG02';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'OSDPD-L2P-MSG02' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P GHRSST Level 2P OSDPD MGS-02 Regional SST ', 'Sea Surface Temperature, 90W-180W and 55S-55N, at 0.05 degree resolution from MSG-02 Imager Sectors', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Eileen' AND last_name = 'Maturi';

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, (Select contact_id from contact where last_name = 'Maturi'  AND rownum<=1));

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'OSDPD-L2P-MSG02';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 312, 0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'OSDPD-L2P-MSG02';



/*NEODAAS-L2P-AVHRR19_L */

INSERT INTO source (source_id, short_name, long_name, type, orbit_period, incl_angle, description)
VALUES (source_id_seq.nextval, 'POES', 'Polar Operational Environmental Satellite', 'Polar Orbiting', 102, 98.7, 'NOAA-19 designated NOAA-N (NOAA-N Prime) prior to launch, is the last of the United States National Oceanic and Atmospheric Administrations POES series of weather satellites. NOAA-19 was launched on 6 February 2009.');

INSERT INTO sensor (sensor_id, short_name, long_name, swath_width, description)
VALUES (sensor_id_seq.nextval, 'AVHRR', 'Advanced Very High Resolution Radiometer', 2800, 'The AVHRR/3, a six channel scanning radiometer, provides three solar channels in the visible-near infrared region and three thermal infrared channels.');


/*Dataset*/

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
VALUES (dataset_id_seq.nextval, (SELECT provider_id from provider where short_name='NAVO'), 'NEODAAS-L2P-AVHRR19_L', 'DESCRIPTION TBD', 'Naval Oceanographic Office', 'AVHRR19_L', 2, 'GLOBAL OCEAN', NULL, '2.2km', '2.2km', NULL, NULL, NULL, 'Revisit determined by recorder scheduling', NULL, NULL, 'Satellite native swath', 'Geolocation information included for each pixel','ftp://podaac.jpl.nasa.gov/pub/sea_surface_temperature/avhrr/navoceano_hrpt_lac/doc/avhrr_hrpt_lac_13.html','Multi-Channel SST retreivals generated in real-time from NOAA-19 AVHRR Local Area Coverage data.  Used operationally in Navy oceanographic analyses and forecasts.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
VALUES (dataset_id_seq.currval, 'ROLLING_STORE', 8, 90, 60, 'OPEN', 'YEAR-DOY', 'NETCDF', 'BZIP2', 'MD5', 'BACKTRACK', 'None','None');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES(dataset_id_seq.currval, 'ARCHIVE-OPEN', 'file:///store/ghrsst/open/data/L2P/AVHRR19_L/NEODAAS');
INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/AVHRR19_L/NEODAAS');
INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/AVHRR19_L/NEODAAS');


INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, p.project_id
FROM project p
WHERE  p.short_name = 'GHRSST';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
VALUES (dataset_id_seq.currval, 'Earth Science', 'Oceans', 'Ocean Temperature', 'Sea Surface Temperature', 'Bulk Sea Surface Temperature');

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
VALUES (dataset_id_seq.currval, inventory.timestampToLong('01-OCT-2009'), NULL, 80.00, -70.00, 180.00, -180.00, NULL, NULL, NULL, NULL);

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
VALUES(dataset_id_seq.currval, source_id_seq.currval, sensor_id_seq.currval);

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'N/A', NULL, NULL);

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'NEODAAS-L2P-AVHRR19_L' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'NEODAAS-L2P-AVHRR19_L COLLECTION NAME', 'NEODAAS-L2P-AVHRR19_L Long Name', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Doug' AND last_name = 'May';

INSERT INTO dataset_contact (dataset_id, contact_id)
VALUES (dataset_id_seq.currval, (Select contact_id from contact where last_name = 'May'  AND rownum<=1));


INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'NEODAAS-L2P-AVHRR19_L';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 309, 0,0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'NEODAAS-L2P-AVHRR19_L';



