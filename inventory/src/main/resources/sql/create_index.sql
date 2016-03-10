/*
** Copyright (c) 2009, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: $
*/

/*
** This SQL script creates indexes to the database objects for the Inventory schema.
*/


CREATE INDEX ce_ci_idx ON collection_element(collection_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

CREATE INDEX ce_ei_idx ON collection_element(element_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

CREATE INDEX dc_di_idx ON dataset_citation(dataset_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

CREATE INDEX dlp_di_idx ON dataset_location_policy(dataset_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

CREATE INDEX dp_di_idx ON dataset_parameter(dataset_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

CREATE INDEX dr_di_idx ON dataset_resource(dataset_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

CREATE INDEX ds_di_idx ON dataset_software(dataset_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

CREATE INDEX g_n_idx ON granule(name)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

CREATE INDEX gr_oname_ifx ON GRANULE(OFFICIAL_NAME)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

CREATE INDEX g_s_idx ON granule(status)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);
   
CREATE INDEX g_di_idx ON granule(dataset_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

CREATE INDEX ga_gi_idx on granule_archive(granule_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

CREATE INDEX gd_gi_idx ON granule_datetime(granule_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

CREATE INDEX gi_gi_idx ON granule_integer(granule_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);
   
CREATE INDEX gi_ei_idx ON granule_integer(de_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

CREATE INDEX greal_gi_idx ON granule_real(granule_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);
   
CREATE INDEX gr_ei_idx ON granule_real(de_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

CREATE INDEX gref_gi_idx ON granule_reference(granule_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

CREATE INDEX gs_gi_idx ON granule_spatial(granule_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);
   
CREATE INDEX gs_ei_idx ON granule_spatial(de_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);

CREATE INDEX pr_pi_idx ON provider_resource(provider_id)
TABLESPACE users
STORAGE (INITIAL 20K
   NEXT 20K
   PCTINCREASE 75);
