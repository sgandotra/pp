/**
 * Created with JetBrains WebStorm.
 * User: sagandotra
 * Date: 5/19/14
 * Time: 2:53 PM
 * To change this template use File | Settings | File Templates.
 */

var _                       = require('underscore'),
    path                    = require('path'),
    fs                      = require('fs'),
    RemoteExec              = require('./RemoteExec'),
    ManageXmlTemplate       = require('./ManageXmlTemplate'),
    Q                       = require('q'),
    log4js                  = require('log4js'),
    logger                  = log4js.getLogger('jmon'),
    config                  = require('./config'),
    helper                  = require('./StageHelper');



var jmon = (function() {

    "use strict";

    var baseUrl          = path.join(__dirname,'..','Scripts','hostmon','jmonitor'),
        restartScript    = path.join(baseUrl,"restart.sh"),
        defaults = {
            restartScriptLoc : "/x/tools/common/mystart",
            supportedJavaServices : ["HELIX_JBOSS_7","SPARTA_JBOSS_7","SPARTA_GERONIMO","HELIX_GERONIMO"],
            SPARTA_JBOSS_GERONIMO : {
                _cmd     : ["cdbdump","<" ,"cdbs/application/application.cdb","|","grep","RmiRegistryPort"],
                getPort  : function(service,deferred) {
                    var cmd = this._cmd.join(" ");
                    helper.execCmd(service.serviceDeploymentFolder,cmd,deferred);
                }
            },
            HELIX_JBOSS : {
                _cmd     : ["cdbdump","<" ,"__PLACEHOLDER__","|","grep","rmi_naming_port"],
                getPort  : function(service,deferred) {

                    this._cmd[2] = "config/"+service.name+".cdb";

                    var cmd = this._cmd.join(" ");
                    helper.execCmd(service.serviceDeploymentFolder,cmd,deferred);
                }
            }
        };


    return {
        isServiceRunning   : function(service) {
            var deferred = Q.defer();
            helper.isServiceRunning(service.name,"java",deferred);
            return deferred.promise;
        },
        getPortNumber     : function(service,data) {
            var port        =  data.split('->')[1],
            newLineIndx = port.indexOf('\n');

            if (newLineIndx !== -1 ) {
                port  = port.substring(0,newLineIndx);
            }

            service.port = port;
            logger.info("service : ",service);
            return service;
        },
        getCdbDump        : function(service) {
            var deferred  = Q.defer();
            logger.info("Getting cdbdump for service type : ",service.type);
            if(service.type === "SPARTA_GERONIMO" || service.type === "SPARTA_JBOSS_7") {
                defaults.SPARTA_JBOSS_GERONIMO.getPort(service,deferred);
            } else if(service.type === "HELIX_JBOSS_7" || service.type === 'HELIX_GERONIMO')  {
                defaults.HELIX_JBOSS.getPort(service,deferred);
            } else {
                throw new TypeError("Type not registered, please contact developer");
            }


            return deferred.promise;
        },
        checkServiceType : function(service) {
            var deployFolder;

            try {
                deployFolder = helper.getDeploymentFolder();


                logger.info("[checkServiceType] deploy folder : " +deployFolder + " service name : " +service.name);
                var serviceDeploymentFolder = path.join(deployFolder,service.name);

                if(fs.existsSync(path.join(serviceDeploymentFolder,"standalone")) &&
                    fs.existsSync(path.join(serviceDeploymentFolder,"helix-lib.sh"))) {
                    logger.info(service.name + " is a jboss7 helix service" );
                    service.type                    = "HELIX_JBOSS_7";
                    service.serviceDeploymentFolder =  serviceDeploymentFolder;
                }
                else if(fs.existsSync(path.join(serviceDeploymentFolder,"jboss-config")))  {
                    logger.info(service.name + " is a sparta jboss 7 service" );
                    service.type                    = "SPARTA_JBOSS_7";
                    service.serviceDeploymentFolder =  serviceDeploymentFolder;
                }
                else if(fs.existsSync(path.join(serviceDeploymentFolder,"spartaserv"))) {
                    logger.info(service.name + " is a sparta geronimo service" );
                    service.type                    = "SPARTA_GERONIMO";
                    service.serviceDeploymentFolder =  serviceDeploymentFolder;
                }
                else if(fs.existsSync(path.join(serviceDeploymentFolder,"helix-lib.sh"))) {
                    logger.info(service.name + " is a helix geronimo service" );
                    service.type                    = "HELIX_GERONIMO";
                    service.serviceDeploymentFolder =  serviceDeploymentFolder;
                }
                else {
                    service.type = "UNDEFINED";
                }
            }
            catch(e) {
                this.handleError(e);
            }

        },
        restartService : function(service) {
            var deferred                    = Q.defer(),
                serviceDeploymentFolder     = service.serviceDeploymentFolder;

            if(service.type !== 'UNDEFINED') {
                logger.info("Restarting service : ",service);

                this.isServiceRunning(service).then(function(procCount) {

                    var formattedProcCount = Number(procCount.trim());

                    logger.info("Is Process running : " , formattedProcCount);
                    if(formattedProcCount === 0) {
                        logger.info("not running , serviceDeploymentFolder : "+serviceDeploymentFolder);
                        if(serviceDeploymentFolder && fs.existsSync(serviceDeploymentFolder)) {
                            var remoteExec = new RemoteExec(),
                                cmd        = [restartScript,serviceDeploymentFolder],
                                opts       = {
                                    cwd         : serviceDeploymentFolder,
                                    maxBuffer   : 1024* 200
                                },
                                serviceName = serviceDeploymentFolder.substring(serviceDeploymentFolder.lastIndexOf('/') );

                            remoteExec.setExecOpts(opts);
                            logger.info("restarting service :", serviceName + " with cmd : " + cmd.join(" "));
                            remoteExec.spawnCmd(cmd).then(function(success) {
                                deferred.resolve(service + ' is running');
                                service.state = "isrunning";
                            },function(error) {
                                logger.info("error failed in restarting service : "+error);
                            });

                        }
                    }
                    else {
                        logger.info(" Service : " + service.name + " already running");
                        service.state = "isrunning";
                        deferred.resolve(service + ' already running');
                    }

                }, function(error) {
                    logger.info("is ERROR : ",error);
                    deferred.reject({message : 'Service ' + service.name + ' error : ' + error});
                });
            } else {
                console.log("Service type undefined");
                deferred.reject({
			'name' : service.name,
			'type' : 'UNDEFINED',
			'serviceDeploymentFolder' : 'N/A',
			'state'			  : 'N/A',
			'port'			  : 'N/A'
		});
            }

            return deferred.promise.timeout(90000);
        },
        verifyService : function(service) {
            var absServicePath,
                deploymentFolder = helper.getDeploymentFolder(),
                servicename      = service.name;

            logger.info("Received a request to verify : ",servicename);

            if(servicename && servicename.length > 0) {
                if(deploymentFolder) {
                    absServicePath  = path.join(deploymentFolder,servicename);
                    if(fs.existsSync(absServicePath)) {
                        logger.info("Service found at :", absServicePath);
                        return absServicePath;
                    }
                }
            }
            throw new Error("Service name at location : " +absServicePath + " does not exist");
        },
        assembleActivationSteps : function(service) {

            var self     = this,
                deferred = Q.defer();

            this.verifyService(service);
            this.checkServiceType(service);

            this.restartService(service)
                .then(function(restartServiceResponse) {
                    return self.getCdbDump(service);
                })
                .then(function(cdbValue) {
                    return Q.when(self.getPortNumber(service,cdbValue), function(success) {
                        deferred.resolve(success);
                    },function(error) {
                        logger.info("Error :",error);
                    });
                })
                .fail(function(error) {
                    deferred.reject(error);
                });


            return deferred.promise;


        },
        enableMon : function(services) {
            var deferred         = Q.defer(),
                exec             = path.join(baseUrl,"jmonitor.sh"),
                templateLocation = path.join(baseUrl,"conf"),
                templateFile     = path.join(templateLocation,"JMX.xml"),
                graphiteHost     = config.getGraphitehost('configuration.yaml') || 'stage2lp48.qa.paypal.com',
                manageXml        = new ManageXmlTemplate(templateLocation,graphiteHost),
                cmd              = [exec,"start","-t",templateFile,"-g",graphiteHost];

            logger.info("calling generate Template");
            manageXml.genTemplate(services);
            logger.info("starting jmon with cmd : " +cmd.join(" "));

            helper.spawnCmd(baseUrl,cmd.join(" "),deferred);
            return deferred.promise;
        },
        status : function() {
            var  baseUrl          = path.join(__dirname,'..','Scripts','hostmon','jmonitor'),
                 lckFile          = path.join(baseUrl,'jmonitor.log.lck');

            return Q.nfcall(fs.stat,lckFile);
        },
        activate : function(services) {

            var promises        = [],
                self            = this,
                validServices   = [],
                invalidServices = [],
                _services       = {},
                response        = {},
                deferred        = Q.defer();

            _.each(services,function(service,indx) {
                promises[indx] = self.assembleActivationSteps(service);
            });

            Q.allSettled(promises)
                .then(function(results) {
                    _.each(results,function(result) {
                        if(result.state === 'fulfilled') {
                            validServices.push(result.value);
                        } else {
			    logger.info(">>>> INVALID : ",result.reason);
                            invalidServices.push(result.reason);
                        }
                    });
                    _services =  {
                        valid : validServices,
                        invalid : invalidServices
                    };
                    response.services = _services;
                    return _services;
                })
                .then(function(services) {
                    logger.info("All services",services);
                    return self.enableMon(services.valid);
                })
                .then(function(success) {
                    response.jmonresponse = success.split('\n');
                    deferred.resolve(response);
                })
                .fail(function(error) {
                    logger.info("error : ",error);
                    response.error = error;
                    deferred.reject(response);
                });

            return deferred.promise;
        },
        deactivate : function() {
            var deferred        = Q.defer(),
                response        = {},
                deactivate      = function() {
                    var _deferred         = Q.defer(),
                        baseUrl          = path.join(__dirname,'..','Scripts','hostmon','jmonitor'),
                        restartScript    = path.join(baseUrl,"restart.sh"),
                        exec             = path.join(baseUrl,'jmonitor.sh'),
                        cmd              = [exec,"stop"],
                        remoteExec = new RemoteExec(),
                        opts = {
                            cwd : baseUrl ,
                            maxBuffer : 1024*200
                        };
                    remoteExec.setExecOpts(opts);
                    remoteExec.execCmd(cmd.join(" "),_deferred);
                    return _deferred.promise;
                };

            deactivate()
                .then(function(success)  {
                    response.jmonresponse = success.split('\n');
                    deferred.resolve(response);
                }, function(error) {
                    deferred.reject(response);
                });

            return deferred.promise;
        }
    };


})();

module.exports = jmon;



