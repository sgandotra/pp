#!/bin/sh


NMON_HOME=`dirname $0`
COMMON_HOME=${NMON_HOME}/../../common
PARSIBLE_HOME=${NMON_HOME}/../../parsible

#python home
PATH=/x/opt/pp/bin:${PATH}

#puts the logs into ${lnp-automation-home}/logs/nmon
LOGS_HOME=${NMON_HOME}/../../../logs/nmon

#do we send our metrics to graphite?
GRAPHITE=FALSE

#what is the target host for graphite
GRAPHITE_HOST=stage2lp48.qa.paypal.com

#build the output name
HOSTNAME=`hostname`
DATETIME=`date +"%Y%m%d_%H%M"`
UUID="${HOSTNAME}_${DATETIME}"
OUTPUT=${UUID}.nmon

if [ -f ${COMMON_HOME}/commonFunctions.sh ]; 
then
   . ${COMMON_HOME}/commonFunctions.sh
else
   echo "Error finding ${COMMON_HOME}/commonFunctions.sh, exiting..."
   exit 100
fi

## A script to launch the system monitoring utility NMON
##
## Usage: run.sh <-s samples> <-f frequency (s)> <-o path> [-g]
##
## Required Parameters
##   -s samples:    The total number of samples to take
##   -f frequency:  The frequency,in seconds, at which to sample
##   -o output:     The path to log results
## Options:
##   -h, --help :   Show this message
##   -g: Enable metric logging to our graphite instance
##
##


checkLckFile() {
	if [ -f ${NMON_HOME}/.nmon.lck ]; then
		logMsg "Lock exists there may be an existing nmon processing running, run shutdown first"
		exit 1
	fi
}

parseArgs() {

  if [ $# -lt 2 ];
  then
    usage
  fi

  while [ $# -gt 0 ]
  do
	case $1 in
		(-h|--help) usage;;
		(-s) shift; export SAMPLES=$1; shift;;
		(-f) shift; export FREQUENCY=$1; shift;;
		(-o) shift; export DATA_DIR=$1; shift;;
		(-t) shift; export GRAPHITE_HOST=$1; shift;;
        (-g) shift; export GRAPHITE=TRUE;;
		(-*) usage "unknown option: $1";;
		(*) break;;
	esac
  done
}

checkLckFile

# parse the cmd-line arguments
parseArgs $@

verifyExe $NMON_HOME/nmon

verifyParameter $SAMPLES "samples"
verifyParameter $FREQUENCY "frequency"
verifyParameter $DATA_DIR "results path"

#creates the directories if needed
LOG=${LOGS_HOME}/run.log

#create our logs & directories with a+rwx privs
createLogFile $LOG "777"
createDirectory $DATA_DIR "777"

#absolute path to the NMON results file
NMON_OUTPUT=${DATA_DIR}/${OUTPUT}

echo $NMON_HOME/nmon -f -s $SAMPLES -c $COUNT -F ${NMON_OUTPUT} | tee -a ${LOG}
$NMON_HOME/nmon -f -s $FREQUENCY -c $SAMPLES -F ${NMON_OUTPUT} -p > ${DATA_DIR}/nmon.pid&


if [ "${GRAPHITE}" = "TRUE" ];
then
    #the pid file we'll use when launching parible, there could be multiple instances running
    #PID_FILE=${DATA_DIR}/parsible.nmon.${UUID}.pid
    PID_FILE=${NMON_HOME}/../../../logs/nmon/parsible.pid

    #verify parsible is installed in the expected path
    verifyExe ${PARSIBLE_HOME}/parsible.py
    
    #let nmon have a change to create its first data file
    sleep 3

    logMsg "Enabling Parsible to process ${NMON_OUTPUT} and send metrics to Graphite...."
    logMsg "------------------------------------"
    logMsg "[exec] python ${PARSIBLE_HOME}/parsible.py --log-file ${NMON_OUTPUT} --parser parse_nmon --batch-reload true --pid-file ${PID_FILE}"    
    
    python ${PARSIBLE_HOME}/parsible.py --log-file ${NMON_OUTPUT} --parser parse_nmon --batch-reload true --pid-file ${PID_FILE} --graphite-host ${GRAPHITE_HOST} > ${DATA_DIR}/parsible.log&
fi    

#create a lock file
logMsg "Creating lock file to prevent duplicate processes"
touch ${NMON_HOME}/.nmon.lck
