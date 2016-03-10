#!/bin/sh
PIDFILE=${archive.temp}/.archivemonitor.pid
LOG_CONFIG=file://${archive.home}/config/archivemonitor.log.properties
JAVA_APP=gov.nasa.podaac.archive.tool.ArchiveMonitor
TOPIC="$1"
if [ "${TOPIC}" = stop ]
then
  exec daemon.sh "${PIDFILE}" "${LOG_CONFIG}" "${JAVA_APP}" stop
else
  exec daemon.sh "${PIDFILE}" "${LOG_CONFIG}" "${JAVA_APP}" "${TOPIC}" -server -Xms75m -Xmx1024m
fi
