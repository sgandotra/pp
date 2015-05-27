#!/bin/sh

# # # # # # # # # # # # # # #
# This shell script manages nodejs processes on the host
#
# Monitor results are output to the Graphite backend.
# # # # # # # # # # # # # # #

# GRAPHITE HOST / PORT (This is actually the carbon daemon)
graphiteHost="stage2lp48.qa.paypal.com"
graphitePort=2003

dir=`dirname $0`
baseDir=$(readlink -e $dir)
debugDir=${baseDir}/../../logs/nodemon

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


# # # # # # # # # # # # # # #
# Usage message and function
# The usage() function does a sed on this file and ouputs any lines starting
# with a double pound sign (##). All such lines will be printed verbatim
# # # # # # # # # # # # # # #

## Usage: startNodeMon.sh start|stop|status -s servicename -h graphiteHost
##
## Required Parameters
##   start|stop|status : Start, Stop or status of nodejs service
##   service name, nodejs app which needs to be configured
##
## Options:
##   -h, --help       Display this message.
##

# # # # # # # # # # # # # # #
# The parseArgs() function iterates over the input parameters and looks
# for specific flags. An unknown parameter will produce the usage message
# # # # # # # # # # # # # # #

checkService() {
	newstage=$(echo $HOSTNAME |tr '[a-z]' '[A-Z]')
	serviceLocation="/x/web/$newstage/$1"
	indexLocation="$serviceLocation/index.js"
	logMsg "Checking for service location $indexLocation"
	if [ ! -f $indexLocation ] 
		then
		 	logErrMsg "Service $service not found or is not an nodeapp, exiting.."
			exit 127
		fi
}

status() {
	checkService $servicename
	PIDS=`pgrep -f "$servicename/index.js"`

	if [ "x${PIDS}" == "x" ]; then
		logMsg "$servicename stopped"	
		exit 0
	fi

	logMsg "$servicename running"
	exit 0

}


stop() {
	set -x
	checkService $servicename
    PIDS=`pgrep -f "$servicename/index.js"`

    if [ "x${PIDS}" = "x" ]; then
        logMsg "Didn't find any nodejs processes to stop"
        return 0
    fi

	`cd $serviceLocation; ./shutdown.sh > /tmp/.shut.log`

#-- TODO
#   replace all occurences of statsd dependency from the index.js script
	#after_replace="mon = require('.\/nodemon'),"
	#after_init="mon.graphite();"
	after_replace="mon.*"
	after_init="mon.*"

	sed -i "s/$after_replace//g" $indexLocation
	sed -i "s/$after_init//g" $indexLocation

    sleep 10
    PIDS=`pgrep -f "$servicename/index.js"` 

    for p in ${PIDS}; do
        logMsg "Failed to Stop $servicename [$p], forcefully killing the process"
        kill -9 ${p}
    done

	`rm -f $serviceLocation/.node.lck`

	`cd $serviceLocation; ./start.sh > /tmp/.start.log`

    if [ $? -eq 0 ]; then
        logMsg "Successfully stopped and restarted nodejs Processes..."
        return 0
    else
        logMsg "Failed to stop and restart nodejs Processes..."
		return 0
    fi
}

start() {
	


    if [ "x$servicename" = "x" ];
    then
       echo
       echo ---------------------------------------------------------------------
       echo Error starting nodemon, servicename not defined
       echo ---------------------------------------------------------------------
       echo

       exit -1 
    fi

    #substitute our values for the the template params, and create a new config file
    logMsg "Checking if the service exists"
	checkService $servicename
	logMsg "Service found and identified as a nodeapp"

	# check if lck file exists
	if [ -f $serviceLocation/.node.lck ]
	then
		echo "Lock file exists, can only have One monitoring session running, run shutdown first"
		exit 
	fi
	
	#add hook into index.js
		#1. add requires
		before_require="kraken-js')\,"
		after_require="kraken-js')\,mon = require('.\/nodemon').graphite()\,"
		sed -i "s/$before_require/$after_require/g" $indexLocation
		# test changes
		if [ `grep mon $indexLocation | wc -l` == 0 ]
		then
			logMsg "Requires Changes could not be propagaged..exiting!"
			exit 1
		fi
		logMsg "added requires dependency"

		#2. copy js
		`cp conf/nodemon.js $serviceLocation`
		sed -i "s/@GRAPHITEHOST/$graphiteHost/"  $serviceLocation/nodemon.js
		if [ ! -f "$serviceLocation/nodemon.js" ] 
		then
			logMsg "nodemon.js missing at $serviceLocation , cannot continue"
			exit 1
		fi
		logMsg "Copied nodemon.js to the directory"

		#4. copy node-statsd
		`cp -r conf/statsd-client $serviceLocation/node_modules`
		if [ ! -d "$serviceLocation/node_modules/statsd-client" ]
		then
			logMsg "Directory node-statsd does not exit"
			exit 1
		fi
		logMsg "Directory node-statd found in the servicelocation"

		#5. restart
		logMsg "restarting node process"
		`cd $serviceLocation; ./shutdown.sh; ./start.sh > /tmp/.start.log`
		 PIDS=`pgrep -f $servicename/index.js`
         #echo ${PIDS} > ${pidFile}
    	 logMsg "Started nodemon Processes: ${PIDS}"

		 if [ "x$PIDS" == "x" ] 
		 then
			 logMsg "Could not start nodeApp"
			 exit 1
	     fi
		 `touch $serviceLocation/.node.lck`
	exit 0
}

parseArgs() {
    [[ $# -eq 0 ]] && usage

    while [ $# -gt 0 ]
    do
       case $1 in
            (-h|--help) usage;;
            (start) shift; operation="start";;
            (status) shift; operation="status";;
            (stop) shift; operation="stop";;
            (-g|--graphitehost) shift; graphiteHost=$1; shift;;
            (-s|--servicename) shift; servicename=$1; shift;;
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
    echo Please do not run nodemon as root
    echo ----------------------------------------
    echo

    exit -1
fi

# create our log files
createDirectory $debugDir 777


case "$operation" in
  stop)
    stop $service
    ;;
  start)
    start $service
	exit 0
    ;;
  status)
    status $service
	exit 0
    ;;
  *)
    usage
esac
