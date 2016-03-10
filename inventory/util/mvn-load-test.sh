#!/bin/sh

# Copyright 2008, by the California Institute of Technology.
# ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
#
# $Id: mvn-load-test.sh 1663 2008-08-22 21:21:15Z shardman $

# This script will execute the GranuleMetadataTest the number of times
# specified in the background. The database schema must be installed prior
# to executing this script, which can be accomplished by executing the 
# mvn-install.sh script. This script requires a local database setup
# in the user's Maven settings.xml config file.

cd ..

if [ ! "${1}" ]
then
  COUNT=1
else
  COUNT="${1}"
fi

for (( i=0; i<"${COUNT}"; i++ ))
do
  mvn test -Dtest=GranuleMetadataTest &
done

wait
