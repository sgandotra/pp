/**
    @description: nmon command implementation
 **/

var os                      = require('os'),
    path                    = require('path'),
    fs                      = require('fs'),
    Q                       = require('q'),
    sqlite                  = require('./SqliteManager'),
    log4js                  = require('log4js'),
    logger                  = log4js.getLogger('nmon'),
    config                  = require('./config'),
    RemoteExec              = require('./RemoteExec');



var nmon = (function() {
    "use strict";

    var response    = {},
        baseUrl     = process.cwd(),
        nmonFolder  = path.join(baseUrl,"monitoringctlr",'Scripts','hostmon','nmon');


    return {
        handleSuccess : function(status) {
            var dbStatus = status[1][0];

            response = {
                status : 'SUCCESS',
                nmonStatus : dbStatus,
                timestamp : new Date()
            };
        },
        handleFailure : function(err) {
            response.status = 'ERROR';
            response.err    = err;
            response.time   = new Date();
        },
        getNmonOutputFile : function() {
            var location   = path.join(baseUrl,'monitoringctlr','logs','nmon'),
                date       =  new Date(),
                folderName = [date.getFullYear(),date.getMonth(),date.getDay(),
                    '_',date.getHours(),'00',date.getMinutes()],
                fileName   =  os.hostname() + folderName.join('') + '.nmon',
                absPath    = path.join(location,folderName.join(''),fileName);

            return absPath;
        },
        getResponseStatus : function() {
            return response;
        },
        status : function(type) {
            var self            = this,
                getNmonStatus   =  function() {
                    var remoteExec     = new RemoteExec(),
                        shellScript    = path.join(nmonFolder,'status.sh'),
                        cmd            = [shellScript,'-j'],
                        deferred       = Q.defer(),
                        opts           = {
                            cwd : nmonFolder,
                            maxBuffer : 1024*200
                        };

                    remoteExec.setExecOpts(opts);
                    remoteExec.execCmd(cmd.join(' '),deferred);
                    return deferred.promise;
                },
                getSqliteStatus  = function() {
                    return sqlite.select('nmon');
                };


            return Q.all([getNmonStatus(),getSqliteStatus()])
                    .spread(function(nmonStatus,sqliteStatus) {
                        nmonStatus = nmonStatus.replace(/\\"/g, '"');
                        self.handleSuccess([nmonStatus,sqliteStatus]);
                        return self.getResponseStatus();
                    },function(err) {
                        self.handleError(err);
                        return self.getResponseStatus();
                    });

        },
        activate : function(input) {
            var self        = this,
                frequency       = input.frequency || 10,
                samples         = input.samples   || 8640,
                graphite        = input.graphite  || undefined,
                graphiteHost    = config.getGraphitehost('configuration.yaml'),
                doActivate  = function() {
                    var remoteExec      = new RemoteExec(),
                        shellScript     = path.join(nmonFolder,'run.sh'),
                        nmonOutFile     = self.getNmonOutputFile(),
                        cmd,
                        opts           = {
                            cwd : nmonFolder,
                            maxBuffer : 1024*200
                        },
                        deferred    = Q.defer();

                    graphite = (typeof graphite !== 'undefined' && graphite === 'false') ? '' : '-g';

                    cmd = [shellScript,'-f',frequency,'-s',samples,graphite,'-o',nmonOutFile,'-t',graphiteHost];
                    logger.info('Executing cmd : ' +cmd.join(' '));

                    remoteExec.setExecOpts(opts);
                    remoteExec._spawnCmd(cmd,deferred,true);

                    return deferred.promise;
                },
                updateDB    = function() {
                    var started = new Date().getTime(),
                        ended   = Number(started) + Number(frequency * samples),
                        params = {
                            type : 'nmon',
                            started : started,
                            ended   : ended,
                            status : 'ACTIVATED'
                        };
                    logger.info('Updating db with params : ',params);
                    return sqlite.update(params);
                },
                testLckFile = function() {
                    var nodeFile = path.join(nmonFolder,'.nmon.lck');
                    logger.info('Trying to stat ',nodeFile);
                    try {
                        fs.statSync(nodeFile);
                        return true;
                    } catch(e) {
                        return false;
                    }
                };

            if(testLckFile()) {
                var status = ' Lock file exists, an existing monitoring session is on-going';
                self.handleSuccess(status);
                return self.getResponseStatus();
            }

            logger.info('Activating nmon');
            return Q.all([doActivate(),updateDB()])
                .spread(function(nmonStatus,update) {
                    self.handleSuccess([nmonStatus,update]);
                    return self.getResponseStatus();
                },function(err) {
                    self.handleFailure(err);
                    return self.getResponseStatus();
                });
        },
        deactivate : function() {
            var self = this,
                doDeactivate = function(type) {
                    var remoteExec     = new RemoteExec(),
                        shellScript    = path.join(nmonFolder,'stop.sh'),
                        cmd            = [shellScript,'-j'],
                        deferred       = Q.defer(),
                        opts           = {
                            cwd : nmonFolder,
                            maxBuffer : 1024*200
                        };

                    logger.info('Executing shell script : ' +shellScript);

                    remoteExec.setExecOpts(opts);
                    remoteExec.execCmd(cmd.join(' '),deferred);
                    return deferred.promise;
                },
                updateDB     = function() {
                    var started = 0,
                        ended   = 0,
                        params = {
                            type : 'nmon',
                            started : started,
                            ended   : ended,
                            status : 'INACTIVE'
                        };
                    logger.info('Updating db with params : ',params);
                    return sqlite.update(params);
                };

            logger.info('deactivating nmon');
            return Q.all([doDeactivate('nmon'),updateDB()])
                .spread(function(nmonStatus,dbStatus) {
                    logger.info('Deactivate : nmonStatus ',nmonStatus);
                    logger.info('Deactivate : dbStatus ',dbStatus);
                    self.handleSuccess([nmonStatus,dbStatus]);
                    return self.getResponseStatus();
                },function(err) {
                    self.handleFailure(err);
                    return self.getResponseStatus();
                });

        }
    };


})();


module.exports = nmon;
