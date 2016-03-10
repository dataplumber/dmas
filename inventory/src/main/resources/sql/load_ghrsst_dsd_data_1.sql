/*
** Copyright (c) 2007-2008, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: load_ghrsst_dsd_data_1.sql 2142 2008-10-23 00:34:09Z shardman $
*/

/*
** This script loads the GHRSST data from the dsd_mmr database into 
** the Inventory schema. This script requires a schema named 
** "ghrsst_migration" to be located on the same Oracle database server 
** which contains the GHRSST dsd_mmr and fr_mmr schemas.
*/

/*
** provider
**
** To avoid duplicates create a temp table, load it with the distinct
** providers, extract the entries into our provider table with assigned
** identifiers and then drop the temp table. Fix a typo in the metadata
** as well.
*/

CREATE TABLE provider_temp (
  short_name            VARCHAR2(31)    NOT NULL,
  long_name             VARCHAR2(160)   NOT NULL
);

INSERT INTO provider_temp (short_name, long_name) 
SELECT DISTINCT short_name, long_name
FROM ghrsst_migration.dsd_data_center;

INSERT INTO provider (provider_id, short_name, long_name, type) 
SELECT provider_id_seq.nextval, short_name, long_name, 'DATA-PROVIDER'
FROM provider_temp;

DROP TABLE provider_temp CASCADE CONSTRAINTS PURGE;

/*
** provider_resource
**
** To avoid duplicates create a temp table.
*/

CREATE TABLE provider_resource_temp (
  p_id           NUMBER          NOT NULL,
  url            VARCHAR2(255)   NOT NULL
);

COMMIT;
