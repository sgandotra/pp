/**
 * Created with JetBrains WebStorm.
 * User: sagandotra
 * Date: 9/9/14
 * Time: 11:44 AM
 * To change this template use File | Settings | File Templates.
 */

"use strict";

var _           = require('underscore'),
    os          = require('os'),
    fs          = require('fs'),
    path        = require('path'),
    log4js      = require('log4js'),
    logger      = log4js.getLogger('servicelist');

exports.enumerate = function(req,res) {

    //get the body
    var serviceList         = req.body || [],
        types               = ["JAVA","NODEJS","UNKNOWN"],
        stagename           = os.hostname().toUpperCase(),
        deploymentFolder    = path.join("/x/web",stagename),
        signatures          = {
            'JAVA_1'          : 'jsw',
            'JAVA_2'          : 'spartaserv',
            'NODEJS'        : 'node_modules'
        };


    _.each(serviceList,function(service) {

        if(service.name) {
            var dirPath =  path.join(deploymentFolder,service.name);
            logger.info("Searching for directory : ",dirPath);
            if(fs.existsSync(dirPath)) {
                service.location = dirPath;
                if(fs.existsSync(path.join(dirPath,signatures['JAVA_1']))
                    || fs.existsSync(path.join(dirPath,signatures['JAVA_2']))) {
                    service.type = types[0];
                } else if(fs.existsSync(path.join(dirPath,signatures['NODEJS']))) {
                    service.type = types[1];
                } else {
                    service.type = types[2];
                }


            } else {
                logger.info("Directory " + dirPath + " not found");
                service.type = types[2];
            }



        }


    });



    res.json(req.body);


}