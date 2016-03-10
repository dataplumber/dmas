--
-- Copyright (c) 2009, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: $
--

--
-- This SQL script to update database to conform with v2.0.0.
--

ALTER TABLE sys_event ADD first_received_long NUMBER;
UPDATE sys_event set first_received_long = to_number((cast(sys_extract_utc(first_received) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE sys_event DROP COLUMN first_received;
ALTER TABLE sys_event RENAME COLUMN first_received_long TO first_received;
ALTER TABLE sys_event MODIFY first_received NUMBER NOT NULL;

ALTER TABLE sys_event ADD last_received_long NUMBER;
UPDATE sys_event set last_received_long = to_number((cast(sys_extract_utc(last_received) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE sys_event DROP COLUMN last_received;
ALTER TABLE sys_event RENAME COLUMN last_received_long TO last_received;
ALTER TABLE sys_event MODIFY last_received NUMBER NOT NULL;

ALTER TABLE sys_event ADD resolved_at_long NUMBER;
UPDATE sys_event set resolved_at_long = to_number((cast(sys_extract_utc(resolved_at) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE sys_event DROP COLUMN resolved_at;
ALTER TABLE sys_event RENAME COLUMN resolved_at_long TO resolved_at;


ALTER TABLE sys_notify ADD last_report_long NUMBER;
UPDATE sys_notify set last_report_long = to_number((cast(sys_extract_utc(last_report) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE sys_notify DROP COLUMN last_report;
ALTER TABLE sys_notify RENAME COLUMN last_report_long TO last_report;

ALTER TABLE sys_notify ADD last_notified_long NUMBER;
UPDATE sys_notify set last_notified_long = to_number((cast(sys_extract_utc(last_notified) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE sys_notify DROP COLUMN last_notified;
ALTER TABLE sys_notify RENAME COLUMN last_notified_long TO last_notified;


ALTER TABLE sys_outgoing ADD created_long NUMBER;
UPDATE sys_outgoing set created_long = to_number((cast(sys_extract_utc(created) as date) - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 60 * 60);
ALTER TABLE sys_outgoing DROP COLUMN created;
ALTER TABLE sys_outgoing RENAME COLUMN created_long TO created;
ALTER TABLE sys_outgoing MODIFY created NUMBER NOT NULL;
