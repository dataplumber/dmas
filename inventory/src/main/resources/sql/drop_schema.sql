/*
** Copyright (c) 2007-2008, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: drop_schema.sql 5742 2010-09-01 22:03:35Z gangl $
*/

/*
** This SQL script drops all database objects associated with the 
** Inventory schema.
*/

/*
** Drop Sequences
*/
DROP SEQUENCE metadata_manifest_id_seq;
DROP SEQUENCE dataset_element_id_seq;
DROP SEQUENCE collection_id_seq;
DROP SEQUENCE contact_id_seq;
DROP SEQUENCE dataset_id_seq;
DROP SEQUENCE element_id_collection_seq;
DROP SEQUENCE element_id_granule_seq;
DROP SEQUENCE granule_id_seq;
DROP SEQUENCE product_id_seq;
DROP SEQUENCE project_id_seq;
DROP SEQUENCE provider_id_seq;
DROP SEQUENCE sensor_id_seq;
DROP SEQUENCE source_id_seq;

/*
** Drop Tables
*/

/*
** Collection Tables
*/
DROP TABLE collection CASCADE CONSTRAINTS PURGE;
DROP TABLE collection_contact CASCADE CONSTRAINTS PURGE;
DROP TABLE collection_dataset CASCADE CONSTRAINTS PURGE;
DROP TABLE collection_element CASCADE CONSTRAINTS PURGE;
DROP TABLE collection_element_dd CASCADE CONSTRAINTS PURGE;
DROP TABLE collection_legacy_product CASCADE CONSTRAINTS PURGE;
DROP TABLE collection_product CASCADE CONSTRAINTS PURGE;

/*
** Dataset Tables
*/

DROP TABLE dataset CASCADE CONSTRAINTS PURGE;
DROP TABLE dataset_citation CASCADE CONSTRAINTS PURGE;
DROP TABLE dataset_contact CASCADE CONSTRAINTS PURGE;
DROP TABLE dataset_coverage CASCADE CONSTRAINTS PURGE;
DROP TABLE dataset_region CASCADE CONSTRAINTS PURGE;
DROP TABLE dataset_character CASCADE CONSTRAINTS PURGE;
DROP TABLE dataset_datetime CASCADE CONSTRAINTS PURGE;
DROP TABLE dataset_element CASCADE CONSTRAINTS PURGE;
DROP TABLE dataset_integer CASCADE CONSTRAINTS PURGE;
DROP TABLE dataset_real CASCADE CONSTRAINTS PURGE;

DROP TABLE dataset_location_policy CASCADE CONSTRAINTS PURGE;
DROP TABLE dataset_policy CASCADE CONSTRAINTS PURGE;
DROP TABLE dataset_meta_history CASCADE CONSTRAINTS PURGE;
DROP TABLE dataset_parameter CASCADE CONSTRAINTS PURGE;
DROP TABLE dataset_project CASCADE CONSTRAINTS PURGE;
DROP TABLE dataset_resource CASCADE CONSTRAINTS PURGE;
DROP TABLE dataset_source CASCADE CONSTRAINTS PURGE;
DROP TABLE dataset_software CASCADE CONSTRAINTS PURGE;
DROP TABLE dataset_spatial CASCADE CONSTRAINTS PURGE;
DROP TABLE dataset_version CASCADE CONSTRAINTS PURGE;
DROP TABLE project CASCADE CONSTRAINTS PURGE;
DROP TABLE sensor CASCADE CONSTRAINTS PURGE;
DROP TABLE source CASCADE CONSTRAINTS PURGE;


/*
** Granule Tables
*/
DROP TABLE granule CASCADE CONSTRAINTS PURGE;
DROP TABLE granule_archive CASCADE CONSTRAINTS PURGE;
DROP TABLE granule_character CASCADE CONSTRAINTS PURGE;
DROP TABLE granule_contact CASCADE CONSTRAINTS PURGE;
DROP TABLE granule_datetime CASCADE CONSTRAINTS PURGE;
DROP TABLE granule_element CASCADE CONSTRAINTS PURGE;
DROP TABLE granule_integer CASCADE CONSTRAINTS PURGE;
DROP TABLE granule_meta_history CASCADE CONSTRAINTS PURGE;
DROP TABLE granule_real CASCADE CONSTRAINTS PURGE;
DROP TABLE granule_reference CASCADE CONSTRAINTS PURGE;
DROP TABLE granule_sip CASCADE CONSTRAINTS PURGE;
DROP TABLE granule_spatial CASCADE CONSTRAINTS PURGE;

/*
** Provider Tables
*/
DROP TABLE contact CASCADE CONSTRAINTS PURGE;
DROP TABLE provider CASCADE CONSTRAINTS PURGE;
DROP TABLE provider_resource CASCADE CONSTRAINTS PURGE;

/*
**METADATA TABLE
*/
DROP TABLE metadata_manifest cascade constraints purge;

/*
** element table
*/
DROP TABLE element_dd CASCADE CONSTRAINTS PURGE;
