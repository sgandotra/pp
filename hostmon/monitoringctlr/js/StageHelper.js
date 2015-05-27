/**
 * Created with JetBrains WebStorm.
 * User: sagandotra
 * Date: 5/13/14
 * Time: 11:25 AM
 * To change this template use File | Settings | File Templates.
 */

var path        = require('path'),
    Q           = require('q'),
    RemoteExec  = require('./RemoteExec'),
    fileSystem  = require('fs'),
    util        = require('util'),
    os          = require('os'),
    log4js      = require('log4js'),
    logger      = log4js.getLogger('helper');

helper = (function() {

    var defaults = {
        restartScriptLoc : "/x/tools/common/mystart"
    },
    deployFolder,
    hostname;



    return {
        restartService : function(serviceDeploymentFolder,deferred) {

            if(serviceDeploymentFolder && fileSystem.existsSync(serviceDeploymentFolder)) {
                var remoteExec = new RemoteExec(),
                    cmd  = ["export","TERM=vt100",";",
                            "cd",serviceDeploymentFolder,";",
                            "./shutdown.sh",";",defaults.restartScriptLoc + " ./start.sh"
                    ],
                    opts =  {
                        cwd : serviceDeploymentFolder  ,
                        maxBuffer : 1024*200
                    },
                    serviceName = serviceDeploymentFolder.substring(serviceDeploymentFolder.lastIndexOf('/'));

                remoteExec.setExecOpts(opts);

                logger.info("Restarting service : " + serviceName + " with cmd " + cmd.join(" "));
                remoteExec._spawnCmd([cmd.join(" ")],deferred);
                return deferred.promise;
            };
            throw new Error("Folder : " +serviceDeploymentFolder + " does not exist!");


        },
        getDeploymentFolder : function() {
            var hostname            = os.hostname().toUpperCase(),
                deployFolderStage2  = path.join('/x/web',hostname),
                deployFolderC3      = '/x/web/LIVE3';

            try {
                if(fileSystem.existsSync(deployFolderStage2)) {
                   logger.info("Deploy Folder :",deployFolderStage2);
                   return deployFolderStage2;
                }

            } catch(e) {}


            try {
                if(fileSystem.existsSync(deployFolderC3)) {
                    logger.info("Deploy Folder :",deployFolderStage2);
                    return deployFolderStage2;
                }
            } catch (e) {}

            throw new Error("Deployment folder : " + deployFolder + " does not exist");
        },
        verifyService        :  function(servicename) {
            var absServicePath;
            util.log("Received a request to verify : " +servicename);
            if(servicename && servicename.length > 0) {
                if(!deployFolder) {
                    this.getDeploymentFolder();
                }
                absServicePath  = path.join(deployFolder,servicename);
                if(fileSystem.existsSync(absServicePath)) {
                    util.print("Service found at :" + absServicePath + "\n");
                    return absServicePath;
                }
            }
            console.log("Service name at location : " +absServicePath + " does not exist");
            throw new Error("Service name at location : " +absServicePath + " does not exist");
        },
        isServiceRunning  : function(servicename,servicetype,deferred) {
            var remoteExec = new RemoteExec(),
                cmd = ['ps','-ef' ,'|','grep','-i',servicename,'|','grep',servicetype, '|','grep','-v','grep','|','wc','-l'];
                remoteExec.execCmd(cmd.join(" "),deferred);
        },
        execCmd       : function(_cwd,cmd,deferred) {
            var remoteExec = new RemoteExec(),
                opts = {
                    cwd : _cwd  ,
                    maxBuffer : 1024*200
                };
            remoteExec.setExecOpts(opts);
            remoteExec.execCmd(cmd,deferred);
        },
        spawnCmd  : function(_cwd,cmd,deferred) {

            var remoteExec = new RemoteExec(),
                opts = {
                    cwd : _cwd  ,
                    maxBuffer : 1024*200
                };
            remoteExec.setExecOpts(opts);
            return remoteExec._spawnCmd(cmd.split(" "),deferred);
        }

    };


})();


module.exports=helper;


