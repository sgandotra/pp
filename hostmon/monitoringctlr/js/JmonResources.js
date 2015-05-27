/**
 * Created with JetBrains WebStorm.
 * User: sagandotra
 * Date: 4/8/15
 * Time: 11:50 AM
 * To change this template use File | Settings | File Templates.
 */



var jmon = require('./JmonCommands'),
    sw   = require("swagger-node-express"),
    paramTypes = sw.paramTypes,
    swe  = sw.errors;


var jmonResources = (function() {

    var handleResponse = function(res,msg) {
        if(msg.status === 'SUCCESS') {
            res.json(200,msg);
        } else {
            res.json(500,msg);
        }


    };


    return {

        findJmonStatus : {
            'spec': {
                "description" : "find jmon status",
                    "path" : "/jmon/status",
                    "notes" : "Returns jmon status",
                    "summary" : "Find jmon status",
                    "method": "GET",
                    "parameters" : [],
                    "responseMessages" : [],
                    "nickname" : "findJmonStatus"
            },
            'action': function (req,res) {
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
                });

            }
        },
        deactivate : {
            'spec': {
                "description" : "deactivate",
                "path" : "/jmon/deactivate",
                "notes" : "Deactivate Jmon",
                "summary" : "Use to deactivate Jmon",
                "method": "GET",
                "parameters" : [],
                "responseMessages" : [],
                "nickname" : "deactivateJmon"
            },
            'action': function (req,res) {
                jmon.deactivate().then(function(response) {
                    console.log("response : ",response);
                    response.timestamp = new Date();
                    res.json(200,response);
                },function(error) {
                    res.json(500,error);
                });

            }
        },
        activate : {
            'spec': {
                "description" : "activate",
                "path" : "/jmon/activate",
                "notes" : "Activate Jmon",
                "summary" : "Use to Activate Jmon",
                "method": "POST",
                "parameters" : [ {
                    "name" : "body",
                    "description" : "activate jmon",
                    "type" : "Jmon",
                    "required" : true,
                    "paramType" : "body"
                }],
                responseMessages : [swe.invalid('input')],
                "nickname" : "activateJmon"
            },
            'action': function (req,res) {

                console.log(">>> " , req.body);

                var services = req.body.services;
                jmon.activate(services).then(function(response) {
                    response.timestamp = new Date();
                    res.json(200,response);
                },function(error) {
                    res.json(500,error);
                });

            }
        }



    }



})();

module.exports=jmonResources;