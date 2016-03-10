/*
** Copyright (c) 2007-2008, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: load_schema.sql 6465 2010-12-07 23:19:41Z gangl $
*/

/*
** This script loads the policy for the Inventory schema.
*/

/*
** Provider
*/

INSERT INTO provider (provider_id, short_name, long_name, type)
VALUES (provider_id_seq.nextval, 'NASA/JPL/PODAAC', 'Physical Oceanography Distributed Active Archive Center, Jet Propulsion Laboratory, NASA', 'DATA-CENTER');

INSERT INTO provider_resource (provider_id, path)
VALUES (provider_id_seq.currval, 'http://podaac.jpl.nasa.gov/');

INSERT INTO contact (contact_id, role, first_name, middle_name, last_name, email, phone, fax, address, provider_id, notify_type)
VALUES (contact_id_seq.nextval, 'Technical Contact', 'User', NULL, 'Services', 'podaac@podaac.jpl.nasa.gov', NULL, NULL, NULL, provider_id_seq.currval, NULL);

INSERT INTO contact (contact_id, role, first_name, middle_name, last_name, email, phone, fax, address, provider_id, notify_type)
VALUES (contact_id_seq.nextval, 'Technical Contact', 'Gregg', NULL, 'Foti', 'Gregg.S.Foti@jpl.nasa.gov', '818-354-2860', '818-393-2718', '4800 Oak Grove Drive Pasadena, CA 91109-8099', provider_id_seq.currval, NULL);

INSERT INTO contact (contact_id, role, first_name, middle_name, last_name, email, phone, fax, address, provider_id, notify_type)
VALUES (contact_id_seq.nextval, 'Technical Contact', 'Jessica', NULL, 'Hausman', 'Jessica.K.Hausman@jpl.nasa.gov', '818-354-4588', '818-393-2718', '4800 Oak Grove Drive Pasadena, CA 91109-8099', provider_id_seq.currval, NULL);

INSERT INTO contact (contact_id, role, first_name, middle_name, last_name, email, phone, fax, address, provider_id, notify_type)
VALUES (contact_id_seq.nextval, 'Technical Contact', 'David', NULL, 'Moroni', 'David.F.Moroni@jpl.nasa.gov', '818-354-2038', '818-393-2718', '4800 Oak Grove Drive Pasadena, CA 91109-8099', provider_id_seq.currval, NULL);

INSERT INTO contact (contact_id, role, first_name, middle_name, last_name, email, phone, fax, address, provider_id, notify_type)
VALUES (contact_id_seq.nextval, 'Technical Contact', 'Christopher', 'J', 'Finch', 'Chris.Finch@jpl.nasa.gov', '818-354-2390', '818-393-2718', '4800 Oak Grove Drive Pasadena, CA 91109-8099', provider_id_seq.currval, NULL);

/*
** Dataset
*/

INSERT INTO project (project_id, short_name, long_name)
VALUES (project_id_seq.nextval, 'EOSDIS', 'Earth Observing System Data Information System');

/*
** Granule
*/

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'ancillaryName','Ancillary Name','character','Used to group files when more than one type of data are stored within a single dataset, such as with ancillary or auxiliary data.');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'batch','Batch','character','An identifier for an ingestion batch as specified by the Data Provider.');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'comment','Comment','character','A comment pertaining to the granule.');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'cycle','Cycle','integer','A designated interval of time, usually a repeat cycle where the spacecraft begins to repeat the same coverage path.');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'dayNightOrbitType','Day/Night Orbit Type','character','Day or Night orbit type for this granule.');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'dayNightType','Day/Night Type','character','Indicates whether the pass was in daytime or nighttime.');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'dayOfYearStart','Start Day-of-Year','integer','Day of the year for the start of this granule (normally used for daily or multi-day granules).');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'easternmostLongitude','Eastern Longitude','real','Eastern most longitude or the right-hand border of a 360 degree area, normally 360 of a 0 to 360 degree or +180 of a -180 to +180.');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'equatorCrossingLongitude','Equator Crossing Longitude','real','Longitude of the first equatorial crossing.');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'equatorCrossingTime','Equator Crossing Time','time','Time of the first equatorial crossing.');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'maxAltitude','Maximum Altitude','real','The altitude level which represents the upper limit of data coverage, as measured from mean sea level (primarily used for model outputs).');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'maxDepth','Maximum Depth','real','The depth level which represents the lower-most depth of data coverage, as measured from mean sea level (primarily for model outputs).');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'minAltitude','Minimum Altitude','real','The altitude level which represents the lower limit of data coverage, as measured from mean sea level (primarily used for model outputs).');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'minDepth','Minimum Depth','real','The depth level which represents the lower-most depth of data coverage, as measured from mean sea level (primarily for model outputs).');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'northernmostLatitude','Northern Latitude','real','Northern most latitude or the top border of a rectangular area.');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'pass','Pass','integer','Half-revolution count. Usually count repeats for each cycle number of other repeating interval.');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'passType','Pass Type','character','Indicates whether the pass was ascending or descending.');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'pointLatitude','Point Latitude','real','Latitude of the center point of a circle.');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'pointLongitude','Point Longitude','real','Longitude of the center point of a circle.');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'pointOrder','Point Order','character','Defines the ordering direction of a collection of points that make up a bounding polygon.');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'radius','Radius','real','Radius');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'revolution','Revolution','integer','Revolution count. Used when data files are divided into revs or smaller.');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'southernmostLatitude','Southern Latitude','real','Southern most latitude or the lower border of a rectangular area.');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'spatialGeometry','Spatial Geometry','spatial','The Oracle representation of the spatial geometry detailed in the corresponding lat/lon values.');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'version','Version','character','The version of the granule as specified by the Data Provider.');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'versionDate','Version Date','date','A date/time value associated with a granule version.');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'volumeName','Volume Name','character','The original volume name (e.g., tape) for this granule.');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'westernmostLongitude','Western Longitude','real','Western most longitude or the left-hand border of a 360 degree area, normally 0 of a 0 to 360 degree or -180 of a -180 to +180.');

/**
* New fields for dmas 2.0
*/
INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'startTime','start time','date','Start time of a granule or dataset');
INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'stopTime','stop time','date','Stop time of a granule or dataset');

INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'region','region','character','The geographic area descriptor for the data.');
INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'regionDetail','region detail','character','More specific names not specified in the GCMD valids list.');
INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'latitudeResolution','latitude resolution','character','The resolution for the North and South latitude values in km.');
INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'longitudeResolution','longitude resolution','character','The resolution for the East and West longitude values in km.');
INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'horizontalResolutionRange','horizontal resolution range','character','The range of resolutions for the latitude/longitude values.');
INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'altitudeResolution','altitude resolution','character','The resolution for altitude as measured from mean sea level in meters.');
INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'depthResolution','depth resolution','character','The resolution for depth as measured from mean sea level in meters.');
INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'temporalResolution','temporal resolution','character','The frequency that the data is sampled.');
INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'temporalResolutionRange','temporal resolution range','character','The range of frequencies within which the data is sampled.');
INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'ellipsoidType','ellipsoid type','character','The type of ellipsoid.');
INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'projectionType','projection type','character','The type of projection.');
INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'projectionDetail','projection detail','character','Free text description of details such as the projections standard parallels, etc., if necessary to fully specify the projection.');
INSERT INTO element_dd (element_id, short_name, long_name, type,  description) VALUES (element_id_granule_seq.nextval,'reference','reference','character','Key bibilographic references pertaining to the dataset.');

/*
*End new fields for DMAS 2.0
*/

/*
* DMAS 2.1
*/
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'anomalyType' ,'Anomaly Type' ,'character', 'Type of anomaly reported.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'eventType' ,'Event Type' ,'character', 'Type of Event, such as distribution, anomaly, Generation, etc.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'dataOriginationName' ,'Data Origination Name' ,'character', 'The project origination name for this granule.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'deliveryVersion' ,'Delivery Version' ,'character', 'A version identifier supplied by the data provider.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'GFZName' ,'GFZ Name' ,'character', 'The German GeoForschungsZentrum (GFZ) Potsdam file name for a Grace granule.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'imageGranularity' ,'Image Granularity' ,'character', 'Image granularity type for this granule.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'imageMask' ,'Image Mask' ,'character', 'Image mask type for this granule.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'imageType' ,'Image Type' ,'character', 'Image type for this granule.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'originalInventoryLocation' ,'Original Inventory Location' ,'character', 'Provides the original location of the granule if it has been moved.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'phase' ,'Phase' ,'character', 'The ERS-1 mission was comprised of seven phases. Each phase is a time period in which the mission parameters and characteristics remain unchanged.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'polarization' ,'Polarization' ,'character', 'Polarization for this granule.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'processType' ,'Process Type' ,'character', 'Process type for this granule.');

INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'dataConsumerId' , 'Data Consumer ID', 'integer', 'ID of the data consumer.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'dataProviderId' , 'Data Provider ID', 'integer', 'ID of the data provider.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'duration' , 'Duration', 'integer', 'The duration of granule measurement (in minutes).');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'eventId' , 'Event ID', 'integer', 'The ID of the event.');
INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'size' , 'Size', 'integer', 'Size of ganule (in KB).');

INSERT into element_dd(element_id, short_name, long_name, type, description) values (ELEMENT_ID_GRANULE_SEQ.nextval,'eventTime' , 'Event Time', 'datetime', 'The time of the event.');

