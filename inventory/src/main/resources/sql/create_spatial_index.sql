/*
** Copyright (c) 2008, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: create_spatial_index.sql 2187 2008-10-30 16:57:19Z shardman $
*/

/*
** This SQL script creates the spatial indexes and SDO geometry
** metadata entries.
*/

INSERT INTO user_sdo_geom_metadata (table_name, column_name, diminfo, srid)
VALUES ('DATASET_SPATIAL', 'SPATIAL_GEOMETRY',
  SDO_DIM_ARRAY(
    SDO_DIM_ELEMENT('LONGITUDE', -180, 180, 1),
    SDO_DIM_ELEMENT('LATITUDE', -90, 90, 1)),
  8307);

CREATE INDEX dataset_spatial_sdo_idx
ON dataset_spatial(spatial_geometry)
INDEXTYPE IS MDSYS.SPATIAL_INDEX;

INSERT INTO user_sdo_geom_metadata (table_name, column_name, diminfo, srid)
VALUES ('GRANULE_SPATIAL', 'VALUE',
  SDO_DIM_ARRAY(
    SDO_DIM_ELEMENT('LONGITUDE', -180, 180, 1),
    SDO_DIM_ELEMENT('LATITUDE', -90, 90, 1)),
  8307);

CREATE INDEX granule_spatial_sdo_idx
ON granule_spatial(value)
INDEXTYPE IS MDSYS.SPATIAL_INDEX;

