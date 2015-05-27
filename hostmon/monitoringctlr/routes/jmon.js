'use strict'

/**
 *
 * @type {*}
 */
var os                      = require('os'),
    _                       = require('underscore'),
    path                    = require('path'),
    fs                      = require('fs'),
    util                    = require('util'),
    RemoteExec              = require('../js/RemoteExec'),
    ManageXmlTemplate       = require('../js/ManageXmlTemplate'),
    Q                       = require('q'),
    StageHelper             = require('../js/StageHelper'),
    jmon                    = require('../js/JmonCommands');


/**
 *
 * @description:
 *  Deactivate jmon on the host machine
 *
 *
 * @param req
 * @param res
 */
exports.deactivate  = function(req,res) {

    jmon.deactivate().then(function(response) {
        console.log("response : ",response);
        response.timestamp = new Date();
        res.json(200,response);
    },function(error) {
        res.json(500,error);
    });



};

/**
 *
 * @description:
 *  Get the current status of JMonitor
 *
 * @param req
 * @param res
 */

exports.status = function(req,res) {

    jmon.status().then(function(response) {
        res.json(200,{
            timestamp : new Date(),
            message   : "lock file found, an instance of JMonitor is running"
        });
    },function(err) {
        if(err.errno === 34) {
            res.json(200, {
                timestamp : new Date(),
                message   : "No instance of JMonitor found running"
            });
        }
        res.json(500,err);
    })

}



/**
 *
 * <p>
 * request to activate  jmonitor for a set of services
 * The input payload consists of the following
 * </p>
 *
 * <pre>
 *     <code>
 *         {
 *              "services" : [
 *                  {
 *                      "name" : "disputeresolutionspartaweb", //required
 *                  },
 *                  {
 *                      "name" : "alphacreditservicespartaweb",
 *                  }
 *              ]
 *         }
 *      </code>
 * </pre>
 *
 *
 * @param req
 * @param res
 * @af {string} - the location of the automation framework
 * @services {array} - a list of services which need to be enabled for jmonitor
 * @type {string} - type of service from { "HELIX_JBOSS_7 | SPARTA_JBOSS_7 | SPARTA_GERONIMO_7" }
 *
 *
 */
exports.activate=function(req,res) {

    var services = req.body.services;
    jmon.activate(services).then(function(response) {
        response.timestamp = new Date();
        res.json(200,response);
    },function(error) {
        res.json(500,error);
    });

};