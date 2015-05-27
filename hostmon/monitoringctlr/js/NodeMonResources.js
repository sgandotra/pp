/**
 * Created with JetBrains WebStorm.
 * User: sagandotra
 * Date: 4/10/15
 * Time: 1:03 PM
 * To change this template use File | Settings | File Templates.
 */


var nodemon    = require('../routes/nmon'),
    sw   = require("swagger-node-express"),
    paramTypes = sw.paramTypes,
    swe  = sw.errors;


var nodeResources = (function() {
   "use strict";

    var handleResponse = function(res,msg) {
        if(msg.status === 'SUCCESS') {
            res.json(200,msg);
        } else {
            res.json(500,msg);
        }


    };

    return {
        findNodeMonStatus : {
            'spec': {
                "description" : "find node mon status",
                "path" : "/nodemon/status",
                "notes" : "Returns nodemon status",
                "summary" : "Find nodemon status",
                "method": "POST",
                "parameters" : [ {
                    "name" : "body",
                    "description" : "activate nodeMon",
                    "type" : "NodeMon",
                    "required" : true,
                    "paramType" : "body"
                }],
                responseMessages : [swe.invalid('input')],
                "type" : "NodeMon",
                "nickname" : "findNodeMonStatus"
            },
            'action': function (req,res) {
                nodemon.status(req,res);

            }
        },
        activate : {
            'spec': {
                "description" : "activate node mon",
                "path" : "/nodemon/activate",
                "notes" : "activate nodemon for a set of services",
                "summary" : "activate nodemon for a set of services",
                "method": "POST",
                "parameters" : [ {
                    "name" : "body",
                    "description" : "activate nodeMon",
                    "type" : "NodeMon",
                    "required" : true,
                    "paramType" : "body"
                }],
                responseMessages : [swe.invalid('input')],
                "type" : "NodeMon",
                "nickname" : "activateNodemon"
            },
            'action': function (req,res) {

               nodemon.activate(req,res);
            }
        },
        deactivate : {
            'spec': {
                "description" : "deactivate node mon",
                "path" : "/nodemon/deactivate",
                "notes" : "deactivate nodemon for a set of services",
                "summary" : "deactivate nodemon for a set of services",
                "method": "POST",
                "parameters" : [ {
                    "name" : "body",
                    "description" : "deactivate nodeMon",
                    "type" : "NodeMon",
                    "required" : true,
                    "paramType" : "body"
                }],
                responseMessages : [swe.invalid('input')],
                "type" : "NodeMon",
                "nickname" : "deactivateNodemon"
            },
            'action': function (req,res) {
                nodemon.deactivate(req,res);

            }
        }
    }

})();


module.exports=nodeResources;