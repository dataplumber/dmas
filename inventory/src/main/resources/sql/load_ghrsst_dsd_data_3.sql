/*
** Copyright (c) 2007-2008, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: load_ghrsst_dsd_data_3.sql 2757 2009-04-07 20:09:29Z shardman $
*/

/*
** This script loads the GHRSST data from the dsd_mmr database into 
** the Inventory schema. This script requires a schema named 
** "ghrsst_migration" to be located on the same Oracle database server 
** which contains the GHRSST dsd_mmr and fr_mmr schemas.
*/

/*
** provider_resource
**
** Drop the temp table. Clean up some errant entries and add two more 
** for IFREMER/CERSAT since we will soon delete other duplicate providers.
*/

DROP TABLE provider_resource_temp CASCADE CONSTRAINTS PURGE;

DELETE FROM provider_resource
WHERE path = 'www.ress.com' OR path = 'www.remss.com';

INSERT INTO provider_resource (provider_id, path)
SELECT provider_id, 'http://www.ifremer.fr/cersat/en/index.html'
FROM provider
WHERE short_name = 'IFREMER/CERSAT';

INSERT INTO provider_resource (provider_id, path)
SELECT provider_id, 'http://www.ifremer.fr/anglais/'
FROM provider
WHERE short_name = 'IFREMER/CERSAT';

/*
** contact
**
** To avoid duplicates create a temp table, load it with dataset and
** granule contacts.
*/

CREATE TABLE contact_temp (
  role                  VARCHAR2(40)    NOT NULL,
  first_name            VARCHAR2(80)    NOT NULL,
  middle_name           VARCHAR2(80)            ,
  last_name             VARCHAR2(80)    NOT NULL,
  email                 VARCHAR2(255)   NOT NULL,
  phone                 VARCHAR2(80)            ,
  fax                   VARCHAR2(80)            ,
  address               VARCHAR2(512)           
);

INSERT INTO contact_temp (role, first_name, middle_name, last_name, email, phone, fax, address)
SELECT role, first_name, middle_name, last_name, email, phone, fax, TO_CHAR(address)
FROM ghrsst_migration.FR_PERSONNEL;

INSERT INTO contact_temp (role, first_name, middle_name, last_name, email, phone, fax, address)
SELECT role, first_name, middle_name, last_name, email, phone, fax, TO_CHAR(address)
FROM ghrsst_migration.DSD_PERSONNEL;

COMMIT;

