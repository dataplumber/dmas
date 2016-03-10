/*
** Copyright (c) 2008, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: load_ghrsst_fr_data_4.sql 4477 2010-01-20 18:00:11Z gangl $
*/

/*
** This script loads the GHRSST data from the fr_mmr database into 
** the Inventory schema. This script requires a schema named 
** "ghrsst_migration" to be located on the same Oracle database server 
** which contains the GHRSST dsd_mmr and fr_mmr schemas.
*/

/*
** dataset_coverage
**
** Update the stop_time for the datasets where we no longer receive data.
*/

DECLARE
  var_dataset_id  dataset.dataset_id%TYPE;

CURSOR Dcursor IS
  SELECT dataset_id
  FROM dataset
  WHERE short_name in ('EUR-L2P-AMSRE', 'EUR-L2P-AVHRR16_G', 'EUR-L2P-AVHRR16_L', 'EUR-L2P-AVHRR17_G', 'EUR-L2P-AVHRR17_L', 'EUR-L2P-NAR16_SST', 'EUR-L2P-TMI', 'EUR-L4UHFnd-MED-v01');

BEGIN
  OPEN DCursor;
  LOOP

  FETCH DCursor INTO var_dataset_id;
  EXIT WHEN DCursor%NOTFOUND;

  UPDATE dataset_coverage SET stop_time_long = 
    (SELECT MAX(g.stop_time_long) FROM granule g, dataset d
    WHERE g.dataset_id = var_dataset_id)
  WHERE dataset_id = var_dataset_id;

  END LOOP;
  CLOSE DCursor;
END;
/
