/*
** Copyright (c) 2008, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: load_spatial_data.sql 4203 2009-11-10 00:01:17Z gangl $
*/

/*
** This script loads the the spatial objects for both dataset and granule
** spatial data. 
*/

/*
** dataset_coverage
*/

DECLARE
  d_id  NUMBER;
  n_lat NUMBER;
  s_lat NUMBER;
  e_lon NUMBER;
  w_lon NUMBER;

CURSOR dCursor IS
  SELECT dataset_id, north_lat, south_lat, east_lon, west_lon
  FROM dataset_coverage
  WHERE north_lat IS NOT NULL AND south_lat IS NOT NULL AND east_lon IS NOT NULL AND west_lon IS NOT NULL;

BEGIN
  OPEN dCursor;
  LOOP

  FETCH dCursor INTO d_id, n_lat, s_lat, e_lon, w_lon;
  EXIT WHEN dCursor%NOTFOUND;

  INSERT INTO dataset_spatial (dataset_id, spatial_geometry)
  VALUES (d_id, 
    SDO_GEOMETRY(2003, 8307, NULL, 
      SDO_ELEM_INFO_ARRAY(1, 1003, 1), 
      SDO_ORDINATE_ARRAY(w_lon,s_lat, e_lon,s_lat, e_lon,n_lat, w_lon,n_lat, w_lon,s_lat)));

  END LOOP;
  CLOSE dCursor;
END;
/

/*
** granule_spatial
*/

DECLARE
  g_id  NUMBER;
  e_id  NUMBER;
  n_lat NUMBER;
  s_lat NUMBER;
  e_lon NUMBER;
  w_lon NUMBER;

CURSOR sCursor IS
  SELECT granule_id
  FROM granule g, dataset_policy dp
  WHERE g.dataset_id = dp.dataset_id AND dp.spatial_type = 'ORACLE';

BEGIN
  OPEN sCursor;
  LOOP

  FETCH sCursor INTO g_id;
  EXIT WHEN sCursor%NOTFOUND;

  SELECT element_id into e_id FROM element_dd WHERE short_name = 'spatialGeometry';

  SELECT value into n_lat FROM granule_real WHERE granule_id = g_id AND element_id = (SELECT element_id FROM element_dd WHERE short_name = 'northLatitude');

  SELECT value into s_lat FROM granule_real WHERE granule_id = g_id AND element_id = (SELECT element_id FROM element_dd WHERE short_name = 'southLatitude');

  SELECT value into e_lon FROM granule_real WHERE granule_id = g_id AND element_id = (SELECT element_id FROM element_dd WHERE short_name = 'eastLongitude');

  SELECT value into w_lon FROM granule_real WHERE granule_id = g_id AND element_id = (SELECT element_id FROM element_dd WHERE short_name = 'westLongitude');

  INSERT INTO granule_spatial (granule_id, element_id, value)
  VALUES (g_id, e_id, 
    SDO_GEOMETRY(2003, 8307, NULL, 
      SDO_ELEM_INFO_ARRAY(1, 1003, 1), 
      SDO_ORDINATE_ARRAY(w_lon,s_lat, e_lon,s_lat, e_lon,n_lat, w_lon,n_lat, w_lon,s_lat)));

  END LOOP;
  CLOSE sCursor;
END;
/

