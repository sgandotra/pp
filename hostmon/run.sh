#!/bin/sh
set -x
NODE_HOME=`dirname $0`
PATH=$PATH:${NODE_HOME}/node/bin
FOREVER=${NODE_HOME}/monitoringctlr/node_modules/forever/bin/forever
BASEPATH=.
CURRENTDIR=`pwd`
LOGS=${CURRENTDIR}/logs/*.log
FIFOOUT=forever.out


#CHECK user is non-root
if [ $USER == 'root' ]; then
	echo "Cannot run monitoringctlr as root"
        exit 1
fi

#start forever
#$FOREVER start -m 10 -a -p ${CURRENTDIR} -l ${NODE_HOME}/logs/forever.log -o ${NODE_HOME}/logs/stdout.log -e ${NODE_HOME}/logs/err.log --minUptime 1000 --spinSleepTime 5000 --pidFile ${CURRENTDIR}/forever.pid ${NODE_HOME}/monitoringctlr/app.js
$FOREVER start -m 10 -a -n 0 -p ${CURRENTDIR} --minUptime 1000 --spinSleepTime 5000 --pidFile ${CURRENTDIR}/forever.pid ${NODE_HOME}/monitoringctlr/app.js --fifo ${FIFOOUT}

#cat > hostmon.conf <<- EndOfMessage
# ${LOGS} {
#       daily
#       size 5M
#      rotate 10
#       compress
#       missingok
#       notifempty
#      postrotate
#      		kill -USR2 `cat {CURRENTDIR}/forever.pid`
#       endscript
#}
# EndOfMessage


rm -f *.log

#cp hostmon.conf /etc/logrotate.d







