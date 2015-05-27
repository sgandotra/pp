/**
 * Created with JetBrains WebStorm.
 * User: sagandotra
 * Date: 5/18/14
 * Time: 11:33 AM
 * To change this template use File | Settings | File Templates.
 */

var os                      = require('os'),
    _                       = require('underscore'),
    path                    = require('path'),
    fs                      = require('fs'),
    Q                       = require('q'),
    RemoteExec              = require('./RemoteExec'),
    log4js                  = require('log4js'),
    config                  = require('./config'),
    logger                  = log4js.getLogger('nodemon');


var nodemon = (function() {

    'use strict';

    var baseUrl       = path.join(__dirname,".."),
        nodemonFolder = path.join(baseUrl,"Scripts","hostmon","nodemon"),
        response      = {};

    return {
        activate : function(services) {
            var doActivate = function(service) {
                var deferred     = Q.defer(),
                    nodemonScrpt = path.join(nodemonFolder,"nodemon.sh"),
                    exec         = [nodemonScrpt,"start","-s"],
                    remoteExec   = new RemoteExec(),
                    graphiteHost = config.getGraphitehost("configuration.yaml") || 'stag2lp48.qa.paypal.com',
                    opts;

                if(!service.name) {
                    throw new Error("Service name is required, look at api documentation");
                }

                logger.info("processing service: " +service.name);
                exec.push(service.name);

                logger.info("getting graphite host name :",graphiteHost);
                exec.push("-g");
                exec.push(graphiteHost);

                opts = {
                    cwd : nodemonFolder  ,
                    maxBuffer : 1024*200
                };

                remoteExec.setExecOpts(opts);
                remoteExec.execCmd(exec.join(" "),deferred);

                return deferred.promise;
            },
            promises = [],
            validServices = [],
            invalidServices = [],
            _services       = {},
            deferred        = Q.defer();

            _.each(services,function(service) {
                var promise = doActivate(service);
                promises.push(promise);
            });

            Q.allSettled(promises)
                .then(function(results) {
                    _.each(results,function(result,indx) {
                        if(result.state === 'fulfilled') {
                            services[indx].result = result.value.split('\n');
                            validServices.push(services[indx]);
                        } else {
                            logger.info("ERROR : [",result.reason + "]");
                            services[indx] = result.reason;
                            invalidServices.push(services[indx]);
                        }
                    });
                    _services =  {
                        valid : validServices,
                        invalid : invalidServices
                    };
                    response.services = _services;
                    deferred.resolve(response);
                })
                .fail(function(error) {
                    logger.info("error : ",error);
                    response.error = error;
                    deferred.reject(response);
                });
            return deferred.promise;


        },
        deActivate : function(services) {
            var doDeActivate = function(service) {
                    var deferred     = Q.defer(),
                        nodemonScrpt = path.join(nodemonFolder,"nodemon.sh"),
                        exec         = [nodemonScrpt,"stop","-s"],
                        remoteExec   = new RemoteExec(),
                        opts;

                    if(!service.name) {
                        throw new Error("Service name is required, look at api documentation");
                    }

                    logger.info("processing service: " +service.name);
                    exec.push(service.name);

                    opts = {
                        cwd : nodemonFolder  ,
                        maxBuffer : 1024*200
                    };

                    remoteExec.setExecOpts(opts);
                    remoteExec.execCmd(exec.join(" "),deferred);

                    return deferred.promise;
                },
                promises = [],
                validServices = [],
                invalidServices = [],
                _services       = {},
                deferred        = Q.defer();

            _.each(services,function(service) {
                var promise = doDeActivate(service);
                promises.push(promise);
            });

            Q.allSettled(promises)
                .then(function(results) {
                    _.each(results,function(result,indx) {
                        if(result.state === 'fulfilled') {
                            services[indx].result = result.value.split('\n');
                            validServices.push(services[indx]);
                        } else {
                            logger.info("ERROR : ",result.reason);
                            services[indx].result = result.reason;
                            invalidServices.push(services[indx]);
                        }
                    });
                    _services =  {
                        valid : validServices,
                        invalid : invalidServices
                    };
                    response.services = _services;
                    deferred.resolve(response);
                })
                .fail(function(error) {
                    logger.info("error : ",error);
                    response.error = error;
                    deferred.reject(response);
                });
            return deferred.promise;
        },
        status : function(services) {
            var hostname     = os.hostname().toUpperCase(),
                pathToFolder = path.join("/x/web/",hostname),
                lckFile      = ".node.lck",
                response    = {},
                promises     = [],
                deferred    = Q.defer();

            _.each(services,function(service) {
                var lckFileAbsPath = path.join(pathToFolder,service.name.toLowerCase(),lckFile);
                promises.push(Q.nfcall(fs.stat,lckFileAbsPath));
            });

            Q.allSettled(promises)
                .then(function(results) {
                    _.each(results,function(result,indx) {
                        if(result.state === 'fulfilled') {
                            services[indx].result = 'Lock file exists';
                        } else {
                            services[indx].result = 'No lock file, service not being monitored';
                        }
                    });

                    response.services = services;
                    deferred.resolve(response);
                })
                .fail(function(error) {
                    response.error = error;
                    deferred.reject(response);
                });

            return deferred.promise;


        }
    };


})();

module.exports = nodemon;