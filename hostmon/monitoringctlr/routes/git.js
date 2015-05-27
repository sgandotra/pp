/**
 * Created by sagandotra on 11/3/14.
 */

'use strict';

var https = require('https'),
    path  = require('path'),
    RemoteExec = require('../js/RemoteExec'),
    Q           = require('q'),
    fs          = require('fs'),
    _           = require('underscore'),
    log4js     = require('log4js'),
    logger     = log4js.getLogger('git');


/**
 * Test if a Git URL is valid before cloning
 *
 *
 * @param request
 * @param response
 */
exports.details = function(request,response) {

    var accessToken = request.param('access_token'),
        url         = 'https://github.paypal.com/api/v3/orgs/performance/repos?access_token='+accessToken;

    logger.info("Validating github url : ",url);

    https.get(url,function(resp) {

        logger.info("Get Git Details Status code : ",resp.statusCode);
        logger.info("Get Git Details headers : ",resp.headers);

        var buffer = "";

        resp.on('data',function(d) {
            buffer += d;
        });

        resp.on('error',function(e) {
            response.json(500, e.message);
        });


        resp.on('end',function() {
            response.json(JSON.parse(buffer));
        })

    });
};

/**
 * Get a list of all folder with *.jmx files in the repo folder
 *
 *
 * @param request
 * @param response
 */

exports.getList = function(request,response) {
    var jmeterpath = path.join(__dirname,'..','repo'),
        responseMsg = {
            'error' : '',
            'status': 'SUCCESS',
            'response': [],
            'timestamp' : new Date(),
            'dirCount' : 0
        },
        _response   = [],
        handleError = function(msg,res) {
            responseMsg = {
                'error' : msg,
                'status': 'ERROR',
                'timestamp' : new Date(),
                'dirCount' : 0
            };
            res.json(500,responseMsg);
        },
        handleSuccess = function() {
            responseMsg.dirCount = _.keys(responseMsg.response).length;
            response.json(200,responseMsg);
        },
        readConfigFile = function(filePath) {
            logger.info("Reading config file ",filePath);

            if(fs.existsSync(filePath)) {
                try {
                    var obj =  JSON.parse(fs.readFileSync(filePath));
                    return obj;
                }
                catch(e) {
                    logger.info("Exception caught while reading json object from data file",e);
                }
            }
        },
        getAllJmx = function(parentDir) {
            logger.info("Search parent dir : ",parentDir);
            var files = fs.readdirSync(parentDir),
                dirRep = {};


            logger.info("Iterating through files " ,files);

            _.each(files,function(file,indx) {
                var stats = fs.statSync(path.join(parentDir,file));
                if(file !== '.git') {
                    if(stats.isDirectory()) {
                        var dir = path.join(parentDir,file);
                        getAllJmx(dir);
                    } else {
                        if(indx === 0) {
                            dirRep.dir = parentDir;
                            _response.push(dirRep);
                        }

                        if(file.match(/^.*.jmx$/)) {
                            var currentJmxPath      = path.join(parentDir,file),
                                fileName            = file.split('.')[0],
                                configFile          = fileName + ".json",
                                currentConfigPath   = path.join(parentDir,configFile);

                            dirRep['jmx'] = {
                                'config' : readConfigFile(currentConfigPath),
                                'path'   : currentJmxPath
                            };
                        }

                      /*  if(file.match(/^.*.jmx$/)) {
                            logger.info("adding jmx file : " + currentPath);
                            if(!responseMsg.response[parentDir]) {
                                responseMsg.response[parentDir] = [currentPath];
                            } else {
                                responseMsg.response[parentDir].push(currentPath);
                            }
                        }*/
                    }
                }

            });
        };
    getAllJmx(jmeterpath);

    _.each(_response,function(element) {
        if(_.keys(element).length > 1) {
             responseMsg.response.push(element);
        }
    });

    handleSuccess();

    /*fs.readdir(jmeterpath,function(err,files) {
       if(err) {
           handleError(err.message,response);
           return;
       } else {
           logger.info("files found : ",files);
           responseMsg.dirCount = files.length;
           _.each(files,function(file) {
              var stats = fs.statSync(path.join(jmeterpath,file));
               if(stats.isDirectory()) {
                   var currentPath = path.join(jmeterpath,file),
                       jmxfileList = {'dir' : file,'jmx': [] },
                       _files      = fs.readdirSync(currentPath);
                   _.each(_files,function(_file) {
                    if(_file.match(/^.*.jmx$/)) {
                        logger.info("adding jmx file : " +_file + " to directory : " +file);
                        jmxfileList.jmx.push(_file);
                    }
                   });
                   logger.info("file list : " ,jmxfileList);
                   responseMsg.response.push(jmxfileList);
               }
           });
       }
       handleSuccess(response);
    });*/

};

/**
 *
 * Clone using a valid repo
 *
 * @param request
 * @param response
 */

exports.clone = function(request,response) {
    var cloneUrl        = request.param('cloneurl'),
        execDir         = path.join(__dirname,'..','repo');


    logger.info("clone url: " ,cloneUrl);
    if(!cloneUrl || !cloneUrl.match(/^.*.git$/)) {
        if(!cloneUrl)
            cloneUrl = '';
        response.json(500, {
            'error' : 'Invalid clone url : '+cloneUrl
        });
        return;
    }

    var pathArgs        = cloneUrl.split('/'),
        cloneFolder     = pathArgs[pathArgs.length-1].split('.')[0],
        cloneFolderAbs  = path.join(execDir,cloneFolder),
        remoteExec      = new RemoteExec(),
        gitCmd          = ['git','clone','-q',cloneUrl],
        rmCmd           = ['rm','-rf',cloneFolder],
        finalCmd        = [rmCmd.join(' ')," ;",gitCmd.join(" ")],
        deferred        = Q.defer();

    remoteExec.setExecOpts({
        'cwd' : execDir
    });

    logger.info('received request to clone : ',cloneUrl);
    logger.info("Directory name : ",execDir);

    remoteExec.execCmd(finalCmd.join(" "),deferred)
        .then(function(resp) {
            logger.info("response : ",resp);
            response.json(200,{
                'response' : "success"
            });
        },function(error) {
           logger.info("response : ",error);
            response.json(500,error.message);
        });
};