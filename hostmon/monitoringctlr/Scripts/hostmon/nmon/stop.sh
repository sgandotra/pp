#!/bin/sh

#
NMON_EXE=nmon
PARSIBLE_EXE=parsible.py
LOG_JSON=false
INITIAL_NMON_PROC_COUNT=-1
INITIAL_NMON_INSTANCES_COUNT=-1
POST_NMON_PROC_COUNT=-1
POST_NMON_INSTANCES_COUNT==-1
STATUS=FAILURE
WHOAMI=`whoami`
NMON_HOME=`pwd`
LOGS_HOME=${NMON_HOME}/../../../logs/nmon
PID_FILE=${NMON_HOME}/../../../logs/nmon/parsible.pid

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


#try to kill this user's instances of NMON
INITIAL_NMON_PROC_COUNT=`pgrep $CMD | wc -l`
pkill ${CMD}

#try to kill the nmon related processes
NMON_INSTANCES=`pgrep -f ${NMON_EXE}`
INITIAL_NMON_INSTANCES_COUNT=`pgrep -f ${NMON_EXE} | wc -l`
	


read -d '' JSON_MSG << EOF
	{
		"cmd" : "./stop.sh",
		"status" : "SUCCESS",
	    "timsestamp" : "$(date)"
	}
EOF

if [ $LOG_JSON == 'true' ]; then
	echo $JSON_MSG
fi

echo "removing lock file"
rm -f $NMON_HOME/.nmon.lck
echo "killing parsible"
cat ${PID_FILE} | xargs kill -9
echo "removing parsible pid file"
rm -f ${PID_FILE}
exit 0