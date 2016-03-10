#!/bin/sh
PIDFILE=${archive.temp}/.aipsubscribe.pid
LOG_CONFIG=file://${archive.home}/config/archive.log.properties
JAVA_APP=gov.nasa.podaac.archive.tool.AipSubscribe
if [ "$1" = stop ]
then
  exec daemon.sh "${PIDFILE}" "${LOG_CONFIG}" "${JAVA_APP}" stop
else
  exec daemon.sh "${PIDFILE}" "${LOG_CONFIG}" "${JAVA_APP}" -server -Xms75m -Xmx1024m
fi
