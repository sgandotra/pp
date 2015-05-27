/**
 * Created by sagandotra on 11/4/14.
 */
var _           = require('underscore'),
    spawn       = require('child_process').spawn,
    exec        = require('child_process').exec,
    log4js      = require('log4js'),
    logger      = log4js.getLogger('jmeter'),
    fs          = require('fs'),
    path        = require('path'),
    Q           = require('q'),
    util        = require('util');


var JmeterManager = (function() {
    "use strict";

    //private variables
    var defaults =  {
        'installation' : path.join(__dirname,'../jmeter/bin/jmeter.sh'),
        'bg-params_1'  : '-n',
        'bg-params_2'  : '-l' + path.join(__dirname,'../logs/logfile.log'),
        'bg-params_3'  : '-j' + path.join(__dirname,'../logs/jmeter.log'),
        'ptable'       : {}
        },
        requiredFields = ['vusers','duration','stageName'],
        runTimeConfig      = {},
        execCmd            = [],
        isRunning          = false,
        pid                = '',
        initOutput         = '',
        captureInit        = true,
        deferred;

    //private methods
    var remoteExec         = function(putStatus) {

        deferred           = Q.defer();
        logger.info("Executing JmeterManager test : "+runTimeConfig.installation + " " +execCmd);

        var _run = spawn(runTimeConfig.installation,execCmd);
        pid      = _run.pid;
        logger.info("Running JMeter execution with process id",pid);

        _run.stdout.on('data',handleOutput(putStatus));
        _run.stderr.on('data',handleError);
        _run.on('exit',handleExit(putStatus));

        return deferred.promise;
    },
    validateLocation = function(loc) {
        logger.info("Validating JmeterManager location : ",loc);
        if(!loc)
            throw new Error("JMeter location invalid or notfound : ",loc);
        try {
            fs.statSync(loc);
        } catch (err) {
            logger.error("No valid installation of JmeterManager found!");
            throw new Error("JmeterManager not found at : ",loc);
        }
    },
    handleOutput        = function(putStatus) {

        return function(data) {


            if(data.toString().match(/.*Starting the test.*/)) {
                logger.info("Test started succesfully with runtimeConfig : ",runTimeConfig);
                isRunning   = true;
                captureInit = false;
                deferred.resolve();
            } else {
                if(captureInit)
                    initOutput += data.toString();
            }

            if(!data.toString().match(/summary/)) {
                var status = data.toString().split('\t');
                if(status.length === 6) {
                    var response = {
                        'samples' : status[0],
                        'threads' : status[1].split(':')[1].trim() || "",
                     //   'samples' : status[2].split(':')[1].trim() || "",
                        'latency' : status[3].split(':')[1].trim() || "",
                        'rt'      : status[4].split(':')[1].trim() || "",
                        'errors' : status[5].split(':')[1].trim() || ""
                    };

                    putStatus(pid,response,'running');
                } else {
                    logger.info("Not updating status, bad line : ",data.toString());
                }

            }
        }
    },
    handleError = function(error) {
        logger.error("Test Execution with runTimeConfig : " + runTimeConfig + " failed with Error ",error);
        deferred.reject(new Error(error));

    },
    handleExit = function(putStatus) {

        return function(exitCode) {
            logger.info("JMeter current status : " + isRunning + ", Jmeter Execution Exit code : ",exitCode);

            if(!isRunning) {
                putStatus(pid,{},"Jmeter test execution failed with error code : " +exitCode);
                deferred.reject(new Error("failed with error msg : " +exitCode + "  " + util.format("%s",initOutput.toString())));
            } else {
                isRunning = false;
                putStatus(pid,{},'completed');
            }
        }

    },
    validate =   function(config) {
        var object = _.difference(requiredFields, _.keys(config));
        if(object.length > 0)
            return false;

        return true;
    }


    return {
        getStatus   : function(pid) {
            var status =  defaults.ptable[pid];
            logger.info('Searching ptable for pid : ' + pid + " returned : '", status);
            return status;
        },
        putStatus   : function(pid,status,state) {
            if(pid && _.isNumber(pid)) {
                defaults.ptable[pid] = {
                    "status" : status,
                    'state'  : state,
                    'timestamp' : new Date()
                }
            }
        },
        delete : function(pid) {
            if(pid && defaults.ptable[pid]) {
                logger.info("Sending kill cmd to pid : ",pid);
                var _run = exec("pkill -9 -P "+pid);
                return "jmeter test run with execution id " + pid + " stopped";
            } else {
                return "pid not found";
            }
        },
        start :     function(config,script)  {
            logger.info("Received request to start JMeter test");

            if(!script || !config)
                throw new Error("Invalid configuration or cmd");

            var script  = script.trim(),
                args    = config;

            if(!script || !args)
                throw new Error("Invalid Request, script : " + script + " args : " + args);

            if(!validate(config)) {
                var response = {
                    'status' : 'ERROR',
                    'responseMsg' : requiredFields + " match failed. These are" +
                        "the mimimum conditions to be provided as Key=-DValue pairs"
                }

                throw new Error(response.responseMsg);
            }

            runTimeConfig = _.extend(defaults,config);
            logger.info("runTimeConfig : ",runTimeConfig);
            execCmd       = [runTimeConfig['bg-params_1'],
                             runTimeConfig['bg-params_2'],
                             runTimeConfig['bg-params_3'],
                             '-t',
                             script,
                            ];

            _.each(config,function(arg,indx) {
                var _arg = arg.trim();
                if(_arg.length == 0) throw new Error("Invalid Jmeter argument value");
                execCmd.push(arg.trim());
            });
        },
        run : function() {
            var response = {
                'status'      : '',
                'responseMsg' : '',
                'executionID' : ''   ,
                'dateStarted' : new Date().getTime()
                },
            deferred      = Q.defer();
            remoteExec(this.putStatus).then(function() {
                response.status      = 200;
                response.responseMsg = "SUCCESS";
                response.executionID = pid

                logger.info("JMeter test with config : " + runTimeConfig + " completed successfully");
                deferred.resolve(response);
            },
            function(error) {
                response.status = 500;
                response.responseMsg = error.message;

                deferred.reject(response);
            });

            return deferred.promise;
        }

    }
}) ();

module.exports = JmeterManager




