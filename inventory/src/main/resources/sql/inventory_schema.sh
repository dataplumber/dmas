#!/bin/sh
#
# Copyright (c) 2008, by the California Institute of Technology.
# ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
#
# $Id: inventory_schema.sh 1193 2008-05-26 21:29:32Z shardman $

#
# This script allows the user to create, drop and load the Inventory 
# database schema by executing the ant script, inventory_schema.xml.
#

if [ $# = "0" ]; then
  ant -f inventory_schema.xml
  exit 1
fi

ant -f inventory_schema.xml $*
