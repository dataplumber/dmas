/*
** Copyright (c) 2008-2009, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: load_ghrsst_policy.sql 4686 2010-04-02 00:02:50Z gangl $
*/

/* 
** This script loads the collection, source/sensor and dataset policy 
** for the Inventory schema. The GHRSST datasets were already populated 
** with default policy. This script updates that policy.  
** 
** Content for the dataset, dataset_policy and dataset_location_policy 
** tables came from picking Ed's brain and interrogating the archive, 
** FTP and OPeNDAP directory structures.  
** 
** Content for the collection and collection_dataset tables came from 
** Cynthia's spreadsheet and the spreadsheet for populating the GCMD. 
** Currently, only products 245, 253, 254 and 255 are populated fully.
**
** The collection associated with product number 243, encompasses two 
** datasets: EUR-L4UHFnd-MED-v01 and EUR-L4UHRfnd-MED-ODYSSEA.
**
** The datasets REMSS-L4HRfnd-GLOB-mw_ir_rt_OI and UPA-L2P-ATS_NR_2P are
** added manually here since they did not exist in the GHRSST database.
*/

delete from sensor s where (select count(short_name) from sensor where short_name=s.short_name) > 1;

/*
** Collection
*/

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST', 'Global High-Resolution Sea Surface Temperature (GHRSST)', 'PROJECT', 'This collection references all of the datasets associated with the GHRSST project.');

/*
** Project
*/

INSERT INTO project (project_id, short_name, long_name)
VALUES (project_id_seq.nextval, 'GHRSST', 'Group for High Resolution Sea Surface Temperature');

/*
** Provider
**
** Providers and contacts were migrated from the MMR database.
** Associate Ed's contact entry with the PO.DAAC data center.
*/

UPDATE contact set provider_id = 
  (SELECT provider_id FROM provider
   WHERE short_name = 'NASA/JPL/PODAAC')
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

/*
** Sources and Sensors
*/

/* AQUA */

INSERT INTO source (source_id, short_name, long_name, type, orbit_period, incl_angle, description)
VALUES (source_id_seq.nextval, 'AQUA', 'Earth Observing System, AQUA', 'SPACECRAFT', 98.8, 98, 'Aqua (EOS PM), Latin for water, is a NASA Earth Science satellite mission named for the large amount of information that the mission will be collecting about the Earth''s water cycle, including evaporation from the oceans, water vapor in the atmosphere, clouds, precipitation, soil moisture, sea ice, land ice, and snow cover on the land and ice.');

INSERT INTO sensor (sensor_id, short_name, long_name, swath_width, description)
VALUES (sensor_id_seq.nextval, 'AMSR-E', 'Advanced Microwave Scanning Radiometer-EOS', 1445, 'The Advanced Microwave Scanning Radiometer for EOS (AMSR-E) is a twelve-channel, six-frequency, total power passive-microwave radiometer system.');

INSERT INTO sensor (sensor_id, short_name, long_name, swath_width, description)
VALUES (sensor_id_seq.nextval, 'MODIS', 'Moderate-Resolution Imaging Spectroradiometer', 2330, 'The Moderate Resolution Imaging Spectroradiometer (MODIS), is a 36-band spectroradiometer measuring visible and infrared radiation and obtaining data that are being used to derive products ranging from vegetation, land surface cover, and ocean chlorophyll fluorescence to cloud and aerosol properties, fire occurrence, snow cover on the land, and sea ice cover on the oceans.');

/* ENVISAT */

INSERT INTO source (source_id, short_name, long_name, type, orbit_period, incl_angle, description)
VALUES (source_id_seq.nextval, 'ENVISAT', 'Environmental Satellite', 'SPACECRAFT', 100.59, 98.55, 'Envisat, is an advanced polar-orbiting Earth observation satellite which provides measurements of the atmosphere, ocean, land, and ice.');

INSERT INTO sensor (sensor_id,  short_name, long_name, swath_width, description)
VALUES (sensor_id_seq.nextval, 'AATSR', 'Advanced Along-Track Scanning Radiometer', 500, 'Advanced Along-Track Scanning Radiometer (AATSR) is one of the Announcement of Opportunity (AO) instruments on board the European Space Agency (ESA) satellite ENVISAT. It is the most recent in a series of instruments designed primarily to measure Sea Surface Temperature (SST), following on from ATSR-1 and ATSR-2 on board ERS-1 and ERS-2.');

/* GOES-11 */

INSERT INTO source (source_id, short_name, long_name, type, orbit_period, incl_angle, description)
VALUES (source_id_seq.nextval, 'GOES-11', 'Geostationary Operational Environmental Satellite 11', 'SPACECRAFT', 1436, 0.3, 'The GOES system maintains a continuous data stream from a two-GOES system in support of the National Weather Service requirements. These satellites send weather data and pictures that cover various sections of the United States.');

INSERT INTO sensor (sensor_id, short_name, long_name, swath_width, description)
VALUES (sensor_id_seq.nextval, 'GOES-11 Imager', 'Geostationary Operational Environmental Satellite 11-Imager', NULL, 'The GOES Imager is a multi-channel instrument designed to sense radiant and solar-reflected energy from sampled areas of the Earth. The multi-element spectral channels simultaneously sweep east-west and west-east along a north-to-south path by means of a two-axis mirror scan system.');

/* GOES-12 */

INSERT INTO source (source_id, short_name, long_name, type, orbit_period, incl_angle, description)
VALUES (source_id_seq.nextval, 'GOES-12', 'Geostationary Operational Environmental Satellite 12', 'SPACECRAFT', 1436.2, 0.2, 'The GOES system maintains a continuous data stream from a two-GOES system in support of the National Weather Service requirements. These satellites send weather data and pictures that cover various sections of the United States.');

INSERT INTO sensor (sensor_id, short_name, long_name, swath_width, description)
VALUES (sensor_id_seq.nextval, 'GOES-12 Imager', 'Geostationary Operational Environmental Satellite 12-Imager', NULL, 'The GOES Imager is a multi-channel instrument designed to sense radiant and solar-reflected energy from sampled areas of the Earth. The multi-element spectral channels simultaneously sweep east-west and west-east along a north-to-south path by means of a two-axis mirror scan system.');

/* InSitu */

INSERT INTO source (source_id, short_name, long_name, type, orbit_period, incl_angle, description)
VALUES (source_id_seq.nextval, 'InSitu', 'Ships and Moored and Drifing Buoys', 'VESSEL, BUOY', NULL, NULL, NULL);

INSERT INTO sensor (sensor_id, short_name, long_name, swath_width, description)
VALUES (sensor_id_seq.nextval, 'InSitu', 'Ships and Moored and Drifing Buoys', NULL, NULL);

/* METOP-A */

INSERT INTO source (source_id, short_name, long_name, type, orbit_period, incl_angle, description)
VALUES (source_id_seq.nextval, 'METOP-A', 'Meteorological Operational Satellite - A', 'SPACECRAFT', 101.3, 98.7, 'METOP-A is Europe''s first polar-orbiting weather satellite and will give forecasters a new perspective on weather phenomena.');

INSERT INTO sensor (sensor_id, short_name, long_name, swath_width, description)
VALUES (sensor_id_seq.nextval, 'AVHRR-3', 'Advanced Very High Resolution Radiometer-3', 3000, 'The AVHRR is a radiation-detection imager that can be used for remotely determining cloud cover and the surface temperature. Note that the term surface can mean the surface of the Earth, the upper surfaces of clouds, or the surface of a body of water.');

/* MSG */

INSERT INTO source (source_id, short_name, long_name, type, orbit_period, incl_angle, description)
VALUES (source_id_seq.nextval, 'MSG', 'Meteosat Second Generation', 'SPACECRAFT', 1440, NULL, NULL);

INSERT INTO sensor (sensor_id, short_name, long_name, swath_width, description)
VALUES (sensor_id_seq.nextval, 'SEVIRI', 'Spinning Enhanced Visible and Infrared Imager', NULL, 'SEVIRI will observe the Earth with improved performance compared to its Meteosat predecessors, which will be particularly beneficial the meteorological "nowcasters".');

/* Multiple */

INSERT INTO source (source_id, short_name, long_name, type, orbit_period, incl_angle, description)
VALUES (source_id_seq.nextval, 'Multiple', 'Multiple Sources', 'SPACECRAFT', NULL, NULL, NULL);

INSERT INTO sensor (sensor_id, short_name, long_name, swath_width, description)
VALUES (sensor_id_seq.nextval, 'Multiple', 'Multiple Sensors', NULL, NULL);

/* NOAA-16 */

INSERT INTO source (source_id, short_name, long_name, type, orbit_period, incl_angle, description)
VALUES (source_id_seq.nextval, 'NOAA-16', 'National Oceanic '||CHR(38)||' Atmospheric Administration-16', 'SPACECRAFT', 102.1, 99, 'The POES satellite system offers the advantage of daily global coverage, with morning and afternoon orbits that deliver global data, for improvement of weather forecasting. The information received includes cloud cover, storm location, temperature, and heat balance in the earth''s atmosphere.');

/* NOAA-17 */

INSERT INTO source (source_id, short_name, long_name, type, orbit_period, incl_angle, description)
VALUES (source_id_seq.nextval, 'NOAA-17', 'National Oceanic '||CHR(38)||' Atmospheric Administration-17', 'SPACECRAFT', 101.2, 98.7, 'The POES satellite system offers the advantage of daily global coverage, with morning and afternoon orbits that deliver global data, for improvement of weather forecasting. The information received includes cloud cover, storm location, temperature, and heat balance in the earth''s atmosphere.');


/* NOAA-18 */

INSERT INTO source (source_id, short_name, long_name, type, orbit_period, incl_angle, description)
VALUES (source_id_seq.nextval, 'NOAA-18', 'National Oceanic '||CHR(38)||' Atmospheric Administration-18', 'SPACECRAFT', 102.12, 98.74, 'The POES satellite system offers the advantage of daily global coverage, with morning and afternoon orbits that deliver global data, for improvement of weather forecasting. The information received includes cloud cover, storm location, temperature, and heat balance in the earth''s atmosphere.');


/* TERRA */

INSERT INTO source (source_id, short_name, long_name, type, orbit_period, incl_angle, description)
VALUES (source_id_seq.nextval, 'TERRA', 'Earth Observing System, TERRA', 'SPACECRAFT', 96.5, 98, 'Terra (EOS AM-1), Latin for earth, is a multi-national NASA scientific research satellite in a sun-synchronous orbit around the Earth.');

/* TRMM */

INSERT INTO source (source_id, short_name, long_name, type, orbit_period, incl_angle, description)
VALUES (source_id_seq.nextval, 'TRMM', 'Tropical Rainfall Measuring Mission', 'SPACECRAFT', 92.4, 35, 'The Tropical Rainfall Measuring Mission (TRMM) is a joint mission between NASA and the Japan Aerospace Exploration Agency (JAXA) designed to monitor and study tropical rainfall.');

INSERT INTO sensor (sensor_id, short_name, long_name, swath_width, description)
VALUES (sensor_id_seq.nextval, 'TMI', 'TRMM Microwave Imager', 878, 'The Tropical Rainfall Measuring Mission''s (TRMM) Microwave Imager (TMI) is a passive microwave sensor designed to provide quantitative rainfall information over a wide swath under the TRMM satellite.');

/*
** Datasets
*/

/* ABOM-L4HRfnd-AUS-RAMSSA_09km */

UPDATE dataset SET processing_level = '4'
WHERE short_name = 'ABOM-L4HRfnd-AUS-RAMSSA_09km';

UPDATE dataset_policy 
SET data_class = 'ARCHIVE-DIST', data_frequency = NULL, data_volume = NULL, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'ORACLE'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'ABOM-L4HRfnd-AUS-RAMSSA_09km');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L4/AUS/ABOM/RAMSSA_09km'
FROM dataset
WHERE short_name = 'ABOM-L4HRfnd-AUS-RAMSSA_09km';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L4/AUS/ABOM/RAMSSA_09km'
FROM dataset
WHERE short_name = 'ABOM-L4HRfnd-AUS-RAMSSA_09km';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L4/AUS/ABOM/RAMSSA_09km'
FROM dataset
WHERE short_name = 'ABOM-L4HRfnd-AUS-RAMSSA_09km';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'ABOM-L4HRfnd-AUS-RAMSSA_09km' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'ABOM-L4HRfnd-AUS-RAMSSA_09km' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'ABOM-L4HRfnd-AUS-RAMSSA_09km' AND so.short_name = 'AQUA' AND se.short_name = 'AMSR-E';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'ABOM-L4HRfnd-AUS-RAMSSA_09km' AND so.short_name = 'ENVISAT' AND se.short_name = 'AATSR';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'ABOM-L4HRfnd-AUS-RAMSSA_09km' AND so.short_name = 'NOAA-17' AND se.short_name = 'AVHRR';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'ABOM-L4HRfnd-AUS-RAMSSA_09km' AND so.short_name = 'NOAA-18' AND se.short_name = 'AVHRR';

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'ABOM-L4HRfnd-AUS-RAMSSA_09km';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'ABOM-L4HRfnd-AUS-RAMSSA_09km' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 4 Australian Bureau of Meteorology RAMSSA_09km Australia SST', 'GHRSST Level 4 Australian Bureau of Meteorology RAMSSA_09km Australia SST', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'ABOM-L4HRfnd-AUS-RAMSSA_09km';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 266, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'ABOM-L4HRfnd-AUS-RAMSSA_09km';

/* ABOM-L4LRfnd-GLOB-GAMSSA_28km */

UPDATE dataset SET processing_level = '4'
WHERE short_name = 'ABOM-L4LRfnd-GLOB-GAMSSA_28km';

UPDATE dataset_policy 
SET data_class = 'ARCHIVE-DIST', data_frequency = NULL, data_volume = NULL, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'ORACLE'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'ABOM-L4LRfnd-GLOB-GAMSSA_28km');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L4/GLOB/ABOM/GAMSSA_28km'
FROM dataset
WHERE short_name = 'ABOM-L4LRfnd-GLOB-GAMSSA_28km';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L4/GLOB/ABOM/GAMSSA_28km'
FROM dataset
WHERE short_name = 'ABOM-L4LRfnd-GLOB-GAMSSA_28km';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L4/GLOB/ABOM/GAMSSA_28km'
FROM dataset
WHERE short_name = 'ABOM-L4LRfnd-GLOB-GAMSSA_28km';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'ABOM-L4LRfnd-GLOB-GAMSSA_28km' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'ABOM-L4LRfnd-GLOB-GAMSSA_28km' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'ABOM-L4LRfnd-GLOB-GAMSSA_28km' AND so.short_name = 'AQUA' AND se.short_name = 'AMSR-E' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'ABOM-L4LRfnd-GLOB-GAMSSA_28km' AND so.short_name = 'ENVISAT' AND se.short_name = 'AATSR';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'ABOM-L4LRfnd-GLOB-GAMSSA_28km' AND so.short_name = 'NOAA-17' AND se.short_name = 'AVHRR';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'ABOM-L4LRfnd-GLOB-GAMSSA_28km' AND so.short_name = 'NOAA-18' AND se.short_name = 'AVHRR';

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'ABOM-L4LRfnd-GLOB-GAMSSA_28km';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'ABOM-L4LRfnd-GLOB-GAMSSA_28km' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 4 Australian Bureau of Meteorology GAMSSA_28km Global SST', 'GHRSST Level 4 Australian Bureau of Meteorology GAMSSA_28km Global SST', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'ABOM-L4LRfnd-GLOB-GAMSSA_28km';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 292, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'ABOM-L4LRfnd-GLOB-GAMSSA_28km';

/* DMI-L4UHfnd-NSEABALTIC-DMI_OI */

UPDATE dataset SET processing_level = '4'
WHERE short_name = 'DMI-L4UHfnd-NSEABALTIC-DMI_OI';

UPDATE dataset_policy 
SET data_class = 'ARCHIVE-DIST', data_frequency = 24, data_volume = 4, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'GZIP', checksum_type = 'MD5', spatial_type = 'ORACLE'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'DMI-L4UHfnd-NSEABALTIC-DMI_OI');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L4/NSEABALTIC/DMI/DMI_OI'
FROM dataset
WHERE short_name = 'DMI-L4UHfnd-NSEABALTIC-DMI_OI';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L4/NSEABALTIC/DMI/DMI_OI'
FROM dataset
WHERE short_name = 'DMI-L4UHfnd-NSEABALTIC-DMI_OI';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L4/NSEABALTIC/DMI/DMI_OI'
FROM dataset
WHERE short_name = 'DMI-L4UHfnd-NSEABALTIC-DMI_OI';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'DMI-L4UHfnd-NSEABALTIC-DMI_OI' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'DMI-L4UHfnd-NSEABALTIC-DMI_OI' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'DMI-L4UHfnd-NSEABALTIC-DMI_OI' AND so.short_name = 'AQUA' AND se.short_name = 'AMSR-E' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'DMI-L4UHfnd-NSEABALTIC-DMI_OI' AND so.short_name = 'ENVISAT' AND se.short_name = 'AATSR' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'DMI-L4UHfnd-NSEABALTIC-DMI_OI' AND so.short_name = 'MSG' AND se.short_name = 'SEVIRI' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'DMI-L4UHfnd-NSEABALTIC-DMI_OI' AND so.short_name = 'NOAA-17' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'DMI-L4UHfnd-NSEABALTIC-DMI_OI' AND so.short_name = 'NOAA-18' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'DMI-L4UHfnd-NSEABALTIC-DMI_OI';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'DMI-L4UHfnd-NSEABALTIC-DMI_OI' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 4 Denmark DMI North Sea Baltic SST', 'GHRSST L4 Foundation Sea Surface Temperature analysis for the North Sea and Baltic Sea', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'DMI-L4UHfnd-NSEABALTIC-DMI_OI';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 272, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'DMI-L4UHfnd-NSEABALTIC-DMI_OI';

/* EUR-L2P-AMSRE */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'EUR-L2P-AMSRE';

UPDATE dataset_policy 
SET data_class = 'ROLLING-STORE', data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'BACKTRACK'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'EUR-L2P-AMSRE');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/AMSRE/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AMSRE';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/AMSRE/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AMSRE';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/AMSRE/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AMSRE';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L2P-AMSRE' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L2P-AMSRE' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L2P-AMSRE' AND so.short_name = 'AQUA' AND se.short_name = 'AMSR-E' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'EUR-L2P-AMSRE';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'EUR-L2P-AMSRE' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P European Medspiration AMSRE SST', 'L2P Sea Surface Temperature (SST-sub-skin) from gridded AQUA-AMSRE Remote Sensing Systems BMAPS', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'EUR-L2P-AMSRE';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 241, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'EUR-L2P-AMSRE';

/*  EUR-L2P-ATS_NR_2P */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'EUR-L2P-ATS_NR_2P';

UPDATE dataset_policy 
SET data_class = 'ROLLING-STORE', data_frequency = 2, data_volume = 5, data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'BACKTRACK'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'EUR-L2P-ATS_NR_2P');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/ATS_NR_2P/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-ATS_NR_2P';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/ATS_NR_2P/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-ATS_NR_2P';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/ATS_NR_2P/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-ATS_NR_2P';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L2P-ATS_NR_2P' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L2P-ATS_NR_2P' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L2P-ATS_NR_2P' AND so.short_name = 'ENVISAT' AND se.short_name = 'AATSR' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'EUR-L2P-ATS_NR_2P';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'EUR-L2P-ATS_NR_2P' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P European Medspiration AATSR SST', 'GHRSST-PP L2P Sea Surface Temperature SSTskin observations from the ENVISAT Advanced Along Track Scanning Radiometer (AATSR)', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'EUR-L2P-ATS_NR_2P';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 231, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'EUR-L2P-ATS_NR_2P';

/*  EUR-L2P-AVHRR16_G */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'EUR-L2P-AVHRR16_G';

UPDATE dataset_policy 
SET data_class = 'ROLLING-STORE', data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'BACKTRACK'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'EUR-L2P-AVHRR16_G');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/AVHRR16_G/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR16_G';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/AVHRR16_G/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR16_G';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/AVHRR16_G/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR16_G';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L2P-AVHRR16_G' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L2P-AVHRR16_G' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L2P-AVHRR16_G' AND so.short_name = 'NOAA-16' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR16_G';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'EUR-L2P-AVHRR16_G' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P European Medspiration AVHRR16 Global SST', 'Multi Channel SST (MCSST) GAC, MCSST Level 2 (NAVOCEANO) orbital 9km for 100W - 45E and 90N - 70S,  from NOAA-16 AVHRR/2', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR16_G';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 232, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'EUR-L2P-AVHRR16_G';

/* EUR-L2P-AVHRR16_L */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'EUR-L2P-AVHRR16_L';

UPDATE dataset_policy 
SET data_class = 'ROLLING-STORE', data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'BACKTRACK'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'EUR-L2P-AVHRR16_L');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/AVHRR16_L/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR16_L';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/AVHRR16_L/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR16_L';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/AVHRR16_L/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR16_L';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L2P-AVHRR16_L' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L2P-AVHRR16_L' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L2P-AVHRR16_L' AND so.short_name = 'NOAA-16' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR16_L';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'EUR-L2P-AVHRR16_L' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P European Medspiration AVHRR16 Local SST', 'NAVOCEANO Multichannel sea surface temperature  2.2km data (MCSST) for 100W - 45E and 90N - 70S, from NOAA-16 AVHRR/2', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR16_L';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 233, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'EUR-L2P-AVHRR16_L';

/* EUR-L2P-AVHRR17_G */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'EUR-L2P-AVHRR17_G';

UPDATE dataset_policy 
SET data_class = 'ROLLING-STORE', data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'BACKTRACK'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'EUR-L2P-AVHRR17_G');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/AVHRR17_G/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR17_G';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/AVHRR17_G/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR17_G';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/AVHRR17_G/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR17_G';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L2P-AVHRR17_G' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L2P-AVHRR17_G' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L2P-AVHRR17_G' AND so.short_name = 'NOAA-17' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR17_G';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'EUR-L2P-AVHRR17_G' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P European Medspiration AVHRR17 Global SST', 'Multi Channel SST (MCSST) GAC, MCSST Level 2 (NAVOCEANO) orbital 9km for 100W - 45E and 90N - 70S,  from NOAA-17 AVHRR/2', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR17_G';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 234, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'EUR-L2P-AVHRR17_G';

/* EUR-L2P-AVHRR17_L */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'EUR-L2P-AVHRR17_L';

UPDATE dataset_policy 
SET data_class = 'ROLLING-STORE', data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'BACKTRACK'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'EUR-L2P-AVHRR17_L');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/AVHRR17_L/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR17_L';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/AVHRR17_L/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR17_L';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/AVHRR17_L/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR17_L';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L2P-AVHRR17_L' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L2P-AVHRR17_L' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L2P-AVHRR17_L' AND so.short_name = 'NOAA-17' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR17_L';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'EUR-L2P-AVHRR17_L' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P European Medspiration AVHRR17 Local SST', 'NAVOCEANO Multichannel sea surface temperature  2.2km data (MCSST) for 100W - 45E and 90N - 70S, from NOAA-17 AVHRR/2', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR17_L';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 235, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'EUR-L2P-AVHRR17_L';

/* EUR-L2P-NAR16_SST */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'EUR-L2P-NAR16_SST';

UPDATE dataset_policy 
SET data_class = 'ROLLING-STORE', data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'ORACLE'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'EUR-L2P-NAR16_SST');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/NAR16_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-NAR16_SST';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/NAR16_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-NAR16_SST';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/NAR16_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-NAR16_SST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L2P-NAR16_SST' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L2P-NAR16_SST' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L2P-NAR16_SST' AND so.short_name = 'NOAA-16' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'EUR-L2P-NAR16_SST';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'EUR-L2P-NAR16_SST' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P European Medspiration NAR16 SST', 'L2P EUMETSAT-Ocean and Sea Ice Satellite Application Facility (OSI-SAF) Near Atlantic Regional (NAR) Sea Surface Temperature (SST-sub-skin) derived for seven separate areas at 2 km resolution every 6 hours', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'EUR-L2P-NAR16_SST';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 238, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'EUR-L2P-NAR16_SST';

/* EUR-L2P-NAR17_SST */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'EUR-L2P-NAR17_SST';

UPDATE dataset_policy 
SET data_class = 'ROLLING-STORE', data_frequency = 12, data_volume = 4, data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'ORACLE'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'EUR-L2P-NAR17_SST');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/NAR17_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-NAR17_SST';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/NAR17_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-NAR17_SST';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/NAR17_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-NAR17_SST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L2P-NAR17_SST' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L2P-NAR17_SST' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L2P-NAR17_SST' AND so.short_name = 'NOAA-17' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'EUR-L2P-NAR17_SST';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'EUR-L2P-NAR17_SST' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P European Medspiration NAR17 SST', 'L2P EUMETSAT-Ocean and Sea Ice Satellite Application Facility (OSI-SAF) Near Atlantic Regional (NAR) Sea Surface Temperature (SST-sub-skin) derived for seven separate areas at 2 km resolution every 6 hours', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'EUR-L2P-NAR17_SST';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 239, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'EUR-L2P-NAR17_SST';

/* EUR-L2P-NAR18_SST */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'EUR-L2P-NAR18_SST';

UPDATE dataset_policy 
SET data_class = 'ROLLING-STORE', data_frequency = 12, data_volume = 4, data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'ORACLE'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'EUR-L2P-NAR18_SST');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/NAR18_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-NAR18_SST';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/NAR18_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-NAR18_SST';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/NAR18_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-NAR18_SST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L2P-NAR18_SST' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L2P-NAR18_SST' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L2P-NAR18_SST' AND so.short_name = 'NOAA-18' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'EUR-L2P-NAR18_SST';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'EUR-L2P-NAR18_SST' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P European Medspiration NAR18 SST', 'L2P EUMETSAT-Ocean and Sea Ice Satellite Application Facility (OSI-SAF) Near Atlantic Regional (NAR) Sea Surface Temperature (SST-sub-skin) derived for seven separate areas at 2 km resolution every 6 hours', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'EUR-L2P-NAR18_SST';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 244, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'EUR-L2P-NAR18_SST';

/* EUR-L2P-SEVIRI_SST */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'EUR-L2P-SEVIRI_SST';

UPDATE dataset_policy 
SET data_class = 'ROLLING-STORE', data_frequency = 3, data_volume = 4, data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'BACKTRACK'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'EUR-L2P-SEVIRI_SST');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/SEVIRI_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-SEVIRI_SST';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/SEVIRI_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-SEVIRI_SST';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/SEVIRI_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-SEVIRI_SST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L2P-SEVIRI_SST' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L2P-SEVIRI_SST' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L2P-SEVIRI_SST' AND so.short_name = 'MSG' AND se.short_name = 'SEVIRI' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'EUR-L2P-SEVIRI_SST';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'EUR-L2P-SEVIRI_SST' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P European Medspiration SEVIRI SST', 'Sea Surface Temperature (SST-sub-skin) Low and Mid Latitudes (LML), 100W - 45E and 60N - 60S, at 0.1 degree resolution from MSG SEVIRI', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'EUR-L2P-SEVIRI_SST';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 240, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'EUR-L2P-SEVIRI_SST';

/* EUR-L2P-TMI */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'EUR-L2P-TMI';

UPDATE dataset_policy 
SET data_class = 'ROLLING-STORE', data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'BACKTRACK'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'EUR-L2P-TMI');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/TMI/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-TMI';

INSERT INTO dataset_location_policy (dataset_id, type, base_path) 
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/TMI/EUR' 
FROM dataset 
WHERE short_name = 'EUR-L2P-TMI';

INSERT INTO dataset_location_policy (dataset_id, type, base_path) 
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/TMI/EUR' 
FROM dataset 
WHERE short_name = 'EUR-L2P-TMI';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L2P-TMI' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L2P-TMI' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L2P-TMI' AND so.short_name = 'TRMM' AND se.short_name = 'TMI' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'EUR-L2P-TMI';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'EUR-L2P-TMI' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P European Medspiration TMI SST', 'L2P Sea Surface Temperature (SST-sub-skin) from gridded Tropical Rainfall Mapping Mission (TRMM) Microwave Imager (TMI)  Remote Sensing Systems BMAPS', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'EUR-L2P-TMI';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 242, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'EUR-L2P-TMI';

/* EUR-L4HRfnd-GLOB-ODYSSEA */

UPDATE dataset SET processing_level = '4'
WHERE short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA';

UPDATE dataset_policy 
SET data_class = 'ARCHIVE-DIST', data_frequency = 24, data_volume = 4, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'ORACLE'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L4/GLOB/EUR/ODYSSEA'
FROM dataset
WHERE short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L4/GLOB/EUR/ODYSSEA'
FROM dataset
WHERE short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L4/GLOB/EUR/ODYSSEA'
FROM dataset
WHERE short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Fronts', NULL
FROM dataset
WHERE short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Gyres', NULL
FROM dataset
WHERE short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Ocean Currents', NULL
FROM dataset
WHERE short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Upwelling/Downwelling', NULL
FROM dataset
WHERE short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Heat Budget', 'Evaporation', NULL
FROM dataset
WHERE short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Heat Budget', 'Heat Flux', NULL
FROM dataset
WHERE short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Temperature', 'Sea Surface Temperature', NULL
FROM dataset
WHERE short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA' AND so.short_name = 'AQUA' AND se.short_name = 'AMSR-E' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA' AND so.short_name = 'ENVISAT' AND se.short_name = 'AATSR' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA' AND so.short_name = 'GOES-11' AND se.short_name = 'GOES-11 Imager' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA' AND so.short_name = 'GOES-12' AND se.short_name = 'GOES-12 Imager' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA' AND so.short_name = 'MSG' AND se.short_name = 'SEVIRI' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA' AND so.short_name = 'NOAA-17' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA' AND so.short_name = 'NOAA-18' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA' AND so.short_name = 'TRMM' AND se.short_name = 'TMI' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST_EUR_GLOB_L4_ODYSSEA_SST', 'GHRSST Mersea Global L4 ODYSSEA merged SST', 'PRODUCT', 'The ODYSSEA Global Sea Surface Temperature is a GHRSST Level 4 product produced at Ifremer/CERSAT (France). It provides a daily cloud-free global field of foundation sea surface temperature at 10km resolution (0.1 degree) over the full globe. It is generated by merging all available satellite sensors for sea surface temperature (micro-wave and infra-red), after careful intercalibration using AATSR sensor as reference (previously re-calibrated using all available in situ data). The development of the global real-time sea surface temperature at Ifremer/CERSAT is supported by European Commission (GMES framework) initially in the frame of MERSEA project. PO.DAAC, through the GHRSST Global Data Assembly Center (http://ghrsst.jpl.nasa.gov) acts as a global distribution node for all GHRSST L4 products in conjunction with the NOAA Longterm Stewardship and Reanalysis Facility (LTSRF; http://ghrsst.nodc.noaa.gov). More information on GHRSST can be found here: http://www.ghrsst-pp.org/.');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 245, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'EUR-L4HRfnd-GLOB-ODYSSEA';

/* EUR-L4UHFnd-MED-v01 */

UPDATE dataset SET processing_level = '4'
WHERE short_name = 'EUR-L4UHFnd-MED-v01';

UPDATE dataset_policy 
SET data_class = 'ARCHIVE-DIST', data_frequency = 24, data_volume = 4, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'ORACLE'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'EUR-L4UHFnd-MED-v01');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L4/MED/EUR'
FROM dataset
WHERE short_name = 'EUR-L4UHFnd-MED-v01';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L4/MED/EUR'
FROM dataset
WHERE short_name = 'EUR-L4UHFnd-MED-v01';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L4/MED/EUR'
FROM dataset
WHERE short_name = 'EUR-L4UHFnd-MED-v01';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L4UHFnd-MED-v01' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L4UHFnd-MED-v01' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4UHFnd-MED-v01' AND so.short_name = 'Multiple' AND se.short_name = 'Multiple' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'EUR-L4UHFnd-MED-v01';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'EUR-L4UHFnd-MED-v01' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 4 European Medspiration Mediterranean Ultra-High Foundation SST', 'Analysed L4 foundation sea surface temperature over Mediterranean Sea', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'EUR-L4UHFnd-MED-v01';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 243, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'EUR-L4UHFnd-MED-v01';

/* EUR-L4UHRfnd-GAL-ODYSSEA */

UPDATE dataset SET processing_level = '4'
WHERE short_name = 'EUR-L4UHRfnd-GAL-ODYSSEA';

UPDATE dataset_policy 
SET data_class = 'ARCHIVE-DIST', data_frequency = 24, data_volume = 4, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'ORACLE'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'EUR-L4UHRfnd-GAL-ODYSSEA');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L4/GAL/EUR/ODYSSEA'
FROM dataset
WHERE short_name = 'EUR-L4UHRfnd-GAL-ODYSSEA';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L4/GAL/EUR/ODYSSEA'
FROM dataset
WHERE short_name = 'EUR-L4UHRfnd-GAL-ODYSSEA';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L4/GAL/EUR/ODYSSEA'
FROM dataset
WHERE short_name = 'EUR-L4UHRfnd-GAL-ODYSSEA';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L4UHRfnd-GAL-ODYSSEA' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L4UHRfnd-GAL-ODYSSEA' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4UHRfnd-GAL-ODYSSEA' AND so.short_name = 'AQUA' AND se.short_name = 'AMSR-E' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4UHRfnd-GAL-ODYSSEA' AND so.short_name = 'ENVISAT' AND se.short_name = 'AATSR' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4UHRfnd-GAL-ODYSSEA' AND so.short_name = 'MSG' AND se.short_name = 'SEVIRI' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4UHRfnd-GAL-ODYSSEA' AND so.short_name = 'NOAA-16' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4UHRfnd-GAL-ODYSSEA' AND so.short_name = 'NOAA-17' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4UHRfnd-GAL-ODYSSEA' AND so.short_name = 'NOAA-18' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4UHRfnd-GAL-ODYSSEA' AND so.short_name = 'TRMM' AND se.short_name = 'TMI' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'EUR-L4UHRfnd-GAL-ODYSSEA';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'EUR-L4UHRfnd-GAL-ODYSSEA' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 4 European Medspiration ODYSSEA Galapagos SST', 'Analysed L4 foundation sea surface temperature over Galapagos and Pacific Central America', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'EUR-L4UHRfnd-GAL-ODYSSEA';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 273, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'EUR-L4UHRfnd-GAL-ODYSSEA';

/* EUR-L4UHRfnd-MED-ODYSSEA */

UPDATE dataset SET processing_level = '4'
WHERE short_name = 'EUR-L4UHRfnd-MED-ODYSSEA';

UPDATE dataset_policy 
SET data_class = 'ARCHIVE-DIST', data_frequency = 24, data_volume = 4, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'ORACLE'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'EUR-L4UHRfnd-MED-ODYSSEA');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L4/MED/EUR/ODYSSEA'
FROM dataset
WHERE short_name = 'EUR-L4UHRfnd-MED-ODYSSEA';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L4/MED/EUR/ODYSSEA'
FROM dataset
WHERE short_name = 'EUR-L4UHRfnd-MED-ODYSSEA';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L4/MED/EUR/ODYSSEA'
FROM dataset
WHERE short_name = 'EUR-L4UHRfnd-MED-ODYSSEA';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L4UHRfnd-MED-ODYSSEA' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L4UHRfnd-MED-ODYSSEA' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4UHRfnd-MED-ODYSSEA' AND so.short_name = 'AQUA' AND se.short_name = 'AMSR-E' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4UHRfnd-MED-ODYSSEA' AND so.short_name = 'ENVISAT' AND se.short_name = 'AATSR' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4UHRfnd-MED-ODYSSEA' AND so.short_name = 'MSG' AND se.short_name = 'SEVIRI' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4UHRfnd-MED-ODYSSEA' AND so.short_name = 'NOAA-16' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4UHRfnd-MED-ODYSSEA' AND so.short_name = 'NOAA-17' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4UHRfnd-MED-ODYSSEA' AND so.short_name = 'NOAA-18' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4UHRfnd-MED-ODYSSEA' AND so.short_name = 'TRMM' AND se.short_name = 'TMI' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'EUR-L4UHRfnd-MED-ODYSSEA';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'EUR-L4UHRfnd-MED-ODYSSEA' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT cp.collection_id, d.dataset_id, 'A'
FROM dataset d, collection_product cp
WHERE d.short_name = 'EUR-L4UHRfnd-MED-ODYSSEA' AND cp.product_id = 243;

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'EUR-L4UHRfnd-MED-ODYSSEA';

/* EUR-L4UHRfnd-NWE-ODYSSEA */

UPDATE dataset SET processing_level = '4'
WHERE short_name = 'EUR-L4UHRfnd-NWE-ODYSSEA';

UPDATE dataset_policy 
SET data_class = 'ARCHIVE-DIST', data_frequency = 24, data_volume = 4, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'ORACLE'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'EUR-L4UHRfnd-NWE-ODYSSEA');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L4/NWE/EUR/ODYSSEA'
FROM dataset
WHERE short_name = 'EUR-L4UHRfnd-NWE-ODYSSEA';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L4/NWE/EUR/ODYSSEA'
FROM dataset
WHERE short_name = 'EUR-L4UHRfnd-NWE-ODYSSEA';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L4/NWE/EUR/ODYSSEA'
FROM dataset
WHERE short_name = 'EUR-L4UHRfnd-NWE-ODYSSEA';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L4UHRfnd-NWE-ODYSSEA' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'EUR-L4UHRfnd-NWE-ODYSSEA' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4UHRfnd-NWE-ODYSSEA' AND so.short_name = 'AQUA' AND se.short_name = 'AMSR-E' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4UHRfnd-NWE-ODYSSEA' AND so.short_name = 'ENVISAT' AND se.short_name = 'AATSR' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4UHRfnd-NWE-ODYSSEA' AND so.short_name = 'MSG' AND se.short_name = 'SEVIRI' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4UHRfnd-NWE-ODYSSEA' AND so.short_name = 'NOAA-16' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4UHRfnd-NWE-ODYSSEA' AND so.short_name = 'NOAA-17' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4UHRfnd-NWE-ODYSSEA' AND so.short_name = 'NOAA-18' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4UHRfnd-NWE-ODYSSEA' AND so.short_name = 'TRMM' AND se.short_name = 'TMI' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'EUR-L4UHRfnd-NWE-ODYSSEA';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'EUR-L4UHRfnd-NWE-ODYSSEA' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 4 European Medspiration ODYSSEA NW Europe SST', 'Analysed L4 foundation sea surface temperature over North-Western European shelves', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'EUR-L4UHRfnd-NWE-ODYSSEA';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 274, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'EUR-L4UHRfnd-NWE-ODYSSEA';

/* JPL-L2P-MODIS_A */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'JPL-L2P-MODIS_A';

UPDATE dataset_policy 
SET data_class = 'ROLLING-STORE', data_frequency = 1, data_volume = 24, data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'ORACLE'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'JPL-L2P-MODIS_A');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/MODIS_A/JPL'
FROM dataset
WHERE short_name = 'JPL-L2P-MODIS_A';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/MODIS_A/JPL'
FROM dataset
WHERE short_name = 'JPL-L2P-MODIS_A';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/MODIS_A/JPL'
FROM dataset
WHERE short_name = 'JPL-L2P-MODIS_A';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'JPL-L2P-MODIS_A' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'JPL-L2P-MODIS_A' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'JPL-L2P-MODIS_A' AND so.short_name = 'AQUA' AND se.short_name = 'MODIS' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'JPL-L2P-MODIS_A';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'JPL-L2P-MODIS_A' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P USA NASA MODIS Aqua SST', 'GHRSST L2P Skin Sea Surface Temperature from the Moderate Resolution Imaging Spectroradiometer (MODIS) on the NASA Aqua satellite', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'JPL-L2P-MODIS_A';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 248, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'JPL-L2P-MODIS_A';

/* JPL-L2P-MODIS_T */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'JPL-L2P-MODIS_T';

UPDATE dataset_policy 
SET data_class = 'ROLLING-STORE', data_frequency = 1, data_volume = 24, data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'ORACLE'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'JPL-L2P-MODIS_T');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/MODIS_T/JPL'
FROM dataset
WHERE short_name = 'JPL-L2P-MODIS_T';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/MODIS_T/JPL'
FROM dataset
WHERE short_name = 'JPL-L2P-MODIS_T';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/MODIS_T/JPL'
FROM dataset
WHERE short_name = 'JPL-L2P-MODIS_T';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'JPL-L2P-MODIS_T' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'JPL-L2P-MODIS_T' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'JPL-L2P-MODIS_T' AND so.short_name = 'TERRA' AND se.short_name = 'MODIS' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'JPL-L2P-MODIS_T';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'JPL-L2P-MODIS_T' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P USA NASA MODIS Terra SST', 'GHRSST L2P Skin Sea Surface Temperature from the Moderate Resolution Imaging Spectroradiometer (MODIS) on the NASA Terra satellite', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'JPL-L2P-MODIS_T';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 250, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'JPL-L2P-MODIS_T';

/* NAVO-L2P-AVHRR17_G */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'NAVO-L2P-AVHRR17_G';

UPDATE dataset_policy 
SET data_class = 'ROLLING-STORE', data_frequency = 2, data_volume = 5, data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'BACKTRACK'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'NAVO-L2P-AVHRR17_G');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/AVHRR17_G/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR17_G';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/AVHRR17_G/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR17_G';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/AVHRR17_G/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR17_G';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'NAVO-L2P-AVHRR17_G' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'NAVO-L2P-AVHRR17_G' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'NAVO-L2P-AVHRR17_G' AND so.short_name = 'NOAA-17' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR17_G';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'NAVO-L2P-AVHRR17_G' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P USA NAVOCEANO AVHRR17 Global SST', 'Bulk Sea Surface Temperature from the NOAA-17 POES Advanced Very High Resolution Radiometer, 8.8 km resolution, 14 orbits per day, global, beginning in the year 2006, from the Naval Oceanographic Office, Stennis Space Center, MS, USA', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR17_G';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 260, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'NAVO-L2P-AVHRR17_G';

/* NAVO-L2P-AVHRR17_L */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'NAVO-L2P-AVHRR17_L';

UPDATE dataset_policy 
SET data_class = 'ROLLING-STORE', data_frequency = 2, data_volume = 14, data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'BACKTRACK'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'NAVO-L2P-AVHRR17_L');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/AVHRR17_L/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR17_L';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/AVHRR17_L/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR17_L';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/AVHRR17_L/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR17_L';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'NAVO-L2P-AVHRR17_L' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'NAVO-L2P-AVHRR17_L' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'NAVO-L2P-AVHRR17_L' AND so.short_name = 'NOAA-17' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR17_L';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'NAVO-L2P-AVHRR17_L' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P USA NAVOCEANO AVHRR17 Local SST ', 'Bulk Sea Surface Temperature from the NOAA-17 POES Advanced Very High Resolution Radiometer, 2.2 km resolution, regional coverage, beginning in the year 2006, from the Naval Oceanographic Office, Stennis Space Center, MS, USA', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR17_L';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 259, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'NAVO-L2P-AVHRR17_L';

/* NAVO-L2P-AVHRR18_G */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'NAVO-L2P-AVHRR18_G';

UPDATE dataset_policy 
SET data_class = 'ROLLING-STORE', data_frequency = 2, data_volume = 5, data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'BACKTRACK'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'NAVO-L2P-AVHRR18_G');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/AVHRR18_G/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR18_G';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/AVHRR18_G/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR18_G';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/AVHRR18_G/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR18_G';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'NAVO-L2P-AVHRR18_G' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'NAVO-L2P-AVHRR18_G' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'NAVO-L2P-AVHRR18_G' AND so.short_name = 'NOAA-18' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR18_G';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'NAVO-L2P-AVHRR18_G' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P USA NAVOCEANO AVHRR18 Global SST', 'Bulk Sea Surface Temperature from the NOAA-18 POES Advanced Very High Resolution Radiometer, 8.8 km resolution, 14 orbits per day, global, beginning in the year 2006, from the Naval Oceanographic Office, Stennis Space Center, MS, USA', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR18_G';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 261, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'NAVO-L2P-AVHRR18_G';

/* NAVO-L2P-AVHRR18_L */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'NAVO-L2P-AVHRR18_L';

UPDATE dataset_policy 
SET data_class = 'ROLLING-STORE', data_frequency = 2, data_volume = 14, data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'BACKTRACK'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'NAVO-L2P-AVHRR18_L');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/AVHRR18_L/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR18_L';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/AVHRR18_L/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR18_L';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/AVHRR18_L/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR18_L';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'NAVO-L2P-AVHRR18_L' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'NAVO-L2P-AVHRR18_L' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'NAVO-L2P-AVHRR18_L' AND so.short_name = 'NOAA-18' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR18_L';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'NAVO-L2P-AVHRR18_L' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P USA NAVOCEANO AVHRR18 Local SST', 'Bulk Sea Surface Temperature from the NOAA-18 POES Advanced Very High Resolution Radiometer, 2.2 km resolution, regional coverage, beginning in the year 2006, from the Naval Oceanographic Office, Stennis Space Center, MS, USA', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR18_L';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 252, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'NAVO-L2P-AVHRR18_L';

/* NAVO-L2P-AVHRRMTA_G */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'NAVO-L2P-AVHRRMTA_G';

UPDATE dataset_policy 
SET data_class = 'ROLLING-STORE', data_frequency = 2, data_volume = 14, data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'BACKTRACK'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'NAVO-L2P-AVHRRMTA_G');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/AVHRRMTA_G/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRRMTA_G';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/AVHRRMTA_G/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRRMTA_G';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/AVHRRMTA_G/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRRMTA_G';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'NAVO-L2P-AVHRRMTA_G' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'NAVO-L2P-AVHRRMTA_G' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'NAVO-L2P-AVHRRMTA_G' AND so.short_name = 'METOP-A' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRRMTA_G';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'NAVO-L2P-AVHRRMTA_G' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P USA NAVOCEANO AVHRR METOP-A Global SST', 'Bulk Sea Surface Temperature from the METOP-A Advanced Very High Resolution Radiometer, 8.8 km resolution, 14 orbits per day, global, beginning in the year 2007, from the Naval Oceanographic Office, Stennis Space Center, MS, USA', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRRMTA_G';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 271, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'NAVO-L2P-AVHRRMTA_G';

/* NAVO-L4HR1m-GLOB-K10_SST */

UPDATE dataset SET processing_level = '4'
WHERE short_name = 'NAVO-L4HR1m-GLOB-K10_SST';

UPDATE dataset_policy 
SET data_class = 'ARCHIVE-DIST', data_frequency = NULL, data_volume = NULL, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'ORACLE'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'NAVO-L4HR1m-GLOB-K10_SST');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L4/GLOB/NAVO/K10_SST'
FROM dataset
WHERE short_name = 'NAVO-L4HR1m-GLOB-K10_SST';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L4/GLOB/NAVO/K10_SST'
FROM dataset
WHERE short_name = 'NAVO-L4HR1m-GLOB-K10_SST';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L4/GLOB/NAVO/K10_SST'
FROM dataset
WHERE short_name = 'NAVO-L4HR1m-GLOB-K10_SST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'NAVO-L4HR1m-GLOB-K10_SST' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'NAVO-L4HR1m-GLOB-K10_SST' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'NAVO-L4HR1m-GLOB-K10_SST' AND so.short_name = 'AQUA' AND se.short_name = 'AMSR-E' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'NAVO-L4HR1m-GLOB-K10_SST' AND so.short_name = 'GOES-11' AND se.short_name = 'GOES-11 Imager' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'NAVO-L4HR1m-GLOB-K10_SST' AND so.short_name = 'GOES-12' AND se.short_name = 'GOES-12 Imager' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'NAVO-L4HR1m-GLOB-K10_SST' AND so.short_name = 'NOAA-17' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'NAVO-L4HR1m-GLOB-K10_SST' AND so.short_name = 'NOAA-18' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'NAVO-L4HR1m-GLOB-K10_SST';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'NAVO-L4HR1m-GLOB-K10_SST' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 4 USA NAVOCEANO K10_SST Global SST', 'GHRSST Level 4 USA NAVOCEANO K10_SST Global SST', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'NAVO-L4HR1m-GLOB-K10_SST';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 249, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'NAVO-L4HR1m-GLOB-K10_SST';

/* NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI */

UPDATE dataset SET processing_level = '4'
WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI';

UPDATE dataset_policy 
SET data_class = 'ARCHIVE-DIST', data_frequency = 24, data_volume = 4, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'ORACLE'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L4/GLOB/NCDC/AVHRR_AMSR_OI'
FROM dataset
WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L4/GLOB/NCDC/AVHRR_AMSR_OI'
FROM dataset
WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L4/GLOB/NCDC/AVHRR_AMSR_OI'
FROM dataset
WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Gyres', NULL
FROM dataset
WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Ocean Currents', NULL
FROM dataset
WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Upwelling/Downwelling', NULL
FROM dataset
WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Heat Budget', 'Evaporation', NULL
FROM dataset
WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Heat Budget', 'Heat Flux', NULL
FROM dataset
WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Temperature', 'Sea Surface Temperature', NULL
FROM dataset
WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI' AND so.short_name = 'AQUA' AND se.short_name = 'AMSR-E' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI' AND so.short_name = 'InSitu' AND se.short_name = 'InSitu' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI' AND so.short_name = 'NOAA-16' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI' AND so.short_name = 'NOAA-17' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI' AND so.short_name = 'NOAA-18' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST_NCDC_GLOB_L4_AVHRR_AMSR_OI_SST', 'GHRSST NCDC Global L4 AVHRR _AMSR_OI merged SST', 'PRODUCT', 'A GHRSST L4 sea surface temperature analysis produced daily at the NOAA National Climatic Data Center using optimal interpolation from AVHRR Pathfinder Version 5 data (http://pathfinder.nodc.noaa.gov) (when available, otherwise operational AVHRR data are used) and in situ ship and buoy observations. A second
 similar product is available that also includes AMSR-E data from June 2002 onward. To avoid a jump in the analysis and to provide the longest, most consistent time series, this product uses only the AVHRR Pathfinder and in situ data. The OI analysis is a daily average SST that is bias adjusted using a spatially smoothed 7-day in situ SST average. Both day and night satellite fields are independently adjusted. More information is available at http://www.ncdc.noaa.gov/oa/climate/research/sst/oi-daily.php.PO.DAAC, through the GHRSST Global Data Assembly Center (http://ghrsst.jpl.nasa.gov) acts as a global distribution node for all GHRSST L4 products in conjunction with the NOAA Longterm Stewardship and Reanalysis Facility (LTSRF; http://ghrsst.nodc.noaa.gov). More information on GHRSST can be found here: http://www.ghrsst-pp.org/.');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 254, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI';

/* NCDC-L4LRblend-GLOB-AVHRR_OI */

UPDATE dataset SET processing_level = '4'
WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_OI';

UPDATE dataset_policy 
SET data_class = 'ARCHIVE-DIST', data_frequency = 24, data_volume = 4, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'ORACLE'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_OI');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L4/GLOB/NCDC/AVHRR_OI'
FROM dataset
WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_OI';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L4/GLOB/NCDC/AVHRR_OI'
FROM dataset
WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_OI';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L4/GLOB/NCDC/AVHRR_OI'
FROM dataset
WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_OI';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Gyres', NULL
FROM dataset
WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_OI';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Ocean Currents', NULL
FROM dataset
WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_OI';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Upwelling/Downwelling', NULL
FROM dataset
WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_OI';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Heat Budget', 'Evaporation', NULL
FROM dataset
WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_OI';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Heat Budget', 'Heat Flux', NULL
FROM dataset
WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_OI';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Temperature', 'Sea Surface Temperature', NULL
FROM dataset
WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_OI';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'NCDC-L4LRblend-GLOB-AVHRR_OI' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'NCDC-L4LRblend-GLOB-AVHRR_OI' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'NCDC-L4LRblend-GLOB-AVHRR_OI' AND so.short_name = 'InSitu' AND se.short_name = 'InSitu' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'NCDC-L4LRblend-GLOB-AVHRR_OI' AND so.short_name = 'NOAA-16' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'NCDC-L4LRblend-GLOB-AVHRR_OI' AND so.short_name = 'NOAA-17' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'NCDC-L4LRblend-GLOB-AVHRR_OI' AND so.short_name = 'NOAA-18' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_OI';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'NCDC-L4LRblend-GLOB-AVHRR_OI' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST_NCDC_GLOB_L4_AVHRR_OI_SST', 'GHRSST NCDC Global L4 AVHRR _OI  merged SST', 'PRODUCT', 'A GHRSST L4 sea surface temperature analysis produced daily at the NOAA National Climatic Data Center using optimal interpolation from AVHRR Pathfinder Version 5 data (http://pathfinder.nodc.noaa.gov) (when available, otherwise operational AVHRR data are used) and in situ ship and buoy observations. A second similar product is available that also includes AMSR-E data from June 2002 onward. To avoid a jump in the analysis and to provide the longest, most consistent time series, this product uses only the AVHRR Pathfinder and in situ data. The OI analysis is a daily average SST that is bias adjusted using a spatially smoothed 7-day in situ SST average. Both day and night satellite fields are independently adjusted. More information is available at http://www.ncdc.noaa.
gov/oa/climate/research/sst/oi-daily.php. PO.DAAC, through the GHRSST Global Data Assembly Center (http://ghrsst.jpl.nasa.gov) acts as a global distribution node for all GHRSST L4 products in conjunction with the NOAA Longterm Stewardship and Reanalysis Facility (LTSRF; http://ghrsst.nodc.noaa.gov).More information on GHRSST can be found here: http://www.ghrsst-pp.org/.');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'NCDC-L4LRblend-GLOB-AVHRR_OI';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 253, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'NCDC-L4LRblend-GLOB-AVHRR_OI';

/* NEODAAS-L2P-AVHRR17_L */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'NEODAAS-L2P-AVHRR17_L';

UPDATE dataset_policy 
SET data_class = 'ROLLING-STORE', data_frequency = 2, data_volume = 14, data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'BACKTRACK'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'NEODAAS-L2P-AVHRR17_L');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/AVHRR17_L/NEODAAS'
FROM dataset
WHERE short_name = 'NEODAAS-L2P-AVHRR17_L';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/AVHRR17_L/NEODAAS'
FROM dataset
WHERE short_name = 'NEODAAS-L2P-AVHRR17_L';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/AVHRR17_L/NEODAAS'
FROM dataset
WHERE short_name = 'NEODAAS-L2P-AVHRR17_L';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'NEODAAS-L2P-AVHRR17_L' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'NEODAAS-L2P-AVHRR17_L' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'NEODAAS-L2P-AVHRR17_L' AND so.short_name = 'NOAA-17' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'NEODAAS-L2P-AVHRR17_L';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'NEODAAS-L2P-AVHRR17_L' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P NEODAAS AVHRR17 Local SST', 'GHRSST Level 2P NEODAAS AVHRR17 Local SST', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'NEODAAS-L2P-AVHRR17_L';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 298, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'NEODAAS-L2P-AVHRR17_L';

/* NEODAAS-L2P-AVHRR18_L */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'NEODAAS-L2P-AVHRR18_L';

UPDATE dataset_policy 
SET data_class = 'ROLLING-STORE', data_frequency = 2, data_volume = 14, data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'BACKTRACK'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'NEODAAS-L2P-AVHRR18_L');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/AVHRR18_L/NEODAAS'
FROM dataset
WHERE short_name = 'NEODAAS-L2P-AVHRR18_L';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/AVHRR18_L/NEODAAS'
FROM dataset
WHERE short_name = 'NEODAAS-L2P-AVHRR18_L';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/AVHRR18_L/NEODAAS'
FROM dataset
WHERE short_name = 'NEODAAS-L2P-AVHRR18_L';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'NEODAAS-L2P-AVHRR18_L' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'NEODAAS-L2P-AVHRR18_L' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'NEODAAS-L2P-AVHRR18_L' AND so.short_name = 'NOAA-18' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'NEODAAS-L2P-AVHRR18_L';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'NEODAAS-L2P-AVHRR18_L' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P NEODAAS AVHRR18 Local SST', 'GHRSST Level 2P NEODAAS AVHRR18 Local SST', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'NEODAAS-L2P-AVHRR18_L';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 291, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'NEODAAS-L2P-AVHRR18_L';

/* OSDPD-L2P-GOES11 */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'OSDPD-L2P-GOES11';

UPDATE dataset_policy 
SET data_class = 'ROLLING-STORE', data_frequency = 2, data_volume = 31, data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'GZIP', checksum_type = 'MD5', spatial_type = 'BACKTRACK'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'OSDPD-L2P-GOES11');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/GOES11/OSDPD'
FROM dataset
WHERE short_name = 'OSDPD-L2P-GOES11';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/GOES11/OSDPD'
FROM dataset
WHERE short_name = 'OSDPD-L2P-GOES11';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/GOES11/OSDPD'
FROM dataset
WHERE short_name = 'OSDPD-L2P-GOES11';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'OSDPD-L2P-GOES11' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'OSDPD-L2P-GOES11' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'OSDPD-L2P-GOES11' AND so.short_name = 'GOES-11' AND se.short_name = 'GOES-11 Imager' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'OSDPD-L2P-GOES11';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'OSDPD-L2P-GOES11' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P USA NAVOCEANO GOES11 SST', 'Sea Surface Temperature, 180W-110W and 60N-45S, at 0.05 degree resolution from GOES-11 Imager', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'OSDPD-L2P-GOES11';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 257, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'OSDPD-L2P-GOES11';

/* OSDPD-L2P-GOES12 */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'OSDPD-L2P-GOES12';

UPDATE dataset_policy 
SET data_class = 'ROLLING-STORE', data_frequency = 2, data_volume = 31, data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'GZIP', checksum_type = 'MD5', spatial_type = 'BACKTRACK'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'OSDPD-L2P-GOES12');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/GOES12/OSDPD'
FROM dataset
WHERE short_name = 'OSDPD-L2P-GOES12';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/GOES12/OSDPD'
FROM dataset
WHERE short_name = 'OSDPD-L2P-GOES12';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/GOES12/OSDPD'
FROM dataset
WHERE short_name = 'OSDPD-L2P-GOES12';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'OSDPD-L2P-GOES12' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'OSDPD-L2P-GOES12' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'OSDPD-L2P-GOES12' AND so.short_name = 'GOES-12' AND se.short_name = 'GOES-12 Imager' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'OSDPD-L2P-GOES12';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'OSDPD-L2P-GOES12' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P USA NAVOCEANO GOES12 SST', 'Sea Surface Temperature, 30W-135W and 65N-50S, at 0.05 degree resolution from GOES-12 Imager', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'OSDPD-L2P-GOES12';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 258, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'OSDPD-L2P-GOES12';

/* REMSS-L2P-AMSRE */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'REMSS-L2P-AMSRE';

UPDATE dataset_policy 
SET data_class = 'ROLLING-STORE', data_frequency = 3, data_volume = 15, data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'GZIP', checksum_type = 'MD5', spatial_type = 'BACKTRACK'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'REMSS-L2P-AMSRE');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/AMSRE/REMSS'
FROM dataset
WHERE short_name = 'REMSS-L2P-AMSRE';

INSERT INTO dataset_location_policy (dataset_id, type, base_path) 
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/AMSRE/REMSS' 
FROM dataset 
WHERE short_name = 'REMSS-L2P-AMSRE';

INSERT INTO dataset_location_policy (dataset_id, type, base_path) 
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/AMSRE/REMSS' 
FROM dataset 
WHERE short_name = 'REMSS-L2P-AMSRE';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'REMSS-L2P-AMSRE' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'REMSS-L2P-AMSRE' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'REMSS-L2P-AMSRE' AND so.short_name = 'AQUA' AND se.short_name = 'AMSR-E' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'REMSS-L2P-AMSRE';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'REMSS-L2P-AMSRE' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P USA Remote Sensing Systems AMSRE SST', 'GHRSST-PP L2P Global Sea Surface Temperature SSTsub-skin observations from the AQUA Advanced Scanning Microwave Radiometer - Earth Observing System (AMSR-E)', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'REMSS-L2P-AMSRE';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 246, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'REMSS-L2P-AMSRE';

/* REMSS-L2P_GRIDDED_25-AMSRE */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'REMSS-L2P_GRIDDED_25-AMSRE';

UPDATE dataset_policy 
SET data_class = 'ARCHIVE-DIST', data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'GZIP', checksum_type = 'MD5', spatial_type = 'ORACLE'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'REMSS-L2P_GRIDDED_25-AMSRE');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P_GRIDDED/AMSRE/REMSS'
FROM dataset
WHERE short_name = 'REMSS-L2P_GRIDDED_25-AMSRE';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P_GRIDDED/AMSRE/REMSS'
FROM dataset
WHERE short_name = 'REMSS-L2P_GRIDDED_25-AMSRE';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'REMSS-L2P_GRIDDED_25-AMSRE' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'REMSS-L2P_GRIDDED_25-AMSRE' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'REMSS-L2P_GRIDDED_25-AMSRE' AND so.short_name = 'AQUA' AND se.short_name = 'AMSR-E' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'REMSS-L2P_GRIDDED_25-AMSRE';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'REMSS-L2P_GRIDDED_25-AMSRE' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 3P USA Remote Sensing Systems AMSRE SST', 'GHRSST-PP L2P Global Sea Surface Temperature SSTsub-skin observations from the AQUA Advanced Scanning Microwave Radiometer - Earth  Observing System (AMSR-E)', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'REMSS-L2P_GRIDDED_25-AMSRE';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 269, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'REMSS-L2P_GRIDDED_25-AMSRE';

/* REMSS-L2P_GRIDDED_25-TMI */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'REMSS-L2P_GRIDDED_25-TMI';

UPDATE dataset_policy 
SET data_class = 'ARCHIVE-DIST', data_frequency = 12, data_volume = 2, data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'GZIP', checksum_type = 'MD5', spatial_type = 'ORACLE'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'REMSS-L2P_GRIDDED_25-TMI');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P_GRIDDED/TMI/REMSS'
FROM dataset
WHERE short_name = 'REMSS-L2P_GRIDDED_25-TMI';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P_GRIDDED/TMI/REMSS'
FROM dataset
WHERE short_name = 'REMSS-L2P_GRIDDED_25-TMI';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'REMSS-L2P_GRIDDED_25-TMI' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'REMSS-L2P_GRIDDED_25-TMI' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'REMSS-L2P_GRIDDED_25-TMI' AND so.short_name = 'TRMM' AND se.short_name = 'TMI' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'REMSS-L2P_GRIDDED_25-TMI';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'REMSS-L2P_GRIDDED_25-TMI' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 3P USA Remote Sensing Systems TMI SST', 'GHRSST-PP L2P Global Sea Surface Temperature SSTsub-skin observations from the TRMM Microwave Radiometer', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'REMSS-L2P_GRIDDED_25-TMI';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 270, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'REMSS-L2P_GRIDDED_25-TMI';

/* REMSS-L2P-TMI */

UPDATE dataset SET processing_level = '2P'
WHERE short_name = 'REMSS-L2P-TMI';

UPDATE dataset_policy 
SET data_class = 'ROLLING-STORE', data_duration = 90, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'GZIP', checksum_type = 'MD5', spatial_type = 'BACKTRACK'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'REMSS-L2P-TMI');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/TMI/REMSS'
FROM dataset
WHERE short_name = 'REMSS-L2P-TMI';

INSERT INTO dataset_location_policy (dataset_id, type, base_path) 
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/TMI/REMSS' 
FROM dataset 
WHERE short_name = 'REMSS-L2P-TMI';

INSERT INTO dataset_location_policy (dataset_id, type, base_path) 
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/TMI/REMSS' 
FROM dataset 
WHERE short_name = 'REMSS-L2P-TMI';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'REMSS-L2P-TMI' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'REMSS-L2P-TMI' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'REMSS-L2P-TMI' AND so.short_name = 'TRMM' AND se.short_name = 'TMI' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'REMSS-L2P-TMI';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'REMSS-L2P-TMI' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 2P USA Remote Sensing Systems TMI SST', 'GHRSST-PP L2P Global Sea Surface Temperature SSTsub-skin observations from the TRMM Microwave Radiometer', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'REMSS-L2P-TMI';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 247, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'REMSS-L2P-TMI';

/* REMSS-L4HRfnd-GLOB-amsre_OI */

UPDATE dataset SET processing_level = '4'
WHERE short_name = 'REMSS-L4HRfnd-GLOB-amsre_OI';

UPDATE dataset_policy 
SET data_class = 'ARCHIVE-DIST', data_frequency = NULL, data_volume = NULL, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'GZIP', checksum_type = 'MD5', spatial_type = 'ORACLE'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'REMSS-L4HRfnd-GLOB-amsre_OI');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L4/GLOB/REMSS/amsre_OI'
FROM dataset
WHERE short_name = 'REMSS-L4HRfnd-GLOB-amsre_OI';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L4/GLOB/REMSS/amsre_OI'
FROM dataset
WHERE short_name = 'REMSS-L4HRfnd-GLOB-amsre_OI';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L4/GLOB/REMSS/amsre_OI'
FROM dataset
WHERE short_name = 'REMSS-L4HRfnd-GLOB-amsre_OI';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-amsre_OI' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-amsre_OI' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-amsre_OI' AND so.short_name = 'AQUA' AND se.short_name = 'AMSR-E' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'REMSS-L4HRfnd-GLOB-amsre_OI';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-amsre_OI' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'REMSS-L4HRfnd-GLOB-amsre_OI';

/* REMSS-L4HRfnd-GLOB-mw_ir_OI */

UPDATE dataset SET processing_level = '4'
WHERE short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI';

UPDATE dataset_policy 
SET data_class = 'ARCHIVE-DIST', data_frequency = 24, data_volume = 4, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'GZIP', checksum_type = 'MD5', spatial_type = 'ORACLE'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L4/GLOB/REMSS/mw_ir_OI'
FROM dataset
WHERE short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L4/GLOB/REMSS/mw_ir_OI'
FROM dataset
WHERE short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L4/GLOB/REMSS/mw_ir_OI'
FROM dataset
WHERE short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI' AND so.short_name = 'AQUA' AND se.short_name = 'AMSR-E' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI' AND so.short_name = 'AQUA' AND se.short_name = 'MODIS' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI' AND so.short_name = 'TRMM' AND se.short_name = 'TMI' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST Level 4 USA Remote Sensing Systems mw_ir_OI Global SST', 'GHRSST-PP L2P Global Sea Surface Temperature SSTsub-skin optimum interpolated observations from the AQUA AMSR-E, AQUA MODIS, and TRMM TMI', 'PRODUCT', 'TBD');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'N', 264, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI';

/* REMSS-L4HRfnd-GLOB-mw_ir_rt_OI */

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
SELECT dataset_id_seq.nextval, d.provider_id, 'REMSS-L4HRfnd-GLOB-mw_ir_rt_OI', d.long_name, d.original_provider, d.provider_dataset_name, d.processing_level, d.region, d.region_detail, d.latitude_resolution, d.longitude_resolution, d.horizontal_resolution_range, d.altitude_resolution, d.depth_resolution, d.temporal_resolution, d.temporal_resolution_range, d.ellipsoid_type, d.projection_type, d.projection_detail, d.reference, d.description
FROM dataset d
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI';

INSERT INTO dataset_citation (dataset_id, title, creator, version, publisher, series_name, release_date_long, release_place, citation_detail, online_resource)
SELECT dataset_id_seq.currval, dc.title, dc.creator, dc.version, dc.publisher, dc.series_name, dc.release_date_long, dc.release_place, dc.citation_detail, dc.online_resource
FROM dataset d, dataset_citation dc
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI' AND d.dataset_id = dc.dataset_id;

INSERT INTO dataset_contact (dataset_id, contact_id)
SELECT dataset_id_seq.currval, dc.contact_id
FROM dataset d, dataset_contact dc
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI' AND d.dataset_id = dc.dataset_id;

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
SELECT dataset_id_seq.currval, dc.start_time_long, dc.stop_time_long, dc.north_lat, dc.south_lat, dc.east_lon, dc.west_lon, dc.min_altitude, dc.max_altitude, dc.min_depth, dc.max_depth
FROM dataset d, dataset_coverage dc
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI' AND d.dataset_id = dc.dataset_id;

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
SELECT dataset_id_seq.currval, dmh.version_id, dmh.creation_date_long, dmh.last_revision_date_long, dmh.revision_history
FROM dataset d, dataset_meta_history dmh
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI' AND d.dataset_id = dmh.dataset_id;

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
SELECT dataset_id_seq.currval, dp.data_class, dp.data_frequency, dp.data_volume, 10,  dp.access_type, dp.base_path_append_type, dp.data_format, dp.compress_type, dp.checksum_type, dp.spatial_type, dp.access_constraint, dp.use_constraint
FROM dataset d, dataset_policy dp
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI' AND d.dataset_id = dp.dataset_id;

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L4/GLOB/REMSS/mw_ir_OI');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L4/GLOB/REMSS/mw_ir_OI');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L4/GLOB/REMSS/mw_ir_OI');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id_seq.currval, dp.category, dp.topic, dp.term, dp.variable, dp.variable_detail
FROM dataset d, dataset_parameter dp
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI' AND d.dataset_id = dp.dataset_id;

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, dp.project_id
FROM dataset d, dataset_project dp
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI' AND d.dataset_id = dp.dataset_id;

INSERT INTO dataset_resource (dataset_id, name, path, type, description)
SELECT dataset_id_seq.currval, dr.name, dr.path, dr.type, dr.description
FROM dataset d, dataset_resource dr
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI' AND d.dataset_id = dr.dataset_id;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT dataset_id_seq.currval, ds.source_id, ds.sensor_id
FROM dataset d, dataset_source ds
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI' AND d.dataset_id = ds.dataset_id;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id_seq.currval, dv.version_id, dv.version, dv.version_date_long, dv.description
FROM dataset d, dataset_version dv
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI' AND d.dataset_id = dv.dataset_id;

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, ge.element_id, ge.obligation_flag
FROM dataset d, granule_element ge
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_OI' AND d.dataset_id = ge.dataset_id;

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, dataset_id_seq.currval, 'A'
FROM collection c
WHERE c.short_name = 'GHRSST';

/* REMSS-L4HRfnd-GLOB-tmi_amsre_OI */

UPDATE dataset SET processing_level = '4'
WHERE short_name = 'REMSS-L4HRfnd-GLOB-tmi_amsre_OI';

UPDATE dataset_policy 
SET data_class = 'ARCHIVE-DIST', data_frequency = NULL, data_volume = NULL, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'GZIP', checksum_type = 'MD5', spatial_type = 'ORACLE'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'REMSS-L4HRfnd-GLOB-tmi_amsre_OI');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L4/GLOB/REMSS/tmi_amsre_OI'
FROM dataset
WHERE short_name = 'REMSS-L4HRfnd-GLOB-tmi_amsre_OI';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L4/GLOB/REMSS/tmi_amsre_OI'
FROM dataset
WHERE short_name = 'REMSS-L4HRfnd-GLOB-tmi_amsre_OI';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L4/GLOB/REMSS/tmi_amsre_OI'
FROM dataset
WHERE short_name = 'REMSS-L4HRfnd-GLOB-tmi_amsre_OI';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-tmi_amsre_OI' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-tmi_amsre_OI' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-tmi_amsre_OI' AND so.short_name = 'AQUA' AND se.short_name = 'AMSR-E' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-tmi_amsre_OI' AND so.short_name = 'TRMM' AND se.short_name = 'TMI' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'REMSS-L4HRfnd-GLOB-tmi_amsre_OI';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-tmi_amsre_OI' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'REMSS-L4HRfnd-GLOB-tmi_amsre_OI';

/* REMSS-L4HRfnd-GLOB-tmi_OI */

UPDATE dataset SET processing_level = '4'
WHERE short_name = 'REMSS-L4HRfnd-GLOB-tmi_OI';

UPDATE dataset_policy 
SET data_class = 'ARCHIVE-DIST', data_frequency = NULL, data_volume = NULL, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'GZIP', checksum_type = 'MD5', spatial_type = 'ORACLE'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'REMSS-L4HRfnd-GLOB-tmi_OI');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L4/GLOB/REMSS/tmi_OI'
FROM dataset
WHERE short_name = 'REMSS-L4HRfnd-GLOB-tmi_OI';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L4/GLOB/REMSS/tmi_OI'
FROM dataset
WHERE short_name = 'REMSS-L4HRfnd-GLOB-tmi_OI';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L4/GLOB/REMSS/tmi_OI'
FROM dataset
WHERE short_name = 'REMSS-L4HRfnd-GLOB-tmi_OI';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-tmi_OI' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-tmi_OI' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-tmi_OI' AND so.short_name = 'TRMM' AND se.short_name = 'TMI' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'REMSS-L4HRfnd-GLOB-tmi_OI';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'REMSS-L4HRfnd-GLOB-tmi_OI' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'REMSS-L4HRfnd-GLOB-tmi_OI';

/* UKMO-L4HRfnd-GLOB-OSTIA */

UPDATE dataset SET processing_level = '4'
WHERE short_name = 'UKMO-L4HRfnd-GLOB-OSTIA';

UPDATE dataset_policy 
SET data_class = 'ARCHIVE-DIST', data_frequency = 24, data_volume = 4, access_type = 'PUBLIC', base_path_append_type = 'YEAR-DOY', data_format = 'NETCDF', compress_type = 'BZIP2', checksum_type = 'MD5', spatial_type = 'ORACLE'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset 
  WHERE short_name = 'UKMO-L4HRfnd-GLOB-OSTIA');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L4/GLOB/UKMO/OSTIA'
FROM dataset
WHERE short_name = 'UKMO-L4HRfnd-GLOB-OSTIA';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L4/GLOB/UKMO/OSTIA'
FROM dataset
WHERE short_name = 'UKMO-L4HRfnd-GLOB-OSTIA';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L4/GLOB/UKMO/OSTIA'
FROM dataset
WHERE short_name = 'UKMO-L4HRfnd-GLOB-OSTIA';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Gyres', NULL
FROM dataset
WHERE short_name = 'UKMO-L4HRfnd-GLOB-OSTIA';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Ocean Currents', NULL
FROM dataset
WHERE short_name = 'UKMO-L4HRfnd-GLOB-OSTIA';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Circulation', 'Upwelling/Downwelling', NULL
FROM dataset
WHERE short_name = 'UKMO-L4HRfnd-GLOB-OSTIA';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Heat Budget', 'Evaporation', NULL
FROM dataset
WHERE short_name = 'UKMO-L4HRfnd-GLOB-OSTIA';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Heat Budget', 'Heat Flux', NULL
FROM dataset
WHERE short_name = 'UKMO-L4HRfnd-GLOB-OSTIA';

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id, 'Earth Science', 'Oceans', 'Ocean Temperature', 'Sea Surface Temperature', NULL
FROM dataset
WHERE short_name = 'UKMO-L4HRfnd-GLOB-OSTIA';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'UKMO-L4HRfnd-GLOB-OSTIA' AND p.short_name = 'GHRSST';

INSERT INTO dataset_project (dataset_id, project_id)
SELECT d.dataset_id, p.project_id
FROM dataset d, project p
WHERE d.short_name = 'UKMO-L4HRfnd-GLOB-OSTIA' AND p.short_name = 'EOSDIS';

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'UKMO-L4HRfnd-GLOB-OSTIA' AND so.short_name = 'AQUA' AND se.short_name = 'AMSR-E' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'UKMO-L4HRfnd-GLOB-OSTIA' AND so.short_name = 'ENVISAT' AND se.short_name = 'AATSR' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'UKMO-L4HRfnd-GLOB-OSTIA' AND so.short_name = 'InSitu' AND se.short_name = 'InSitu' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'UKMO-L4HRfnd-GLOB-OSTIA' AND so.short_name = 'MSG' AND se.short_name = 'SEVIRI' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'UKMO-L4HRfnd-GLOB-OSTIA' AND so.short_name = 'NOAA-16' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'UKMO-L4HRfnd-GLOB-OSTIA' AND so.short_name = 'NOAA-17' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'UKMO-L4HRfnd-GLOB-OSTIA' AND so.short_name = 'NOAA-18' AND se.short_name = 'AVHRR-3' ;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'UKMO-L4HRfnd-GLOB-OSTIA' AND so.short_name = 'TRMM' AND se.short_name = 'TMI' ;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
SELECT dataset_id, 1, 'N/A', NULL, NULL
FROM dataset
WHERE short_name = 'UKMO-L4HRfnd-GLOB-OSTIA';

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT d.dataset_id, g.element_id, 'O'
FROM dataset d, element_dd g
WHERE d.short_name = 'UKMO-L4HRfnd-GLOB-OSTIA' AND (g.short_name = 'batch' OR g.short_name = 'northLatitude' OR g.short_name = 'southLatitude' OR g.short_name = 'eastLongitude' OR g.short_name = 'westLongitude' OR g.short_name = 'version');

INSERT INTO collection (collection_id, short_name, long_name, type, description)
VALUES (collection_id_seq.nextval, 'GHRSST_UKMO_GLOB_L4_OSTIA_SST', 'GHRSST UK Met Office  Global L4 OSTIA merged SST', 'PRODUCT', 'A GHRSST Level 4 sea surface temperature analysis produced daily on an operational basis at the UK Met Office using optimal interpolation from AVHRR, AATSR, SEVIRI, AMSRE, TMI and in situ sensors. PO.DAAC, through the GHRSST Global Data Assembly Center (http://ghrsst.jpl.nasa.gov) acts as a global distribution node for all GHRSST L4 products in conjunction with the NOAA Longterm Stewardship and Reanalysis Facility (LTSRF; http://ghrsst.nodc.noaa.gov). More information on GHRSST can be found here: http://www.ghrsst-pp.org/.');

INSERT INTO collection_contact (collection_id, contact_id)
SELECT collection_id_seq.currval, contact_id
FROM contact
WHERE first_name = 'Edward' AND last_name = 'Armstrong';

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT collection_id_seq.currval, dataset_id, 'A'
FROM dataset
WHERE short_name = 'UKMO-L4HRfnd-GLOB-OSTIA';

INSERT INTO collection_product (collection_id, visible_flag, product_id, echo_submit_date_long, gcmd_submit_date_long)
VALUES (collection_id_seq.currval, 'Y', 255, 0, 0);

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, d.dataset_id, 'A'
FROM collection c, dataset d
WHERE c.short_name = 'GHRSST' AND d.short_name = 'UKMO-L4HRfnd-GLOB-OSTIA';

/* UPA-L2P-ATS-NR_2P */

/* Inherit from EUR-L2P-ATS_NR_2P. */

INSERT INTO dataset (dataset_id, provider_id, short_name, long_name, original_provider, provider_dataset_name, processing_level, region, region_detail, latitude_resolution, longitude_resolution, horizontal_resolution_range, altitude_resolution, depth_resolution, temporal_resolution, temporal_resolution_range, ellipsoid_type, projection_type, projection_detail, reference, description)
SELECT dataset_id_seq.nextval, d.provider_id, 'UPA-L2P-ATS_NR_2P', d.long_name, d.original_provider, d.provider_dataset_name, d.processing_level, d.region, d.region_detail, d.latitude_resolution, d.longitude_resolution, d.horizontal_resolution_range, d.altitude_resolution, d.depth_resolution, d.temporal_resolution, d.temporal_resolution_range, d.ellipsoid_type, d.projection_type, d.projection_detail, d.reference, d.description
FROM dataset d
WHERE d.short_name = 'EUR-L2P-ATS_NR_2P';

INSERT INTO dataset_citation (dataset_id, title, creator, version, publisher, series_name, release_date_long, release_place, citation_detail, online_resource)
SELECT dataset_id_seq.currval, dc.title, dc.creator, dc.version, dc.publisher, dc.series_name, dc.release_date_long, dc.release_place, dc.citation_detail, dc.online_resource
FROM dataset d, dataset_citation dc
WHERE d.short_name = 'EUR-L2P-ATS_NR_2P' AND d.dataset_id = dc.dataset_id;

INSERT INTO dataset_contact (dataset_id, contact_id)
SELECT dataset_id_seq.currval, dc.contact_id
FROM dataset d, dataset_contact dc
WHERE d.short_name = 'EUR-L2P-ATS_NR_2P' AND d.dataset_id = dc.dataset_id;

INSERT INTO dataset_coverage (dataset_id, start_time_long, stop_time_long, north_lat, south_lat, east_lon, west_lon, min_altitude, max_altitude, min_depth, max_depth)
SELECT dataset_id_seq.currval, inventory.timestampToLong('24-NOV-2008 01:00:00'), NULL, dc.north_lat, dc.south_lat, dc.east_lon, dc.west_lon, dc.min_altitude, dc.max_altitude, dc.min_depth, dc.max_depth
FROM dataset d, dataset_coverage dc
WHERE d.short_name = 'EUR-L2P-ATS_NR_2P' AND d.dataset_id = dc.dataset_id;

INSERT INTO dataset_meta_history (dataset_id, version_id, creation_date_long, last_revision_date_long, revision_history)
VALUES (dataset_id_seq.currval, 1, inventory.timestampToLong('24-NOV-2008'), inventory.timestampToLong('30-APR-2009'), '2009-04-30. S. Hardman: The majority of dataset policy was inherited from EUR-L2P-ATS_NR_2P.');

INSERT INTO dataset_policy (dataset_id, data_class, data_frequency, data_volume, data_duration,  access_type, base_path_append_type, data_format, compress_type, checksum_type, spatial_type, access_constraint, use_constraint)
SELECT dataset_id_seq.currval, dp.data_class, dp.data_frequency, dp.data_volume, dp.data_duration,  dp.access_type, dp.base_path_append_type, dp.data_format, dp.compress_type, dp.checksum_type, dp.spatial_type, dp.access_constraint, dp.use_constraint
FROM dataset d, dataset_policy dp
WHERE d.short_name = 'EUR-L2P-ATS_NR_2P' AND d.dataset_id = dp.dataset_id;

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'ARCHIVE-PUBLIC', 'file:///store/ghrsst/public/data/L2P/ATS_NR_2P/UPA');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-FTP', 'ftp://podaac.jpl.nasa.gov/pub/GHRSST/data/L2P/ATS_NR_2P/UPA');

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
VALUES (dataset_id_seq.currval, 'LOCAL-OPENDAP', 'http://dods.jpl.nasa.gov/opendap/GHRSST/data/L2P/ATS_NR_2P/UPA');

INSERT INTO dataset_parameter (dataset_id, category, topic, term, variable, variable_detail)
SELECT dataset_id_seq.currval, dp.category, dp.topic, dp.term, dp.variable, dp.variable_detail
FROM dataset d, dataset_parameter dp
WHERE d.short_name = 'EUR-L2P-ATS_NR_2P' AND d.dataset_id = dp.dataset_id;

INSERT INTO dataset_project (dataset_id, project_id)
SELECT dataset_id_seq.currval, dp.project_id
FROM dataset d, dataset_project dp
WHERE d.short_name = 'EUR-L2P-ATS_NR_2P' AND d.dataset_id = dp.dataset_id;

INSERT INTO dataset_resource (dataset_id, name, path, type, description)
SELECT dataset_id_seq.currval, dr.name, dr.path, dr.type, dr.description
FROM dataset d, dataset_resource dr
WHERE d.short_name = 'EUR-L2P-ATS_NR_2P' AND d.dataset_id = dr.dataset_id;

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT dataset_id_seq.currval, ds.source_id, ds.sensor_id
FROM dataset d, dataset_source ds
WHERE d.short_name = 'EUR-L2P-ATS_NR_2P' AND d.dataset_id = ds.dataset_id;

INSERT INTO dataset_version (dataset_id, version_id, version, version_date_long, description)
VALUES (dataset_id_seq.currval, 1, 'N/A', NULL, NULL);

INSERT INTO granule_element (dataset_id, element_id, obligation_flag)
SELECT dataset_id_seq.currval, ge.element_id, ge.obligation_flag
FROM dataset d, granule_element ge
WHERE d.short_name = 'EUR-L2P-ATS_NR_2P' AND d.dataset_id = ge.dataset_id;

INSERT INTO collection_dataset (collection_id, dataset_id, granule_flag)
SELECT c.collection_id, dataset_id_seq.currval, 'A'
FROM collection c
WHERE c.short_name = 'GHRSST';

COMMIT;
