--
-- Copyright (c) 2010, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: $
--

--
-- This SQL script to fix the values in date related columns to be in a unit of
-- millisecond from second.
--
-- Following SQL command found in patch_142_200.sql contains bug:
-- UPDATE ing_data_file set ingest_started_long = to_number((cast(sys_extract_utc(ingest_started) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
--
-- It should be:
-- UPDATE ing_data_file set ingest_started_long = to_number((cast(sys_extract_utc(ingest_started) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60 * 1000);
--
--
UPDATE sys_event set first_received = (first_received * 1000) WHERE first_received < 10000000000;

UPDATE sys_event set last_received = (last_received * 1000) WHERE last_received < 10000000000;

UPDATE sys_event set resolved_at = (resolved_at * 1000) WHERE resolved_at < 10000000000;


UPDATE sys_notify set last_report = (last_report * 1000) WHERE last_report < 10000000000;

UPDATE sys_notify set last_notified = (last_notified * 1000) WHERE last_notified < 10000000000;


UPDATE sys_outgoing set created = (created * 1000) WHERE created < 10000000000;
