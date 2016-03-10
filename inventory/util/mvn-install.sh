#!/bin/sh

# Copyright 2007-2008, by the California Institute of Technology.
# ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
#
# $Id: mvn-install.sh 1929 2008-09-20 00:28:41Z clwong $

# This script cleans, compiles, loads the database schema and installs
# the Inventory software in the user's local Maven repository. This 
# script requires a local database setup in the user's Maven settings.xml
# config file.

cd ..
mvn clean compile test-compile
mvn sql:execute
mvn install -Dmaven.test.skip=true
