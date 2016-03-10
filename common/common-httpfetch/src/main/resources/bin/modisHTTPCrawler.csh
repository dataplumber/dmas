#!/bin/csh -f


java -Dlog4j.configuration=$LOGGER  gov.nasa.podaac.common.httpfetch.oceandata.ModisDataFetcher $*

