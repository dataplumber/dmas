#!/bin/sh

# Make the classpath:
CLASSPATH=`echo /usr/local/daac/lib/*.jar | tr ' ' ':'`
export CLASSPATH
LOG_CONFIG=file://${archive.home}/config/archive.log.properties

# Start GHRSST Reconciliation
java -Xmx512m -Dlog4j.configuration="${LOG_CONFIG}" -Darchive.config.file=/usr/local/daac/config/archive.config \
        gov.nasa.podaac.archive.tool.GHRSSTReconciliationUtility $*

