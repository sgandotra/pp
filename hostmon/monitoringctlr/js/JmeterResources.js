/**
 * Created with JetBrains WebStorm.
 * User: sagandotra
 * Date: 4/16/15
 * Time: 11:04 AM
 * To change this template use File | Settings | File Templates.
 */


var log4js          = require('log4js'),
    logger          = log4js.getLogger('jmeterResources'),
    jmeterManager   = require('../routes/jmeter'),
    sw              = require("swagger-node-express"),
    paramTypes      = sw.paramTypes,
    swe             = sw.errors;


var jmeterResources = (function() {
    return {
        configurestart : {
            'spec': {
                "description" : "configure jmeter for a test run",
                "path" : "/jmeter/configure/start",
                "notes" : "Start JMeter instance with a particular configuration",
                "summary" : "Start JMeter instance with a particular configuration",
                "method": "POST",
                "parameters" : [ {
                    "name" : "body",
                    "description" : "activate jmon",
                    "type" : "Jmeter",
                    "required" : true,
                    "paramType" : "body"
                }],
                "type" : "Jmeter",
                "responseMessages" : [],
                "nickname" : "configureJMeterStart"
            },
            'action': function (req,res) {
                jmeterManager.configure(req,res);
            }
        },
        configurestop : {
            'spec': {
                "description" : "configure jmeter for stop test run",
                "path" : "/jmeter/configure/{pid}",
                "notes" : "Stop JMeter instance with a particular configuration",
                "summary" : "Stop JMeter instance with a particular configuration",
                "method": "GET",
                "parameters" : [ paramTypes.path("pid", "ID of jmeter that needs to be stopped", "string") ],
                "type" : "path",
                "responseMessages" : [],
                "nickname" : "configureJmeterStop"
            },
            'action': function (req,res) {
                jmeterManager.configure(req,res);
            }
        },
        getStatus : {
            'spec': {
                "description" : "get jmeter data for test run",
                    "path" : "/jmeter/data/{pid}",
                    "notes" : "Get JMeter data for a particular pid",
                    "summary" : "Get JMeter data for a particular pid",
                    "method": "GET",
                    "parameters" : [ paramTypes.path("pid", "ID of jmeter for which we need to fetch data", "string") ],
                    "type" : "path",
                    "responseMessages" : [],
                    "nickname" : "getStatus"
            },
            'action': function (req,res) {
                jmeterManager.configure(req,res);
            }
        }
    };

})();


module.exports = jmeterResources;