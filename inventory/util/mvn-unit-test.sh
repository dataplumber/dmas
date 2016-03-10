#!/bin/sh

# Copyright 2007-2008, by the California Institute of Technology.
# ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
#
# $Id: mvn-unit-test.sh 1929 2008-09-20 00:28:41Z clwong $

# This script cleans, compiles, loads the database schema and executes
# the unit tests. This script requires a local database setup in the 
# user's Maven settings.xml config file.

cd ..
mvn clean
mvn compile
mvn sql:execute
mvn test
