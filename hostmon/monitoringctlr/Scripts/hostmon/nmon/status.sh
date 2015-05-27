#!/bin/bash


#
OUTPUT=""
NMON_EXE=nmon
LOG_JSON=false
STATUS=FAILURE
WHOAMI=`whoami`
#tryto kill this users nmon processes, we assume only 1 test runs at a time
CMD="-u ${WHOAMI} ${NMON_EXE}"

parseArgs(){
	while [ $# -gt 0 ]	
	do
		case $1 in
			(-j)shift; export LOG_JSON=true; shift;;
			(*) break;;
		esac
	done
}

#parse the cmd-line arguments
parseArgs $@
cat /dev/null > .out
#mon instances
pgrep -l $NMON_EXE | while read OUTPUT
do
	echo "\"ps\":\"$OUTPUT\"">>.out	
done 
STATUS="success"	

NUM_OF_RUNNING_NMON=`wc -l .out | awk '{print $1}'`
PS_OUTPUT="\"-\""
if [ $NUM_OF_RUNNING_NMON -gt 0 ] 
then
	PS_OUTPUT="{`tr '\n' ',' < .out | sed -e 's/,$//g'`}"
fi

read -d '' JSON_MSG << EOF
	{
		"cmd" : "./status.sh",
	    "response" : {
			"num_of_running_nmon" :  $NUM_OF_RUNNING_NMON,
			"ps_output" :  $PS_OUTPUT
		},
		"status" : "$STATUS",
	    "timsestamp" : "$(date)"
	}
EOF

if [ $LOG_JSON == 'true' ]; then
	echo $JSON_MSG
fi
