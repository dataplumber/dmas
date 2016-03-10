/*
** Copyright (c) 2009, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: patch_130-to-130.sql 2765 2009-06-11 13:43:00 gangl $
*/

/*
** This script patches the 1.2.0 Inventory schema for the 1.3.0 release.
*/


/*
** Add REMOTE-FTP and REMOTE-ODAP paths to the dataset_location_policy table
*/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AMSRE/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AMSRE';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AMSRE/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AMSRE';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/ATS_NR_2P/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-ATS_NR_2P';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/ATS_NR_2P/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-ATS_NR_2P';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AVHRR16_G/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR16_G';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AVHRR16_G/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR16_G';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AVHRR16_L/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR16_L';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AVHRR16_L/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR16_L';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AVHRR17_G/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR17_G';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AVHRR17_G/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR17_G';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AVHRR17_L/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR17_L';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AVHRR17_L/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-AVHRR17_L';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/NAR16_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-NAR16_SST';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/NAR16_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-NAR16_SST';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/NAR17_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-NAR17_SST';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/NAR17_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-NAR17_SST';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/NAR18_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-NAR18_SST';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/NAR18_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-NAR18_SST';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/SEVIRI_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-SEVIRI_SST';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/SEVIRI_SST/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-SEVIRI_SST';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/TMI/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-TMI';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/TMI/EUR'
FROM dataset
WHERE short_name = 'EUR-L2P-TMI';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/MODIS_A/JPL'
FROM dataset
WHERE short_name = 'JPL-L2P-MODIS_A';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/MODIS_A/JPL'
FROM dataset
WHERE short_name = 'JPL-L2P-MODIS_A';

/**/


INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/MODIS_T/JPL'
FROM dataset
WHERE short_name = 'JPL-L2P-MODIS_T';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/MODIS_T/JPL'
FROM dataset
WHERE short_name = 'JPL-L2P-MODIS_T';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AVHRR17_G/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR17_G';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AVHRR17_G/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR17_G';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AVHRR17_L/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR17_L';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AVHRR17_L/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR17_L';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AVHRR18_G/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR18_G';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AVHRR18_G/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR18_G';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AVHRR18_L/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR18_L';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AVHRR18_L/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRR18_L';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AVHRRMTA_G/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRRMTA_G';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AVHRRMTA_G/NAVO'
FROM dataset
WHERE short_name = 'NAVO-L2P-AVHRRMTA_G';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AVHRR17_L/NEODAAS'
FROM dataset
WHERE short_name = 'NEODAAS-L2P-AVHRR17_L';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AVHRR17_L/NEODAAS'
FROM dataset
WHERE short_name = 'NEODAAS-L2P-AVHRR17_L';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AVHRR18_L/NEODAAS'
FROM dataset
WHERE short_name = 'NEODAAS-L2P-AVHRR18_L';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AVHRR18_L/NEODAAS'
FROM dataset
WHERE short_name = 'NEODAAS-L2P-AVHRR18_L';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/GOES11/OSDPD'
FROM dataset
WHERE short_name = 'OSDPD-L2P-GOES11';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/GOES11/OSDPD'
FROM dataset
WHERE short_name = 'OSDPD-L2P-GOES11';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/GOES12/OSDPD'
FROM dataset
WHERE short_name = 'OSDPD-L2P-GOES12';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/GOES12/OSDPD'
FROM dataset
WHERE short_name = 'OSDPD-L2P-GOES12';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/AMSRE/REMSS'
FROM dataset
WHERE short_name = 'REMSS-L2P-AMSRE';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/AMSRE/REMSS'
FROM dataset
WHERE short_name = 'REMSS-L2P-AMSRE';

/**/
INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/TMI/REMSS'
FROM dataset
WHERE short_name = 'REMSS-L2P-TMI';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/TMI/REMSS'
FROM dataset
WHERE short_name = 'REMSS-L2P-TMI';

/**/

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-FTP', 'ftp://data.nodc.noaa.gov/pub/data.nodc/ghrsst/L2P/ATS-NR_2P/UPA'
FROM dataset
WHERE short_name = 'UPA-L2P-ATS-NR_2P';

INSERT INTO dataset_location_policy (dataset_id, type, base_path)
SELECT dataset_id, 'REMOTE-OPENDAP', 'http://data.nodc.noaa.gov/cgi-bin/nph-dods/ghrsst/L2P/ATS-NR_2P/UPA'
FROM dataset
WHERE short_name = 'UPA-L2P-ATS-NR_2P';

/*
** 
** Changes for the new Directory Structure only JASON-1_GDR_*, JASON-1_IGDR*, JASON-1_OSDR, and QSCAT_LEVEL_3 are affected 
**
*/

/*Change DATASET_POLICY.ACCESS_TYPE from X to open*/
/*
** Note: This seemingly changes some Restricted items to 'open'
** based on new DIR structure. See JASON-1_GDR for example.
*/

UPDATE dataset_policy 
SET ACCESS_TYPE='OPEN'
WHERE DATASET_ID IN(
SELECT DATASET_ID from DATASET WHERE SHORT_NAME LIKE 'JASON-1_GDR_%'
OR SHORT_NAME LIKE 'JASON-1_IGDR%'
OR SHORT_NAME LIKE 'JASON-1_OSDR'
OR SHORT_NAME LIKE 'QSCAT_LEVEL_3'
);

/*Change DATASET_LOCATION_POLICY type to ARCHIVE-OPEN*/

UPDATE dataset_location_policy 
SET TYPE='ARCHIVE-OPEN' 
WHERE TYPE='ARCHIVE-PUBLIC'  
AND DATASET_ID IN(
SELECT DATASET_ID from DATASET WHERE SHORT_NAME LIKE 'JASON-1_GDR_%'
OR SHORT_NAME LIKE 'JASON-1_IGDR%'
OR SHORT_NAME LIKE 'JASON-1_OSDR'
OR SHORT_NAME LIKE 'QSCAT_LEVEL_3'
);

/*Change DATASET_LOCATION_POLICY type to ARCHIVE-CONTROLLED*/

UPDATE dataset_location_policy 
SET TYPE='ARCHIVE-CONTROLLED' 
WHERE TYPE='ARCHIVE-RESTRICTED'  
AND DATASET_ID IN(
SELECT DATASET_ID from DATASET WHERE SHORT_NAME LIKE 'JASON-1_GDR_%'
OR SHORT_NAME LIKE 'JASON-1_IGDR%'
OR SHORT_NAME LIKE 'JASON-1_OSDR'
OR SHORT_NAME LIKE 'QSCAT_LEVEL_3'
);


/*Change GRANULE.ACCESS_TYPE type to OPEN*/

UPDATE granule 
SET ACCESS_TYPE='OPEN' 
WHERE DATASET_ID IN(
SELECT DATASET_ID from DATASET WHERE SHORT_NAME LIKE 'JASON-1_GDR_%'
OR SHORT_NAME LIKE 'JASON-1_IGDR%'
OR SHORT_NAME LIKE 'JASON-1_OSDR'
OR SHORT_NAME LIKE 'QSCAT_LEVEL_3'
);

/*change DATASET_LOCATION_POLICY.BASE_PATH to mimic new dir structure
** First we'll do "public" -> "open"
** i.e. file:///store/quikscat/public/L3/data -> file:///store/quikscat/open/L3/data
*/

UPDATE dataset_location_policy 
SET BASE_PATH=REPLACE(base_path,'public','open') 
WHERE TYPE='ARCHIVE-OPEN'  
AND DATASET_ID IN(
SELECT DATASET_ID from DATASET WHERE SHORT_NAME LIKE 'JASON-1_GDR_%'
OR SHORT_NAME LIKE 'JASON-1_IGDR%'
OR SHORT_NAME LIKE 'JASON-1_OSDR'
OR SHORT_NAME LIKE 'QSCAT_LEVEL_3'
);

/*change DATASET_LOCATION_POLICY.BASE_PATH to mimic new dir structure
** First we'll do "restricted" -> "controlled"
** i.e. file:///store/quikscat/restricted/L3/data -> file:///store/quikscat/controlled/L3/data
*/

UPDATE dataset_location_policy 
SET BASE_PATH=REPLACE(base_path,'restricted','controlled') 
WHERE TYPE='ARCHIVE-CONTROLLED'  
AND DATASET_ID IN(
SELECT DATASET_ID from DATASET WHERE SHORT_NAME LIKE 'JASON-1_GDR_%'
OR SHORT_NAME LIKE 'JASON-1_IGDR%'
OR SHORT_NAME LIKE 'JASON-1_OSDR'
OR SHORT_NAME LIKE 'QSCAT_LEVEL_3'
);

/*
** GHRSST ACCESS_TYPE
*/

/*
** GHRSST DATASET_POLICY ACCESS_TYPE fix
*/
UPDATE dataset_policy 
SET ACCESS_TYPE='OPEN'
WHERE DATASET_ID IN(
select dataset_id from dataset 
where SHORT_NAME not like 'JASON%'
AND SHORT_NAME not like 'QSCAT%'
AND SHORT_NAME not like 'OCO%'
);

/*
** And GHRSST associated granules
*/

/*Change GRANULE.ACCESS_TYPE type to OPEN*/

UPDATE granule 
SET ACCESS_TYPE='OPEN' 
WHERE DATASET_ID IN(
select dataset_id from dataset 
where SHORT_NAME not like 'JASON%'
AND SHORT_NAME not like 'QSCAT%'
AND SHORT_NAME not like 'OCO%'
);


 

