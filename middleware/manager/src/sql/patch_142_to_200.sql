--
-- Copyright (c) 2009, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: $
--

--
-- This SQL script to update database to conform with v2.0.0.
--


ALTER TABLE ing_engine_job MODIFY operation VARCHAR2(9 CHAR);


ALTER TABLE ing_data_file ADD ingest_started_long NUMBER;
UPDATE ing_data_file set ingest_started_long = to_number((cast(sys_extract_utc(ingest_started) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE ing_data_file DROP COLUMN ingest_started;
ALTER TABLE ing_data_file RENAME COLUMN ingest_started_long TO ingest_started;

ALTER TABLE ing_data_file ADD ingest_completed_long NUMBER;
UPDATE ing_data_file set ingest_completed_long = to_number((cast(sys_extract_utc(ingest_completed) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE ing_data_file DROP COLUMN ingest_completed;
ALTER TABLE ing_data_file RENAME COLUMN ingest_completed_long TO ingest_completed;

ALTER TABLE ing_engine ADD started_at_long NUMBER;
UPDATE ing_engine set started_at_long = to_number((cast(sys_extract_utc(started_at) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE ing_engine DROP COLUMN started_at;
ALTER TABLE ing_engine RENAME COLUMN started_at_long TO started_at;

ALTER TABLE ing_engine ADD stopped_at_long NUMBER;
UPDATE ing_engine set stopped_at_long = to_number((cast(sys_extract_utc(stopped_at) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE ing_engine DROP COLUMN stopped_at;
ALTER TABLE ing_engine RENAME COLUMN stopped_at_long TO stopped_at;

ALTER TABLE ing_engine ADD updated_long NUMBER;
UPDATE ing_engine set updated_long = to_number((cast(sys_extract_utc(updated) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE ing_engine DROP COLUMN updated;
ALTER TABLE ing_engine RENAME COLUMN updated_long TO updated;

ALTER TABLE ing_engine ADD last_seen_long NUMBER;
UPDATE ing_engine set last_seen_long = to_number((cast(sys_extract_utc(last_seen) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE ing_engine DROP COLUMN last_seen;
ALTER TABLE ing_engine RENAME COLUMN last_seen_long TO last_seen;

ALTER TABLE ing_engine_job ADD assigned_long NUMBER;
UPDATE ing_engine_job set assigned_long = to_number((cast(sys_extract_utc(assigned) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE ing_engine_job DROP COLUMN assigned;
ALTER TABLE ing_engine_job RENAME COLUMN assigned_long TO assigned;
ALTER TABLE ing_engine_job MODIFY assigned NUMBER NOT NULL;

ALTER TABLE ing_federation ADD updated_long NUMBER;
UPDATE ing_federation set updated_long = to_number((cast(sys_extract_utc(updated) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE ing_federation DROP COLUMN updated;
ALTER TABLE ing_federation RENAME COLUMN updated_long TO updated;

ALTER TABLE ing_message_schema ADD released_long NUMBER;
UPDATE ing_message_schema set released_long = to_number((cast(sys_extract_utc(released) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE ing_message_schema DROP COLUMN released;
ALTER TABLE ing_message_schema RENAME COLUMN released_long TO released;
ALTER TABLE ing_message_schema MODIFY released NUMBER NOT NULL;

ALTER TABLE ing_product ADD created_long NUMBER;
UPDATE ing_product set created_long = to_number((cast(sys_extract_utc(created) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE ing_product DROP COLUMN created;
ALTER TABLE ing_product RENAME COLUMN created_long TO created;

ALTER TABLE ing_product ADD updated_long NUMBER;
UPDATE ing_product set updated_long = to_number((cast(sys_extract_utc(updated) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE ing_product DROP COLUMN updated;
ALTER TABLE ing_product RENAME COLUMN updated_long TO updated;

ALTER TABLE ing_product ADD completed_long NUMBER;
UPDATE ing_product set completed_long = to_number((cast(sys_extract_utc(completed) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE ing_product DROP COLUMN completed;
ALTER TABLE ing_product RENAME COLUMN completed_long TO completed;

ALTER TABLE ing_product ADD archived_at_long NUMBER;
UPDATE ing_product set archived_at_long = to_number((cast(sys_extract_utc(archived_at) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE ing_product DROP COLUMN archived_at;
ALTER TABLE ing_product RENAME COLUMN archived_at_long TO archived_at;

ALTER TABLE ing_product_type ADD locked_at_long NUMBER;
UPDATE ing_product_type set locked_at_long = to_number((cast(sys_extract_utc(locked_at) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE ing_product_type DROP COLUMN locked_at;
ALTER TABLE ing_product_type RENAME COLUMN locked_at_long TO locked_at;

ALTER TABLE ing_product_type ADD updated_long NUMBER;
UPDATE ing_product_type set updated_long = to_number((cast(sys_extract_utc(updated) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE ing_product_type DROP COLUMN updated;
ALTER TABLE ing_product_type RENAME COLUMN updated_long TO updated;
ALTER TABLE ing_product_type MODIFY updated NUMBER NOT NULL;

ALTER TABLE ing_remote_system ADD created_long NUMBER;
UPDATE ing_remote_system set created_long = to_number((cast(sys_extract_utc(created) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE ing_remote_system DROP COLUMN created;
ALTER TABLE ing_remote_system RENAME COLUMN created_long TO created;

ALTER TABLE ing_remote_system ADD updated_long NUMBER;
UPDATE ing_remote_system set updated_long = to_number((cast(sys_extract_utc(updated) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE ing_remote_system DROP COLUMN updated;
ALTER TABLE ing_remote_system RENAME COLUMN updated_long TO updated;

ALTER TABLE ing_storage ADD created_long NUMBER;
UPDATE ing_storage set created_long = to_number((cast(sys_extract_utc(created) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE ing_storage DROP COLUMN created;
ALTER TABLE ing_storage RENAME COLUMN created_long TO created;

ALTER TABLE ing_storage ADD last_used_long NUMBER;
UPDATE ing_storage set last_used_long = to_number((cast(sys_extract_utc(last_used) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE ing_storage DROP COLUMN last_used;
ALTER TABLE ing_storage RENAME COLUMN last_used_long TO last_used;

ALTER TABLE ing_system_user_session ADD issue_time_long NUMBER;
UPDATE ing_system_user_session set issue_time_long = to_number((cast(sys_extract_utc(issue_time) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE ing_system_user_session DROP COLUMN issue_time;
ALTER TABLE ing_system_user_session RENAME COLUMN issue_time_long TO issue_time;
ALTER TABLE ing_system_user_session MODIFY issue_time NUMBER NOT NULL;

ALTER TABLE ing_system_user_session ADD expire_time_long NUMBER;
UPDATE ing_system_user_session set expire_time_long = to_number((cast(sys_extract_utc(expire_time) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE ing_system_user_session DROP COLUMN expire_time;
ALTER TABLE ing_system_user_session RENAME COLUMN expire_time_long TO expire_time;
