

"use strict";

var os = require('os'),
    fs = require('fs'),
    path = require('path'),
    RemoteExec      = require('../js/RemoteExec'),
    _               = require('underscore'),
    Q               = require('q');

exports.list = function(req,res) {

    var stagename           = os.hostname().toUpperCase(),
        deploymentFolder    = path.join("/x/web",stagename),
        dirnames;

    if(deploymentFolder) {
        dirnames            = fs.readdirSync(deploymentFolder);
    }

    res.json(dirnames);
}

exports.javarestart = function(req,res) {
    var stagename           = os.hostname().toUpperCase(),
        _path               =  path.join("/x/web",stagename),
        query               = req.param('q'),
        deferred            = Q.defer(),
        remoteExec          = new RemoteExec(),
        cmd                 = ['cd',_path,';','grep','"[0-9]*"','*/*.pid'],
        response            = {
            'status' : '',
            'message' : '',
            'payload' : ''
        },
        processes             = [],
        saveJson              = function(processes) {

        } ;
    console.log("received query for : ",query);
    //remoteExec.setExecOpts(cwd);
    remoteExec.execCmd(cmd.join(" "),deferred)
            .then(function(text) {
                var processlist = text.split('\n');
                _.each(processlist,function(val,indx) {
                    var process = val.split(":"),
                        processName=process[0],
                        processId  = process[1];

                    var entry = {
                        status : '',
                        cmdline : '',
                        processName : processName,
                        processid : processId,
                        isJava : (processName.match(/java/) === null) ? false : true
                    };
                    //console.log("query=",query);
                    //console.log("index="+entry.processName+ "="+entry.processName.indexOf(query))       ;
                    if(query && entry.processName.indexOf(query) >= 0
                        && entry.isJava === true) {
                        processes.push(entry);
                    } else if(!query) {
                        processes.push(entry);
                    } else {}

                });
                res.json({"data" : processes});
            },function(error) {
                res.status(500);
                response.status = "ERROR";
                response.message = (typeof error === 'object') ? error.toString() : error;
                res.json(response);
            })
}

exports.getpid = function(req,res) {
    var servicename = req.params.servicename;

    if(servicename) {
        var resolvedServiceName = servicename.replace('+','/'),
            stagename           = os.hostname().toUpperCase(),
            absPath             = path.join("/x/web",stagename,resolvedServiceName);

        fs.stat(absPath,function(err,stats) {

            if(err) {
                res.json({ data : {
                    path : resolvedServiceName,
                    mtime : ""
                }});
            }
            res.json({ data : {
                path : resolvedServiceName,
                mtime : stats && stats.mtime || "",
                atime : stats && stats.atime || "",
                ctime : stats && stats.ctime || ""
            }
            });
        })

    } else {
        res.json({error : "error , service list is undefined"});
    }
}


exports.appstat = function(req,res) {
    var shellScript = path.join(__dirname,"../../../Tools/hostmon/util/calculateStats.sh"),
        pids        = req.params.list,
        deferred    = Q.defer(),
        remoteExec  = new RemoteExec(),
        cmd         = [shellScript,pids];

    if(pids) {
        console.log("Executing shell script")
        pids = pids.replace('+'," ");

        remoteExec.execCmd(cmd.join(" "),deferred)
            .then(function(response) {
                var responsePayload = [],
                    lines = response.trim().split("\n");

                var sysMetrics = lines.pop();
                var sysBefore = sysMetrics.trim().split(',')[0],
                    sysAfter  = sysMetrics.trim().split(',')[1],
                    sysTicks  = (parseInt(sysAfter[1],10) + parseInt(sysAfter[2],10) + parseInt(sysBefore[3],10)
                                - parseInt(sysBefore[1],10) - parseInt(sysBefore[2],10) - parseInt(sysBefore[3],10));
                console.log('sysbefore : ',sysBefore);
                console.log('sysafter : ',sysAfter,parseInt(sysAfter[3],10),parseInt(sysAfter[4],10),parseInt(sysAfter[5],10));
                console.log("systicks : ",sysTicks);
                for(var i = 0; i < lines.length; i++) {
                    var before = lines[i].trim().split(',')[0],
                        after  = lines[i].trim().split(',')[1];

                    var metricsBefore = before.split(" "),
                        metricsAfter  = after.split(" "),
                        pid           = metricsAfter[0],
                        numOfTicks    = (parseInt(metricsAfter[13],10) + parseInt(metricsAfter[14],10)
                                            - parseInt(metricsBefore[13],10)) - parseInt(metricsBefore[14],10);
                        console.log("pid : " +pid + " ticks : ",numOfTicks);
                        var metric = {
                            'pid' : pid,
                            'ticks' : numOfTicks,
                            'systicks': sysTicks
                        }
                    responsePayload.push(metric);
                }

                res.json(responsePayload);
            });
    }
}

exports.meminfo = function(req,res) {
    var shellScript = path.join(__dirname,"../../../Tools/hostmon/jmonitor/meminfo.sh"),
    remoteExec = new RemoteExec(),
        deferred   = Q.defer();


    console.log("cmd : " , shellScript);
    remoteExec.execCmd(shellScript,deferred)
        .then(function(response) {

            res.json(response);
        });


}
