#!/bin/sh

export TERM=vt100
FOLDER=$1

if [ "x$1" == "x" ]; then
	"Usage : $0 deploymentfolder"
	exit -1
fi

`cd $1; ./shutdown.sh; /x/tools/common/mystart ./start.sh &> /dev/null`
