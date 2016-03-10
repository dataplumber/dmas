/*
** Copyright (c) 2009, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: patch_120-to-130.sql 2765 2009-04-14 23:54:53Z shardman $
*/

/*
** This script patches the 1.2.0 Inventory schema for the 1.3.0 release. 
*/

/*
** Add Chris as a PO.DAAC contact.
*/

INSERT INTO contact (contact_id, role, first_name, middle_name, last_name, email, phone, fax, address, provider_id, notify_type)
VALUES (contact_id_seq.nextval, 'Technical Contact', 'Christopher', 'J', 'Finch', 'Chris.Finch@jpl.nasa.gov', '818-354-2390', '818-393-2718', '4800 Oak Grove Drive Pasadena, CA 91109-8099', (SELECT provider_id FROM provider WHERE short_name = 'NASA/JPL/PODAAC'), NULL);

/*
** Update the provider, source and sensor information for OCO.
*/

UPDATE contact SET role = 'User Services Contact', first_name = 'OCO Data Center', last_name = 'User Services', email = 'help@oco-dc.jpl.nasa.gov', address = 'MS 300-320, 4800 Oak Grove Dr, Pasadena, CA  91109-8099'
WHERE first_name = 'MOS' AND last_name = 'Operations';

UPDATE contact SET role = 'Investigator Contact'
WHERE first_name = 'David' AND last_name = 'Crisp';

INSERT INTO contact (contact_id, role, first_name, middle_name, last_name, email, phone, fax, address, provider_id, notify_type)
VALUES (contact_id_seq.nextval, 'Science Contact', 'Charles', 'E', 'Miller', 'Charles.E.Miller@jpl.nasa.gov', '818-393-6294', '818-354-5148', '4800 Oak Grove Drive Pasadena, CA 91109-8099', (SELECT provider_id FROM provider WHERE short_name = 'OCO MOS'), NULL);

UPDATE source SET orbit_period = 98.88, incl_angle = 98, description = 'The Orbiting Carbon Observatory (OCO) is a dedicated spacecraft that carries a single instrument comprised of three high-resolution grating spectrometers. The instrument, developed by Hamilton Sundstrand Sensor Systems, will acquire the most precise measurements of atmospheric CO2 ever made from space. The spacecraft, developed by Orbital Sciences Corporation, is based upon the LeoStar-2 architecture. The Observatory will be launched from the Vandenberg Air Force Base in California on a dedicated Taurus XL rocket in February 2009. The Observatory will fly in a near polar orbit that enables the instrument to observe most of the Earth''s surface at least once every sixteen days.'
WHERE short_name = 'OCO';

UPDATE sensor SET description = 'OCO uses three high-resolution grating spectrometers.  Each spectrometer measures light in one specific region of the spectrum. The spectrometers are designed to measure the absorption of reflected sunlight at near-infrared wavelengths by CO2 and molecular oxygen (O2) in the Earth''s atmosphere. The space-based instrument will acquire spatially and temporally coincident measurements of reflected sunlight in the CO2 bands centered at wavelengths near 1.61 mm and 2.06 mm and in the O2 A-band centered near 0.76 mm.'
WHERE short_name = 'OCO';

/*
** Update the dataset information for OCO.
*/

/* OCO_ACS */

UPDATE dataset SET long_name = 'OCO Attitude Control System (ACS)', processing_level = '0'
WHERE short_name = 'OCO_ACS';

/* OCO_Anc */

UPDATE dataset SET processing_level = '0'
WHERE short_name = 'OCO_Anc';

/* OCO_FTS_Anc */

UPDATE dataset SET long_name = 'OCO Fourier Transform Spectrometer (FTS) Ancillary', processing_level = '0'
WHERE short_name = 'OCO_FTS_Anc';

/* OCO_FTS_Igram */

UPDATE dataset SET long_name = 'OCO Fourier Transform Spectrometer (FTS) Interferogram', processing_level = '1'
WHERE short_name = 'OCO_FTS_Igram';

UPDATE dataset_policy SET data_format = 'RAW'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset WHERE short_name = 'OCO_FTS_Igram');

/* OCO_FTS_Spectra */

UPDATE dataset SET long_name = 'OCO Fourier Transform Spectrometer (FTS) Spectral', processing_level = '2'
WHERE short_name = 'OCO_FTS_Spectra';

UPDATE dataset_policy SET data_format = 'RAW'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset WHERE short_name = 'OCO_FTS_Spectra');

/* OCO_L1A */

UPDATE dataset SET latitude_resolution = '2.25 km', longitude_resolution = '1.29 km', temporal_resolution = '0.333 seconds'
WHERE short_name = 'OCO_L1A';

UPDATE dataset_location_policy SET base_path = 'file:///store/oco/restricted/L1A/data'
WHERE type = 'ARCHIVE-RESTRICTED' AND dataset_id = 
  (SELECT dataset_id FROM dataset WHERE short_name = 'OCO_L1A');

/* OCO_L1B */

UPDATE dataset SET latitude_resolution = '2.25 km', longitude_resolution = '1.29 km', temporal_resolution = '0.333 seconds'
WHERE short_name = 'OCO_L1B';

UPDATE dataset_location_policy SET base_path = 'file:///store/oco/restricted/L1B/data'
WHERE type = 'ARCHIVE-RESTRICTED' AND dataset_id = 
  (SELECT dataset_id FROM dataset WHERE short_name = 'OCO_L1B');

UPDATE dataset_location_policy SET base_path = 'file:///store/oco/public/L1B/data'
WHERE type = 'ARCHIVE-PUBLIC' AND dataset_id = 
  (SELECT dataset_id FROM dataset WHERE short_name = 'OCO_L1B');

UPDATE dataset_location_policy SET base_path = 'ftp://podaac.jpl.nasa.gov/pub/atmosphere/oco/L1B/data'
WHERE type = 'LOCAL-FTP' AND dataset_id = 
  (SELECT dataset_id FROM dataset WHERE short_name = 'OCO_L1B');

/* OCO_L2 */

UPDATE dataset SET long_name = 'OCO Level 2 Apparent Optical Path Difference (AOPD)', provider_dataset_name = 'OCO_L2_AOPD', latitude_resolution = '2.25 km', longitude_resolution = '1.29 km', temporal_resolution = '0.333 seconds'
WHERE short_name = 'OCO_L2';

UPDATE dataset_location_policy SET base_path = 'file:///store/oco/restricted/L2_AOPD/data'
WHERE type = 'ARCHIVE-RESTRICTED' AND dataset_id = 
  (SELECT dataset_id FROM dataset WHERE short_name = 'OCO_L2');

UPDATE dataset_location_policy SET base_path = 'file:///store/oco/public/L2_AOPD/data'
WHERE type = 'ARCHIVE-PUBLIC' AND dataset_id = 
  (SELECT dataset_id FROM dataset WHERE short_name = 'OCO_L2');

UPDATE dataset_location_policy SET base_path = 'ftp://podaac.jpl.nasa.gov/pub/atmosphere/oco/L2_AOPD/data'
WHERE type = 'LOCAL-FTP' AND dataset_id = 
  (SELECT dataset_id FROM dataset WHERE short_name = 'OCO_L2');

/* OCO_L2_FullPhysics */

UPDATE dataset SET latitude_resolution = '2.25 km', longitude_resolution = '1.29 km', temporal_resolution = '0.333 seconds'
WHERE short_name = 'OCO_L2_FullPhysics';

UPDATE dataset_policy SET data_class = 'ROLLING-STORE', data_duration = 180
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset WHERE short_name = 'OCO_L2_FullPhysics');

UPDATE dataset_location_policy SET base_path = 'file:///store/oco/restricted/L2_FULL/data'
WHERE type = 'ARCHIVE-RESTRICTED' AND dataset_id = 
  (SELECT dataset_id FROM dataset WHERE short_name = 'OCO_L2_FullPhysics');

/* OCO_MOC */

UPDATE dataset_policy SET data_class = 'ARCHIVE-ONLY', access_type = 'PRIVATE', spatial_type = 'NONE'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset WHERE short_name = 'OCO_MOC');

UPDATE dataset_location_policy SET type = 'ARCHIVE-PRIVATE', base_path = 'file:///store/oco/private/moc/data'
WHERE type = 'ARCHIVE-RESTRICTED' AND dataset_id = 
  (SELECT dataset_id FROM dataset WHERE short_name = 'OCO_MOC');

/* OCO_RICA */

UPDATE dataset SET long_name = 'OCO Level 1A Residual Image Correction Algorithm (RICA)', processing_level = '1A', latitude_resolution = '2.25 km', longitude_resolution = '1.29 km', temporal_resolution = '0.333 seconds'
WHERE short_name = 'OCO_RICA';

UPDATE dataset_location_policy SET base_path = 'file:///store/oco/restricted/RICA/data'
WHERE type = 'ARCHIVE-RESTRICTED' AND dataset_id = 
  (SELECT dataset_id FROM dataset WHERE short_name = 'OCO_RICA');

/* OCO_Telem */

UPDATE dataset SET long_name = 'OCO Science and Housekeeping Telemetry', processing_level = '0', latitude_resolution = '2.25 km', longitude_resolution = '1.29 km', temporal_resolution = '0.333 seconds'
WHERE short_name = 'OCO_Telem';

UPDATE dataset_policy SET data_format = 'RAW'
WHERE dataset_id = 
  (SELECT dataset_id FROM dataset WHERE short_name = 'OCO_Telem');

/*
** Add archive_time column to the granule table and populate
** the column the ingest_time value.
*/

ALTER TABLE granule ADD (archive_time TIMESTAMP);

UPDATE granule SET archive_time = ingest_time;

/*
** Create the granule_sip table and define the foreign key.
*/

CREATE TABLE granule_sip
(
  granule_id            NUMBER          NOT NULL,
  sip                   CLOB            NOT NULL,
  CONSTRAINT granule_sip_pk PRIMARY KEY (granule_id)
);

ALTER TABLE granule_sip
  ADD CONSTRAINT granule_sip_fk1
  FOREIGN KEY (granule_id)
  REFERENCES granule;

/*
** Add the Multiple Source/Sensor combination and associate it with
** the EUR-L4UHFnd-MED-v01 dataset.
*/

INSERT INTO source (source_id, short_name, long_name, type, orbit_period, incl_angle, description)
VALUES (source_id_seq.nextval, 'Multiple', 'Multiple Sources', 'SPACECRAFT', NULL, NULL, NULL);

INSERT INTO sensor (sensor_id, source_id, short_name, long_name, swath_width, description)
VALUES (sensor_id_seq.nextval, source_id_seq.currval, 'Multiple', 'Multiple Sensors', NULL, NULL);

INSERT INTO dataset_source (dataset_id, source_id, sensor_id)
SELECT d.dataset_id, so.source_id, se.sensor_id
FROM dataset d, source so, sensor se
WHERE d.short_name = 'EUR-L4UHFnd-MED-v01' AND so.short_name = 'Multiple' AND se.short_name = 'Multiple' AND so.source_id = se.source_id;

/*
** Update the data class policy for a couple of GHRSST datasets.
*/

UPDATE dataset_policy SET data_class = 'ARCHIVE-DIST'
WHERE dataset_id in
  (SELECT dataset_id FROM dataset
   WHERE short_name = 'REMSS-L2P_GRIDDED_25-AMSRE'
   OR    short_name = 'REMSS-L2P_GRIDDED_25-TMI'
   OR    short_name = 'REMSS-L4HRfnd-GLOB-mw_ir_rt_OI');

/*
** Update the compression type for certain datasets.
*/

UPDATE dataset_policy SET compress_type = 'BZIP2'
WHERE dataset_id in
  (SELECT dataset_id FROM dataset
   WHERE short_name = 'ABOM-L4HRfnd-AUS-RAMSSA_09km'
   OR    short_name = 'ABOM-L4LRfnd-GLOB-GAMSSA_28km');


COMMIT;
