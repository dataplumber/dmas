#!/bin/sh

# Make the classpath:
CLASSPATH=`echo ${archive.home}/lib/*.jar | tr ' ' ':'`
export CLASSPATH

# Start Verifier
java -Darchive.config.file=${archive.home}/config/archive.config \
        gov.nasa.podaac.archive.tool.ArchiveTool $*
