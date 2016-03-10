#!/bin/csh
echo "Processing $2 with command $1";

##set type2 = `head -1 $2`;
set dat = `date '+%Y' | tr -d "\n"`;
set yat = `date '+%m' | tr -d "\n"`;
if(! -e $2) then
	echo "Data file $2 does not exist. Exiting.";
else if("$1" =~ "user" ) then
	awk 'NR==1{next}{FS=";"}{print $1,"YYYY DDDD",$5}' $2 | sed s/YYYY/$dat/ | sed s/DDDD/$yat/
else if("$1" =~ "size") then
        awk 'NR==1{next}{FS=";"}{print $1,"YYYY DDDD",$4}' $2 | sed s/YYYY/$dat/ | sed s/DDDD/$yat/
#else if("$1" =~ *"TOTAL"*) then
#        awk 'NR==1{next}{FS=";"}{print $1,"YYYY DDDD",$4}' $2 | sed s/YYYY/$dat/ | sed s/DDDD/$yat/
#else if("$1" =~ *"WEEKLY"*) then
#        awk 'NR==1{next}{FS=";"}{print $1,"YYYY DDDD",$4}' $2 | sed s/YYYY/$dat/ | sed s/DDDD/$yat/
#else if("$1" =~ "user") then
#	awk 'NR==1{next}{FS="\",\""}{print $6,"YYYY DDDD",$15}' $2 | sed s/\"//g
else
	echo "Specified command not found [$1], accepted commands are [user, size]"
endif

