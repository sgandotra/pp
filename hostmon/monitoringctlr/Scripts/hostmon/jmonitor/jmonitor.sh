#!/bin/sh

# # # # # # # # # # # # # # #
# RETURN CODES
#  100 - can't find common functions
#  200 - don't run as root
#  300 - can't start jmonitor, template file not defined
#  400 - can't start, jmonitor is already running
#
# # # # # # # # # # # # # # #

# # # # # # # # # # # # # # #
# This shell script starts the jmonitor process on the localhost using an existing
# template config file and substituting the @STAGE variable with localhost.
#
# JMonitor results can be output to file and/or the Graphite backend.
# # # # # # # # # # # # # # #

# JAVA HOME
JAVA_HOME=/x/opt/java7
PATH=${JAVA_HOME}/bin:${PATH}

# GRAPHITE HOST / PORT (This is actually the carbon daemon)
graphiteHost="stage2lp48.qa.paypal.com"
graphitePort=2003

dir=`dirname $0`
baseDir=$(readlink -e $dir)
debugDir=${baseDir}/../../logs/jmonitor
jmonitorExec=${baseDir}/jmonitor.jar
pidFile=${debugDir}/jmonitor.pid

# Common Script Functions
commonHome=${baseDir}/../../../Scripts/common

if [ -f ${commonHome}/commonFunctions.sh ];
then
   . ${commonHome}/commonFunctions.sh
else
    echo
    echo ----------------------------------------------------------------
    echo "Error finding ${commonHome}/commonFunctions.sh, exiting..."
    echo ----------------------------------------------------------------
    echo
    exit -1
fi

if [ ! -f ${jmonitorExec} ];
then
    fullPath=$(dirname $(readlink -e ${baseDir}))
    echo
    echo ---------------------------------------
    echo "ERROR: Failed to find jmonitor (jmonitor.jar) in expected path: ${fullPath}"
    echo ---------------------------------------
    echo
    exit -1 
fi

# # # # # # # # # # # # # # #
# Usage message and function
# The usage() function does a sed on this file and ouputs any lines starting
# with a double pound sign (##). All such lines will be printed verbatim
# # # # # # # # # # # # # # #

## Usage: startJmonitor.sh start|stop|restart -t JMonitorConfig.template
##
## Required Parameters
##   start|stop|restart: Start, Stop or Restart JMonitor
##
##   -t, --template   A template configuration file that is used as the basis for generating
##                    the JMonitorConfiguration.xml configuration file, specifying the apps
##                    mbeans, and attributes to monitor.  The primary function of the template
##                    is to abstract the stage name, the start script will replace @STAGE
##                    with the current stage name so the template is mostly portable across stage
##                    environments.
## Options:
##   -h, --help       Display this message.
##

# # # # # # # # # # # # # # #
# The parseArgs() function iterates over the input parameters and looks
# for specific flags. An unknown parameter will produce the usage message
# # # # # # # # # # # # # # #

#the template file we will convert to a jmonitor xml configuration
template=""

checkIfRunning() {

    if [ `pgrep -f "java.*jmonitor.jar" | wc -l` -gt 0 ];
    then
        return 1
    else 
        return 0
    fi
}

stop() {
    PIDS=`pgrep -f "java.*jmonitor.jar"`

    if [ "x${PIDS}" = "x" ]; 
    then
        logMsg "Didn't find any JMonitor processes to stop"
        return 0
    fi

    for p in ${PIDS}; do 
        logMsg "Stopping JMonitor: ${p}"
        
        kill ${p}
    done

    sleep 3
    PIDS=`pgrep -f "java.*jmonitor.jar"` 

    for p in ${PIDS}; do
        logMsg "Failed to Stop JMonitor [$p], forcefully killing the process"
        kill -9 ${p}
    done

    if [ $? -eq 0 ]; then
        logMsg "Successfully stopped JMonitor Processes..."
        return 0
    else
        logMessage "Failed to stop JMonitor Processes..."
    fi
}

start() {
    template=$1
    if [ "x$template" = "x" ];
    then
       echo
       echo ---------------------------------------------------------------------
       echo Error starting JMonitor, template file not defined
       echo ---------------------------------------------------------------------
       echo

       exit -1 
    fi

    checkIfRunning

    running=$?

    if [ $running = 1 ]; 
    then
        echo
        echo ---------------------------------------------------------------------
        echo JMonitor is already running, please stop the existing instance first
        echo ---------------------------------------------------------------------
        echo 

        exit 0 
    fi

    configFile=${debugDir}/jmonitor.xml
    logProps=${baseDir}/logging.properties
    logHome=${debugDir}
    #hostname=`hostname`
    #hostname="${hostname}.qa.paypal.com"
    hostname="127.0.0.1"
    
    #substitute our values for the the template params, and create a new config file
    logMsg "Creating ${configFile} from template ${template}"
    sed 's/@STAGE/'${hostname}'/g;s/@GRAPHITE_HOST/'${graphiteHost}'/g;s/@GRAPHITE_PORT/'${graphitePort}'/g' ${template} > ${configFile}

    #jmonitor requires the absolute path to our config file, and we can't call readlink until the sed runs and creates the config file
    absConfig=$(readlink -e ${configFile})

    ${JAVA_HOME}/bin/java -DconfigFile=${absConfig} -Dmode=metrics -Djava.util.logging.config.file=${logProps} -Duser.home=${logHome} -jar ${baseDir}/jmonitor.jar >> ${debugDir}/jmonitor-start.log &

    PIDS=`pgrep -f jmonitor.jar`
    echo ${PIDS} > ${pidFile}

    logMsg "Started JMonitor Processes: ${PIDS}"
	exit 0
}

parseArgs() {
    [[ $# -eq 0 ]] && usage

    while [ $# -gt 0 ]
    do
       case $1 in
            (-h|--help) usage;;
            (start) shift; operation="start";;
            (restart) shift; operation="restart";;
            (stop) shift; operation="stop";;
            (-g|--graphitehost) shift; graphiteHost=$1; shift;;
            (-t|--template) shift; template=$1; shift;;
            (-*) usage "unknown option: $1";; 
            (*) break;;
       esac
    done
}

# # # # # # # # # #
#  MAIN
# # # # # # # # # #
parseArgs $@

USER=`whoami`
if [ ${USER} = "root" ];
then
    echo
    echo ----------------------------------------
    echo Please do not run jmonitor as root
    echo ----------------------------------------
    echo

    exit -1
fi

# create our log files
createDirectory $debugDir 777


case "$operation" in
  stop)
    stop
    ;;
  start)
    start $template
	exit 0
    ;;
  restart)
    stop
    start $template
    ;;
  *)
    usage
esac
