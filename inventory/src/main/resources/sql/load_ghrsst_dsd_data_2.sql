/*
** Copyright (c) 2007-2008, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: load_ghrsst_dsd_data_2.sql 1193 2008-05-26 21:29:32Z shardman $
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
** Load it with all provider URLs, extract distinct entries into our 
** provider_resource table.
*/

DECLARE
  d_id       ghrsst_migration.dsd_data_center.dsd_id%TYPE;
  p_id       provider.provider_id%TYPE;

CURSOR DCursor IS
  SELECT p.provider_id, g.dsd_id
  FROM provider p, ghrsst_migration.DSD_DATA_CENTER g
  WHERE p.short_name=g.short_name;

BEGIN
  OPEN DCursor;
  LOOP

  FETCH DCursor INTO p_id, d_id;
  EXIT WHEN DCursor%NOTFOUND;

  INSERT INTO provider_resource_temp (p_id, url)
  SELECT p_id, data_center_url
  FROM ghrsst_migration.DSD_DATA_CENTER_URL
  WHERE dsd_id = d_id;

  END LOOP;
  CLOSE DCursor;

  INSERT INTO provider_resource (provider_id, path)
  SELECT DISTINCT p_id, url
  FROM provider_resource_temp;

END;
/
