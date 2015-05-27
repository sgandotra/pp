#!/bin/sh
NODE_HOME=`dirname $0`
PATH=$PATH:${NODE_HOME}/node/bin
FOREVER=${NODE_HOME}/monitoringctlr/node_modules/forever/bin/forever


echo $PATH
node -v

#start forever
./$FOREVER stopall 


