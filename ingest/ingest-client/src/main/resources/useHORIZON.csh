#!/bin/csh -f
#
### ==================================================================== ###
#                                                                          #
#  The PO.DAAC Ingest Client Setup Script                                  #
#                                                                          #
#  Function:                                                               #
#  Simple c-shell script to add Ingest launchers to client's path.         #
#                                                                          #
#  Created:                                                                #
#  August 29, 2007 - T. Huang {Thomas.Huang@jpl.nasa.gov}                  #
#                                                                          #
#  Modifications:                                                          #
### ==================================================================== ###
#
# $Id: $
#

if (! $?JAVA_HOME) then
   setenv JAVA_HOME /usr/local/java
   setenv PATH ${JAVA_HOME}/bin:${PATH}
endif

setenv HORIZON ${cwd}/config

if (! $?HORIZON_NAMING) then
   setenv HORIZON_NAMING jnp://localhost:1099
endif

if (! $?HORIZON_LOGGING) then
   setenv HORIZON_LOGGING `pwd`
endif

setenv PATH ${HORIZON}/../bin:${PATH}
