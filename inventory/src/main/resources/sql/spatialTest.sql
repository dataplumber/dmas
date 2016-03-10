/*select granule_real.value, element_dd.short_name from element_dd,granule_real, dataset_element where granule_id=144500 and dataset_element.de_id=granule_real.de_id and dataset_element.element_id=element_dd.element_id;

*/
INSERT INTO granule_spatial VALUES(
  144500,
   SDO_GEOMETRY(
    2003,  -- two-dimensional polygon
    8307,
    NULL,
    SDO_ELEM_INFO_ARRAY(1,1003,1), -- one polygon (exterior polygon ring)
    SDO_ORDINATE_ARRAY(-111.925,-9.84, -87.175,-9.84, -87.175,11.02, -111.925,11.02, -111.925,-9.84)
  ), 
  13152
);



/*INSERT INTO user_sdo_geom_metadata
    (TABLE_NAME,
     COLUMN_NAME,
     DIMINFO,
     SRID)
  VALUES (
  'granule_spatial',
  'value',
  SDO_DIM_ARRAY(   -- 20X20 grid
    SDO_DIM_ELEMENT('X', -180, 180, 0.0000005),
    SDO_DIM_ELEMENT('Y', -90, 90, 0.0000005)
     ),
  8307   -- SRID
);*/
/*
UPDATE USER_SDO_GEOM_METADATA SET DIMINFO=SDO_DIM_ARRAY(SDO_DIM_ELEMENT('X', -180.000, 180.000, 0.00005),SDO_DIM_ELEMENT('Y', -90.000, 90.000, 0.00005)) WHERE TABLE_NAME='GRANULE_SPATIAL';
CREATE INDEX granule_spatial_idx
   ON granule_spatial(value)
   INDEXTYPE IS MDSYS.SPATIAL_INDEX;
-- Preceding statement created an R-tree index.
*/
/*
select * from granule_spatial where
   SDO_FILTER(
      granule_spatial.value,
      sdo_geometry(
         2003,
         8307,
         null,
         sdo_elem_info_array(1,1003,1),
         sdo_ordinate_array(-180,-40, -112,-10.0)
      )
   ) = 'TRUE'


