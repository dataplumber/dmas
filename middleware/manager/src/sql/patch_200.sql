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
UPDATE ing_data_file set ingest_started = (ingest_started * 1000) WHERE ingest_started < 10000000000;

UPDATE ing_data_file set ingest_completed = (ingest_completed * 1000) WHERE ingest_completed < 10000000000;

UPDATE ing_engine set started_at = (started_at * 1000) WHERE started_at < 10000000000;

UPDATE ing_engine set stopped_at = (stopped_at * 1000) WHERE stopped_at < 10000000000;

UPDATE ing_engine set updated = (updated * 1000) WHERE updated < 10000000000;

UPDATE ing_engine set last_seen = (last_seen * 1000) WHERE last_seen < 10000000000;

UPDATE ing_engine_job set assigned = (assigned * 1000) WHERE assigned < 10000000000;

UPDATE ing_federation set updated = (updated * 1000) WHERE updated < 10000000000;

UPDATE ing_message_schema set released = (released * 1000) WHERE released < 10000000000;

UPDATE ing_product set created = (created * 1000) WHERE created < 10000000000;

UPDATE ing_product set updated = (updated * 1000) WHERE updated < 10000000000;

UPDATE ing_product set completed = (completed * 1000) WHERE completed < 10000000000;

UPDATE ing_product set archived_at = (archived_at * 1000) WHERE archived_at < 10000000000;

UPDATE ing_product_type set locked_at = (locked_at * 1000) WHERE locked_at < 10000000000;

UPDATE ing_product_type set updated = (updated * 1000) WHERE updated < 10000000000;

UPDATE ing_remote_system set created = (created * 1000) WHERE created < 10000000000;

UPDATE ing_remote_system set updated = (updated * 1000) WHERE updated < 10000000000;

UPDATE ing_storage set created = (created * 1000) WHERE created < 10000000000;

UPDATE ing_storage set last_used = (last_used * 1000) WHERE last_used < 10000000000;

UPDATE ing_system_user_session set issue_time = (issue_time * 1000) WHERE issue_time < 10000000000;

UPDATE ing_system_user_session set expire_time = (expire_time * 1000) WHERE expire_time < 10000000000;
