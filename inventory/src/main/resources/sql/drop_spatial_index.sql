/*
** Copyright (c) 2008, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: drop_spatial_index.sql 2187 2008-10-30 16:57:19Z shardman $
*/

/*
** This SQL script drops the spatial indexes and SDO geometry
** metadata entries.
*/

DROP INDEX dataset_spatial_sdo_idx;

DELETE FROM user_sdo_geom_metadata
WHERE table_name = 'DATASET_SPATIAL';

DROP INDEX granule_spatial_sdo_idx;

DELETE FROM user_sdo_geom_metadata
WHERE table_name = 'GRANULE_SPATIAL';

