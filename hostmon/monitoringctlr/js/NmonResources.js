/**
 * Created with JetBrains WebStorm.
 * User: sagandotra
 * Date: 4/7/15
 * Time: 1:57 PM
 * To change this template use File | Settings | File Templates.
 */

var nmon = require('./NmonCommands'),
    sw   = require("swagger-node-express"),
    paramTypes = sw.paramTypes,
    swe  = sw.errors;


var nmonResources = (function() {

    var handleResponse = function(res,msg) {
        if(msg.status === 'SUCCESS') {
            res.json(200,msg);
        } else {
            res.json(500,msg);
        }


    };

    return {
        findNmonStatus : {
            'spec': {
                "description" : "find nmon status",
                "path" : "/nmon/status",
                "notes" : "Returns nmon status",
                "summary" : "Find nmon status",
                "method": "GET",
                "parameters" : [],
                "type" : "Nmon",
                "responseMessages" : [],
                "nickname" : "findNmonStatus"
            },
            'action': function (req,res) {
                nmon.status().then(function(success) {
                        handleResponse(res,success);
                    },function(error) {
                        handleResponse(res,error);
                    }).done();

            }
        },
        deactivate : {
            'spec': {
                "description" : "deactivate nmon",
                "path" : "/nmon/deactivate",
                "notes" : "Deactivates Nmon",
                "summary" : "Can be used to deactivate nmon",
                "method": "GET",
                "parameters" : [],
                "type" : "Nmon",
                "responseMessages" : [],
                "nickname" : "deactivateNmon"
            },
            'action': function (req,res) {
                nmon.deactivate().then(function(success) {
                    handleResponse(res,success);
                },function(error) {
                    handleResponse(res,error);
                }).done();

            }
        },
        activate : {
            'spec': {
                "description" : "Activate nmon",
                "path" : "/nmon/activate",
                "notes" : "activate Nmon",
                "summary" : "Can be used to activate nmon",
                "method": "POST",
                "parameters" : [paramTypes.body("body", "Nmon request to activate resource monitoring", "nmon",null,true)],
                "type" : "Nmon",
                responseMessages : [swe.invalid('input')],
                "nickname" : "activateNmon"
            },
            'action': function (req,res) {
                console.log(req.body);
                var _freq      = req.body.nmon && req.body.nmon.frequency,
                    _samples    = req.body.nmon && req.body.nmon.samples,
                    _graphite   = req.body.nmon && req.body.nmon.graphite,
                    input       = {
                        frequency : _freq,
                        samples : _samples,
                        graphite : _graphite
                    };



                if(typeof req.body === 'undefined' || typeof req.body.nmon === 'undefined'){
                    throw swe.invalid('Nmon', res);
                } else {

                    Q.when(nmon.activate(input),function(success) {
                        return nmon.status().then(function(nmonStatus) {
                            handleResponse(res,nmonStatus);
                        },function(err) {
                            handleResponse(res,err)
                        });
                    }).done();
                }


            }
        }

    }

})();

module.exports = nmonResources;