#!/usr/local/bin/perl
#
# Copyright 2007-2008, California Institute of Technology.
# ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
#
# $Id$


# Get the input parameters and display them to the operator for confirmation.
# Note: The password parameter is not echoed.
chop ($pwd = `pwd`);
use lib `pwd`;
use DBI;


print "\@INC is @INC\n";

print "\n\nExecuting script OpsMergeToDev.pl.\n\n";

# Set up the date/time variables.
($sec, $min, $hour, $mday, $mon, $year, $wday, $yday, $isdst) =
    localtime(time);
$year = substr ($year, 1, 2);
$mon = $mon+1;
if (length ($mon) == 1) {$mon = 0 . $mon;}
if (length ($mday) == 1) {$mday = 0 . $mday;}
if (length ($hour) == 1) {$hour = 0 . $hour;}
if (length ($min) == 1) {$min = 0 . $min;}
if (length ($sec) == 1) {$sec = 0 . $sec;}
$dateString = $year . $mon . $mday;
$timeString = $hour . $min . $sec;



ACTION: {
   $action = &prompt ("Action [ baseline | dataset | collection | sequence ] {dataset}");

   if ($action eq "") {
      $action = "dataset";
   }
   
    if ($action eq "baseline") {
      $action = "baseline";
      $outputFile = "$pwd/inventory_baseline_$dateString$timeString.sql";
   }
  
   if ($action eq "baseline") {
      $action = "baseline";
      $outputFile = "$pwd/inventory_collection_$dateString$timeString.sql";
   }

 
   if ($action eq "dataset") {
      $action = "dataset";
      
      $datasetId = &prompt("Please enter the dataset ID to dump");
   
   }

   if (($action ne "baseline") && ($action ne "sequence")  && ($action ne "dataset") && ($action ne "collection")) {
      print "Not a valid entry, try again.\n\n";
      redo ACTION;
   }
}

$sid = 'DAACOPS'; #&prompt ("Oracle SID");


$username = 'ghrsst_web';#&prompt ("Username");

   #system 'stty', '-echo';
   $inputPassword = 'ghrsst$web';#&prompt ("Password");
   #system 'stty', 'echo';
   #print "\n";
   $password = 'ghrsst$web';#$inputPassword . "@" . $sid;


print "\nAre these the correct parameters?\n";
print "--------------------------------------------------------\n";
print "Action:           $action\n";
if ($action eq "dataset") {
   print "Dataset Id:       $datasetId\n";

}
print "Output file name:       $outputFile\n";
print "Oracle SID:       $sid\n";
print "Username:         $username\n";
print "--------------------------------------------------------\n\n";

# Prompt for acceptance of input parameters.
REPROMPT: {
   $accept = &prompt ("Accept Parameters [y|n] {y}");

   if ($accept eq "") {
      $accept = "y";
   }
   unless ($accept =~ /^y$|^n$/) {
      print "Unrecognized input; try again.\n\n";
      redo REPROMPT;
   }
}
if ($accept eq 'n') {
   print "Exiting because operator indicated that the input parameters ";
   print "are incorrect.\n\n";
   exit (1);
}



# Set up sql file and log file variables.
#$logFile = "$pwd/inventory_schema_$dateString$timeString.log";
#$sqlFile = "$pwd/inventory_schema_$dateString$timeString.sql";

#Do work here.

my $dbh = &getDBH();

if($action eq "sequence"){

 open (MYFILE, ">>sequences_$dateString$timeString.sql");


print MYFILE "DROP SEQUENCE metadata_manifest_id_seq;\n";
print MYFILE "DROP SEQUENCE dataset_element_id_seq;\n";
print MYFILE "DROP SEQUENCE collection_id_seq;\n";
print MYFILE "DROP SEQUENCE contact_id_seq;\n";
print MYFILE "DROP SEQUENCE dataset_id_seq;\n";
print MYFILE "DROP SEQUENCE element_id_collection_seq;\n";
print MYFILE "DROP SEQUENCE element_id_granule_seq;\n";
print MYFILE "DROP SEQUENCE granule_id_seq;\n";
print MYFILE "DROP SEQUENCE product_id_seq;\n";
print MYFILE "DROP SEQUENCE project_id_seq;\n";
print MYFILE "DROP SEQUENCE provider_id_seq;\n";
print MYFILE "DROP SEQUENCE sensor_id_seq;\n";
print MYFILE "DROP SEQUENCE source_id_seq;\n";

 my $count = 0;
 
$countSel = "select count(*) from metadata_manifest";
$count = sequenceCount($countSel) +1;
print MYFILE "CREATE SEQUENCE metadata_manifest_id_seq\n NOCACHE;";
print MYFILE "\n";
$countSel = "select max(de_id) from dataset_element";
$count = sequenceCount($countSel) + 1;
print MYFILE "CREATE SEQUENCE dataset_element_id_seq\n START WITH $count \n NOCACHE;";
print MYFILE "\n";
$countSel = "select max(collection_id) from collection";
$count = sequenceCount($countSel) +1;
print MYFILE "CREATE SEQUENCE collection_id_seq \n START WITH $count \n  NOCACHE;";
print MYFILE "\n";
$countSel = "select max(contact_id) from contact";
$count = sequenceCount($countSel) +1;
print MYFILE "CREATE SEQUENCE contact_id_seq\n START WITH $count \n   NOCACHE;";
print MYFILE "\n";
my $countSel = "select max(dataset_id) from dataset";
$count = sequenceCount($countSel) +1;
print MYFILE "CREATE SEQUENCE dataset_id_seq\n START WITH $count \n  NOCACHE;";
print MYFILE "\n";
$countSel = "select max(element_id) from collection_element";
$count = sequenceCount($countSel) +1;
print MYFILE "CREATE SEQUENCE element_id_collection_seq \n START WITH $count \n NOCACHE;";
print MYFILE "\n";
$countSel = "select max(element_id) from element_dd";
$count = sequenceCount($countSel) +1;
print MYFILE "CREATE SEQUENCE element_id_granule_seq \n START WITH $count \n NOCACHE;";
print MYFILE "\n";
$countSel = "select max(granule_id) from granule";
$count = sequenceCount($countSel) +1;
print MYFILE "CREATE SEQUENCE granule_id_seq \n START WITH $count \n  CACHE 100;";
print MYFILE "\n";
#$countSel = "select max(*) from product";
#$count = sequenceCount($countSel) +1;
print MYFILE "CREATE SEQUENCE product_id_seq \n START WITH 500 \n   NOCACHE;";
print MYFILE "\n";
$countSel = "select max(project_id) from project";
$count = sequenceCount($countSel) +1;
print MYFILE "CREATE SEQUENCE project_id_seq \n START WITH $count \n  NOCACHE;";
print MYFILE "\n";
$countSel = "select max(provider_id) from provider";
$count = sequenceCount($countSel) +1;
print MYFILE "CREATE SEQUENCE provider_id_seq \n START WITH $count \n  NOCACHE;";
print MYFILE "\n";
$countSel = "select max(sensor_id) from sensor";
$count = sequenceCount($countSel) +1;
print MYFILE "CREATE SEQUENCE sensor_id_seq \n START WITH $count \n  NOCACHE;";
print MYFILE "\n";
$countSel = "select max(source_id) from source";
$count = sequenceCount($countSel) +1;
print MYFILE "CREATE SEQUENCE source_id_seq \n START WITH $count \n  NOCACHE;";
print MYFILE "\n";






 
 close (MYFILE);

}

if($action eq "baseline"){

	open (MYFILE, ">>baseline_$dateString$timeString.sql");
	
	printDeletes();
	printAuxInfo();
	 
	my $SEL = "SELECT * FROM DATASET order by DATASET_ID";
        my @vals;	
	my $sth = $dbh->prepare($SEL);
	$sth->execute();
	 
	while ( my @row = $sth->fetchrow_array() ) {
	    push(@vals, $row[0]);
	}
	 
	END {
       	  $dbh->disconnect if defined($dbh);
	}
	
	foreach (@vals){
	   &printDatasetInfo($_);
	}
	close (MYFILE);

}

if($action eq "collection"){

        open (MYFILE, ">>collections_$dateString$timeString.sql");

        my $SEL = "SELECT * FROM COLLECTION order by COLLECTION_ID";
        my @vals;
        my $sth = $dbh->prepare($SEL);
        $sth->execute();

        while ( my @row = $sth->fetchrow_array() ) {
            push(@vals, $row[0]);
        }

        END {
          $dbh->disconnect if defined($dbh);
        }

        foreach (@vals){
           &printCollectionInfo($_);
        }
        close (MYFILE);

}

if($action eq "dataset"){
	open (MYFILE, '>>dataset.sql');
	&printDatasetInfo($datasetId);
	close (MYFILE);
}



exit (0);

sub getDBH{
	  my $dbh =  DBI->connect("DBI:Oracle:host=bashful.jpl.nasa.gov;sid=$sid",$username,$inputPassword)
                or die "Couldn't connect to database: " . DBI->errstr;
	$dbh->{LongReadLen} = 512 * 1024;
 	$dbh->{LongTruncOk} = 1;    ### We're happy to truncate any excess
	return $dbh;
}

sub printAuxInfo {

	my $dbh = &getDBH();
	
	$SEL = "SELECT PROJECT_ID,SHORT_NAME,LONG_NAME from PROJECT";
        $sth = $dbh->prepare($SEL);
        $sth->execute();

        while ( my @row = $sth->fetchrow_array() ) {
		$arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        $row[$i] =~ s/\'//g;
                }

                print MYFILE "INSERT INTO PROJECT (PROJECT_ID,SHORT_NAME, LONG_NAME) values ($row[0],'$row[1]','$row[2]');\n";
        }

	$SEL = "SELECT PROVIDER_ID,SHORT_NAME,LONG_NAME, TYPE from PROVIDER";
        $sth = $dbh->prepare($SEL);
        $sth->execute();

        while ( my @row = $sth->fetchrow_array() ) {
		$arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        $row[$i] =~ s/\'//g;
                }

                print MYFILE "INSERT INTO PROVIDER (PROVIDER_ID,SHORT_NAME, LONG_NAME, TYPE) values ($row[0],'$row[1]','$row[2]','$row[3]');\n";
        }

	$SEL = "SELECT PROVIDER_ID,PATH from PROVIDER_RESOURCE";
        $sth = $dbh->prepare($SEL);
        $sth->execute();

        while ( my @row = $sth->fetchrow_array() ) {

		$arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        $row[$i] =~ s/\'//g;
                }

                print MYFILE "INSERT INTO PROVIDER_RESOURCE (PROVIDER_ID,PATH) values ($row[0],'$row[1]');\n";
        }

	$SEL = "SELECT CONTACT_ID, ROLE,FIRST_NAME,MIDDLE_NAME,LAST_NAME,EMAIL,PHONE,FAX,ADDRESS,PROVIDER_ID,NOTIFY_TYPE from CONTACT";
        $sth = $dbh->prepare($SEL);
        $sth->execute();

        while ( my @row = $sth->fetchrow_array() ) {
		$arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        $row[$i] =~ s/\'//g;
                }

                $row[9] = 'null' if !defined($row[9]);
                print MYFILE "INSERT INTO CONTACT (CONTACT_ID, ROLE,FIRST_NAME,MIDDLE_NAME,LAST_NAME,EMAIL,PHONE,FAX,ADDRESS,PROVIDER_ID,NOTIFY_TYPE) values ($row[0],'$row[1]','$row[2]','$row[3]','$row[4]','$row[5]','$row[6]','$row[7]','$row[8]',$row[9], '$row[10]');\n";
        }

	$SEL = "SELECT SOURCE_ID, SHORT_NAME,LONG_NAME,TYPE,ORBIT_PERIOD,INCL_ANGLE,DESCRIPTION from SOURCE";
        $sth = $dbh->prepare($SEL);
        $sth->execute();

        while ( my @row = $sth->fetchrow_array() ) {
		$arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        $row[$i] =~ s/\'//g;
                }

                $row[4] = 'null' if !defined($row[4]);
                $row[5] = 'null' if !defined($row[5]);

                print MYFILE "INSERT INTO SOURCE (SOURCE_ID, SHORT_NAME,LONG_NAME,TYPE,ORBIT_PERIOD,INCL_ANGLE,DESCRIPTION) values ($row[0],'$row[1]','$row[2]','$row[3]',$row[4],$row[5],'$row[6]');\n";
        }
	
	$SEL = "SELECT SENSOR_ID, SHORT_NAME,LONG_NAME,SWATH_WIDTH,DESCRIPTION from SENSOR";
        $sth = $dbh->prepare($SEL);
        $sth->execute();

        while ( my @row = $sth->fetchrow_array() ) {
		$arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        $row[$i] =~ s/\'//g;
                }

		$row[3] = 'null' if !defined($row[3]);
                print MYFILE "INSERT INTO SENSOR (SENSOR_ID, SHORT_NAME,LONG_NAME,SWATH_WIDTH,DESCRIPTION) values ($row[0],'$row[1]','$row[2]',$row[3],'$row[4]');\n";
        }

	$SEL = "SELECT ELEMENT_ID, SHORT_NAME, LONG_NAME, TYPE, DESCRIPTION, SCOPE, MAX_LENGTH from ELEMENT_DD";
        $sth = $dbh->prepare($SEL);
        $sth->execute();

        while ( my @row = $sth->fetchrow_array() ) {
		$arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        $row[$i] =~ s/\'//g;
                }

		$row[5] = 'null' if !defined($row[5]);	
                $row[6] = 'null' if !defined($row[6]);
		print MYFILE "INSERT INTO ELEMENT_DD (ELEMENT_ID, SHORT_NAME, LONG_NAME, TYPE, DESCRIPTION, SCOPE, MAX_LENGTH) values ($row[0],'$row[1]','$row[2]','$row[3]','$row[4]',$row[5],$row[6]);\n";
        }


}

sub printDeletes {

	print MYFILE "delete from contact where 1=1;\n";
        print MYFILE "delete from provider_resource where 1=1;\n";
        print MYFILE "delete from element_dd where 1=1;\n";
	print MYFILE "delete from provider where 1=1;\n";
        print MYFILE "delete from project where 1=1;\n";

}

sub printCollectionInfo {
        my $c = $_[0];
        print "COLLECTION: $c \n";
        my $dbh = &getDBH();

        my $SEL = "SELECT COLLECTION_ID, SHORT_NAME,LONG_NAME, TYPE, replace( translate(DESCRIPTION, chr(9) || chr(13) || chr(10), 'X'), 'X', ' '), FULL_DESCRIPTION, AGGREGATE from COLLECTION where COLLECTION_ID=$c ";
        my $sth = $dbh->prepare($SEL);
        $sth->execute();

        while ( my @row = $sth->fetchrow_array() ) {
                $arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        if(length($row[$i]) > 1000){
                                #print "Trimming string...\n";
                          	my $pat = chr(10);
				$row[$i] =~  s/$pat/ /g;
 				$row[$i] = substr($row[$i], 0,1000);
                        }
                        $row[$i] =~ s/\'//g;
                }

           print MYFILE "INSERT INTO COLLECTION (COLLECTION_ID, SHORT_NAME,LONG_NAME, TYPE, DESCRIPTION, FULL_DESCRIPTION, AGGREGATE ) values ($row[0],'$row[1]','$row[2]','$row[3]','$row[4]', '$row[5]', '$row[6]');\n";
        }

	 $SEL = "SELECT COLLECTION_ID, CONTACT_ID from COLLECTION_CONTACT where COLLECTION_ID=$c ";
        $sth = $dbh->prepare($SEL);
        $sth->execute();
        while ( my @row = $sth->fetchrow_array() ) {                $arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        if(length($row[$i]) > 1000){
                                #print "Trimming string...\n";
                                $row[$i] = substr($row[$i], 0,1000);
                        }
                        $row[$i] =~ s/\'//g;
                }
           print MYFILE "INSERT INTO COLLECTION_CONTACT (COLLECTION_ID, CONTACT_ID) values ($row[0],$row[1]);\n";
        }

	$SEL = "SELECT COLLECTION_ID, DATASET_ID, GRANULE_FLAG, START_GRANULE_ID, STOP_GRANULE_ID, GRANULE_RANGE_360 from COLLECTION_DATASET where COLLECTION_ID=$c ";
        $sth = $dbh->prepare($SEL);
        $sth->execute();
        while ( my @row = $sth->fetchrow_array() ) {                
		$arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        if(length($row[$i]) > 1000){
                                #print "Trimming string...\n";
                                $row[$i] = substr($row[$i], 0,1000);
                        }
                        $row[$i] =~ s/\'//g;
                }

                $row[3] = 'null' if !defined($row[3]);
                $row[4] = 'null' if !defined($row[4]);
           print MYFILE "INSERT INTO COLLECTION_DATASET (COLLECTION_ID, DATASET_ID, GRANULE_FLAG, START_GRANULE_ID, STOP_GRANULE_ID, GRANULE_RANGE_360) values ($row[0],$row[1],'$row[2]',$row[3],$row[4], '$row[5]');\n";
        }

	$SEL = "SELECT COLLECTION_ID, LEGACY_PRODUCT_ID from COLLECTION_LEGACY_PRODUCT where COLLECTION_ID=$c ";
        $sth = $dbh->prepare($SEL);
        $sth->execute();
        while ( my @row = $sth->fetchrow_array() ) {                $arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        if(length($row[$i]) > 1000){
                                #print "Trimming string...\n";
                                $row[$i] = substr($row[$i], 0,1000);
                        }
                        $row[$i] =~ s/\'//g;
                }
           print MYFILE "INSERT INTO COLLECTION_LEGACY_PRODUCT (COLLECTION_ID, LEGACY_PRODUCT_ID) values ($row[0],$row[1]);\n";
        }


	$SEL = "SELECT COLLECTION_ID, VISIBLE_FLAG, ECHO_SUBMIT_DATE_LONG, GCMD_SUBMIT_DATE_LONG,PRODUCT_ID from COLLECTION_PRODUCT where COLLECTION_ID=$c ";
        $sth = $dbh->prepare($SEL);
        $sth->execute();

        while ( my @row = $sth->fetchrow_array() ) {
                $arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        if(length($row[$i]) > 1000){
                                #print "Trimming string...\n";
                                $row[$i] = substr($row[$i], 0,1000);
                        }
                        $row[$i] =~ s/\'//g;
                }

                $row[2] = 'null' if !defined($row[2]);
                $row[3] = 'null' if !defined($row[3]);
           print MYFILE "INSERT INTO COLLECTION_PRODUCT (COLLECTION_ID, VISIBLE_FLAG, ECHO_SUBMIT_DATE_LONG, GCMD_SUBMIT_DATE_LONG,PRODUCT_ID) values ($row[0],'$row[1]',$row[2],$row[3],'$row[4]');\n";
        }

}

sub sequenceCount {
 my $Select = $_[0];

 my @vals;
 my $sth = $dbh->prepare($Select);
        $sth->execute();


        while ( my @row = $sth->fetchrow_array() ) {
            push(@vals, $row[0]);
        }
        $count = $vals[0];
        #print "--Current Count: $count\n";
	return $count;
}


sub printDatasetInfo {
	my $d = $_[0];
 	print "Dataset: $d\n";	
	my $dbh = &getDBH();

	my $SEL = "SELECT DATASET_ID, PROVIDER_ID, SHORT_NAME, LONG_NAME, ORIGINAL_PROVIDER, PROVIDER_DATASET_NAME, PROCESSING_LEVEL, '','',LATITUDE_RESOLUTION,LONGITUDE_RESOLUTION,HORIZONTAL_RESOLUTION_RANGE, ALTITUDE_RESOLUTION, DEPTH_RESOLUTION, TEMPORAL_RESOLUTION, TEMPORAL_RESOLUTION_RANGE, ELLIPSOID_TYPE,PROJECTION_TYPE, PROJECTION_DETAIL, REFERENCE, DESCRIPTION,ACROSS_TRACK_RESOLUTION, ALONG_TRACK_RESOLUTION,ASCENDING_NODE_TIME, REMOTE_DATASET, PERSISTENT_ID FROM DATASET where DATASET_ID=$d";
 	#print "select: $SEL";	
	my $sth = $dbh->prepare($SEL);
        $sth->execute();

        while ( my @row = $sth->fetchrow_array() ) {
		$arraySize = @row;
		for($i=0; $i< $arraySize; $i++){
			if(length($row[$i]) > 1000){
				#print "Trimming string...\n";
				$row[$i] = substr($row[$i], 0,1000);
			}
			$row[$i] =~ s/\'//g;
			$row[$i] =~ tr/\015//d; 
			$row[$i] =~ s/\n/ /g;
		}		

		$row[11] = 'null' if !defined($row[11]);
		$row[12] = 'null' if !defined($row[12]);
		$row[10] = 'null' if !defined($row[10]);                
		$row[9] = 'null' if !defined($row[9]);        	
	print "Extracting datataset: $row[2]";  	
 
	  print MYFILE "INSERT INTO DATASET (DATASET_ID, PROVIDER_ID, SHORT_NAME, LONG_NAME, ORIGINAL_PROVIDER, PROVIDER_DATASET_NAME, PROCESSING_LEVEL, LATITUDE_RESOLUTION,LONGITUDE_RESOLUTION,HORIZONTAL_RESOLUTION_RANGE, ALTITUDE_RESOLUTION, DEPTH_RESOLUTION, TEMPORAL_RESOLUTION, TEMPORAL_RESOLUTION_RANGE, ELLIPSOID_TYPE,PROJECTION_TYPE, PROJECTION_DETAIL, REFERENCE, DESCRIPTION,ACROSS_TRACK_RESOLUTION, ALONG_TRACK_RESOLUTION,ASCENDING_NODE_TIME, REMOTE_DATASET, PERSISTENT_ID )values ($row[0],$row[1],'$row[2]','$row[3]','$row[4]','$row[5]','$row[6]',$row[9],$row[10],'$row[11]','$row[12]','$row[13]','$row[14]','$row[15]','$row[16]','$row[17]','$row[18]','$row[19]','$row[20]','$row[21]','$row[22]','$row[23]', '$row[24]', '$row[25]');\n"; 
        }

	$SEL = "SELECT DATASET_ID,TITLE,CREATOR,VERSION,PUBLISHER,SERIES_NAME,RELEASE_PLACE,CITATION_DETAIL,ONLINE_RESOURCE,RELEASE_DATE_LONG FROM DATASET_CITATION where DATASET_ID=$d";
        $sth = $dbh->prepare($SEL);
        $sth->execute();

        while ( my @row = $sth->fetchrow_array() ) {
		$arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        $row[$i] =~ s/\'//g;
                }
 
		$row[9] = 'null' if !defined($row[9]);
		 print MYFILE "INSERT INTO DATASET_CITATION (DATASET_ID,TITLE,CREATOR,VERSION,PUBLISHER,SERIES_NAME,RELEASE_PLACE,CITATION_DETAIL,ONLINE_RESOURCE,RELEASE_DATE_LONG) values ($row[0],'$row[1]','$row[2]','$row[3]','$row[4]','$row[5]','$row[6]','$row[7]','$row[8]',$row[9]);\n";
	}

	#dataset element

	$SEL = "SELECT DATASET_ID, ELEMENT_ID, OBLIGATION_FLAG, DE_ID, SCOPE FROM DATASET_ELEMENT where DATASET_ID=$d";
        $sth = $dbh->prepare($SEL);
        $sth->execute();

        while ( my @row = $sth->fetchrow_array() ) {
		$arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        $row[$i] =~ s/\'//g;
                }

                print MYFILE "INSERT INTO DATASET_ELEMENT (DATASET_ID, ELEMENT_ID, OBLIGATION_FLAG, DE_ID, SCOPE)values ($row[0],$row[1],'$row[2]',$row[3],'$row[4]');\n";
        }

	$SEL = "SELECT DATASET_ID,REGION,REGION_DETAIL FROM DATASET_REGION where DATASET_ID=$d";
        $sth = $dbh->prepare($SEL);
        $sth->execute();
                
        while ( my @row = $sth->fetchrow_array() ) {
                print MYFILE "INSERT INTO DATASET_REGION (DATASET_ID,REGION,REGION_DETAIL) values ($row[0],'$row[1]','$row[2]');\n";
                $arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        $row[$i] =~ s/\'//g;
                }

        }



	$SEL = "SELECT DATASET_ID,VALUE,DE_ID FROM DATASET_CHARACTER where DATASET_ID=$d";
        $sth = $dbh->prepare($SEL);
        $sth->execute();

        while ( my @row = $sth->fetchrow_array() ) {
                print MYFILE "INSERT INTO DATASET_CHARACTER (DATASET_ID,VALUE,DE_ID) values ($row[0],'$row[1]',$row[2]);\n";
        	$arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        $row[$i] =~ s/\'//g;
                }

	}

	$SEL = "SELECT DATASET_ID,VALUE_LONG,DE_ID FROM DATASET_DATETIME where DATASET_ID=$d";
        $sth = $dbh->prepare($SEL);
        $sth->execute();

        while ( my @row = $sth->fetchrow_array() ) {
		 $row[1] = 'null' if !defined($row[1]);
                print MYFILE "INSERT INTO DATASET_DATETIME (DATASET_ID,VALUE_LONG,DE_ID) values ($row[0],$row[1],$row[2]);\n";
        }

	$SEL = "SELECT DATASET_ID,VALUE, UNITS,DE_ID FROM DATASET_REAL where DATASET_ID=$d";
        $sth = $dbh->prepare($SEL);
        $sth->execute();

        while ( my @row = $sth->fetchrow_array() ) {
		$arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        $row[$i] =~ s/\'//g;
                }


        	 $row[1] = 'null' if !defined($row[1]);
	        print MYFILE "INSERT INTO DATASET_REAL (DATASET_ID,VALUE, UNITS,DE_ID) values ($row[0],$row[1],'$row[2]',$row[3]);\n";
        }

	$SEL = "SELECT DATASET_ID,VALUE, UNITS,DE_ID FROM DATASET_INTEGER where DATASET_ID=$d";
        $sth = $dbh->prepare($SEL);
        $sth->execute();
        
        while ( my @row = $sth->fetchrow_array() ) {
		$arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        $row[$i] =~ s/\'//g;
                }


                print MYFILE "INSERT INTO DATASET_INTEGER (DATASET_ID,VALUE, UNITS,DE_ID) values ($row[0],$row[1],'$row[2]',$row[3]);\n";
        }

	$SEL = "SELECT DATASET_ID,CONTACT_ID FROM DATASET_CONTACT where DATASET_ID=$d";
        $sth = $dbh->prepare($SEL);
        $sth->execute();
        
        while ( my @row = $sth->fetchrow_array() ) {

		$arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        $row[$i] =~ s/\'//g;
                }

                print MYFILE "INSERT INTO DATASET_CONTACT (DATASET_ID,CONTACT_ID) values ($row[0],$row[1]);\n";
        }

	$SEL = "SELECT DATASET_ID,PROJECT_ID FROM DATASET_PROJECT where DATASET_ID=$d";
        $sth = $dbh->prepare($SEL);
        $sth->execute(); 
 
        while ( my @row = $sth->fetchrow_array() ) {
		$arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        $row[$i] =~ s/\'//g;
                }

                print MYFILE "INSERT INTO DATASET_PROJECT (DATASET_ID,PROJECT_ID) values ($row[0],$row[1]);\n";
        }

	$SEL = "SELECT DATASET_ID,SOURCE_ID,SENSOR_ID FROM DATASET_SOURCE where DATASET_ID=$d";
        $sth = $dbh->prepare($SEL);
        $sth->execute();

        while ( my @row = $sth->fetchrow_array() ) {
		$arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        $row[$i] =~ s/\'//g;
                }

                print MYFILE "INSERT INTO DATASET_SOURCE (DATASET_ID,SOURCE_ID,SENSOR_ID) values ($row[0],$row[1], $row[2]);\n";
        }

	$SEL = "SELECT DATASET_ID,VERSION_ID,VERSION,DESCRIPTION, VERSION_DATE_LONG FROM DATASET_VERSION where DATASET_ID=$d";
        $sth = $dbh->prepare($SEL);
        $sth->execute();

        while ( my @row = $sth->fetchrow_array() ) {
		$arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        $row[$i] =~ s/\'//g;
                }
		 $row[4] = 'null' if !defined($row[4]);
                print MYFILE "INSERT INTO DATASET_VERSION (DATASET_ID,VERSION_ID,VERSION,DESCRIPTION, VERSION_DATE_LONG) values ($row[0],$row[1],'$row[2]','$row[3]',$row[4]);\n";
        }

	$SEL = "SELECT DATASET_ID,NAME,TYPE,PATH,VERSION,LANGUAGE,PLATFORM,DESCRIPTION,RELEASE_DATE_LONG FROM DATASET_SOFTWARE where DATASET_ID=$d";
        $sth = $dbh->prepare($SEL);
        $sth->execute();
        
        while ( my @row = $sth->fetchrow_array() ) {
                $arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        $row[$i] =~ s/\'//g;
                }

		 $row[8] = 'null' if !defined($row[8]);
		print MYFILE "INSERT INTO DATASET_SOFTWARE (DATASET_ID,NAME,TYPE,PATH,VERSION,LANGUAGE,PLATFORM,DESCRIPTION,RELEASE_DATE_LONG) values ($row[0],'$row[1]','$row[2]','$row[3]','$row[4]','$row[5]','$row[6]','$row[7]',$row[8]);\n";
        }

	$SEL = "SELECT DATASET_ID,NAME,PATH,TYPE,DESCRIPTION FROM DATASET_RESOURCE where DATASET_ID=$d";
        $sth = $dbh->prepare($SEL);
        $sth->execute();

        while ( my @row = $sth->fetchrow_array() ) {
		$arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        $row[$i] =~ s/\'//g;
                }

                print MYFILE "INSERT INTO DATASET_RESOURCE (DATASET_ID,NAME,PATH,TYPE,DESCRIPTION) values ($row[0],'$row[1]','$row[2]','$row[3]','$row[4]');\n";
        }

	$SEL = "SELECT DATASET_ID,DATA_CLASS,DATA_FREQUENCY,DATA_VOLUME,DATA_DURATION,ACCESS_TYPE,BASE_PATH_APPEND_TYPE, DATA_FORMAT, COMPRESS_TYPE, CHECKSUM_TYPE,SPATIAL_TYPE,ACCESS_CONSTRAINT,USE_CONSTRAINT, DATA_LATENCY, VIEW_ONLINE FROM DATASET_POLICY where DATASET_ID=$d";
        $sth = $dbh->prepare($SEL);
        $sth->execute();

        while ( my @row = $sth->fetchrow_array() ) {
		$arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        $row[$i] =~ s/\'//g;
                }


	       	 $row[2] = 'null' if !defined($row[2]);
		 $row[3] = 'null' if !defined($row[3]);
		 $row[4] = null if !defined($row[4]);
		 $row[13] = 'null' if !defined($row[13]);        
	
		print MYFILE "INSERT INTO DATASET_POLICY (DATASET_ID,DATA_CLASS,DATA_FREQUENCY,DATA_VOLUME,DATA_DURATION,ACCESS_TYPE,BASE_PATH_APPEND_TYPE, DATA_FORMAT, COMPRESS_TYPE, CHECKSUM_TYPE,SPATIAL_TYPE,ACCESS_CONSTRAINT, USE_CONSTRAINT, DATA_LATENCY, VIEW_ONLINE) values ($row[0],'$row[1]',$row[2],$row[3],$row[4],'$row[5]','$row[6]','$row[7]','$row[8]','$row[9]','$row[10]','$row[11]','$row[12]',$row[13], '$row[14]');\n";
        }

	$SEL = "SELECT DATASET_ID, NORTH_LAT,SOUTH_LAT,EAST_LON,WEST_LON,MIN_ALTITUDE,MAX_ALTITUDE,MIN_DEPTH,MAX_DEPTH,START_TIME_LONG,STOP_TIME_LONG FROM DATASET_COVERAGE where DATASET_ID=$d";
        $sth = $dbh->prepare($SEL);
        $sth->execute();

        while ( my @row = $sth->fetchrow_array() ) {                
		        	 
		for(my $i=1;$i<=13;$i++){
			$row[$i] = 'null' if !defined($row[$i]);
		}
	        print MYFILE "INSERT INTO DATASET_COVERAGE (DATASET_ID, NORTH_LAT,SOUTH_LAT,EAST_LON,WEST_LON,MIN_ALTITUDE,MAX_ALTITUDE,MIN_DEPTH,MAX_DEPTH,START_TIME_LONG,STOP_TIME_LONG ) values ($row[0],$row[1],$row[2],$row[3],$row[4],$row[5],$row[6],$row[7],$row[8],$row[9],$row[10]);\n";
        }

	$SEL = "SELECT DATASET_ID,CATEGORY,TOPIC,TERM,VARIABLE,VARIABLE_DETAIL FROM DATASET_PARAMETER where DATASET_ID=$d";
        $sth = $dbh->prepare($SEL);
        $sth->execute();

        while ( my @row = $sth->fetchrow_array() ) {
               $arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        $row[$i] =~ s/\'//g;
                }

		print MYFILE "INSERT INTO DATASET_PARAMETER (DATASET_ID,CATEGORY,TOPIC,TERM,VARIABLE,VARIABLE_DETAIL) values ($row[0],'$row[1]','$row[2]','$row[3]','$row[4]','$row[5]');\n";
        }

        $SEL = "SELECT DATASET_ID,VERSION_ID,REVISION_HISTORY,CREATION_DATE_LONG, LAST_REVISION_DATE_LONG FROM DATASET_META_HISTORY where DATASET_ID=$d";
        $sth = $dbh->prepare($SEL);
        $sth->execute();

        while ( my @row = $sth->fetchrow_array() ) {                
		$arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        $row[$i] =~ s/\'//g;
			$row[$i] =~ s///g;
                }

                $row[3] = 'null' if !defined($row[3]);
		 $row[4] = 'null' if !defined($row[4]);
		print MYFILE "INSERT INTO DATASET_META_HISTORY (DATASET_ID,VERSION_ID,REVISION_HISTORY,CREATION_DATE_LONG, LAST_REVISION_DATE_LONG) values ($row[0],$row[1],'$row[2]',$row[3],$row[4]);\n";
        }

	$SEL = "SELECT DATASET_ID,TYPE,BASE_PATH FROM DATASET_LOCATION_POLICY where DATASET_ID=$d";
        $sth = $dbh->prepare($SEL);
        $sth->execute();

        while ( my @row = $sth->fetchrow_array() ) {
		$arraySize = @row;
                for($i=0; $i< $arraySize; $i++){
                        $row[$i] =~ s/\'//g;
                }

               print MYFILE "INSERT INTO DATASET_LOCATION_POLICY (DATASET_ID,TYPE,BASE_PATH) values ($row[0],'$row[1]','$row[2]');\n";
        }
        
        $dbh->disconnect if defined($dbh);
}



# subroutine:  prompt - Prompt the user for input. Return the response.

sub prompt {
   PROMPT: {
      print "$_[0]: ";
      chop ($response = <STDIN>);
      print "\n";
   }
   return $response;
}

