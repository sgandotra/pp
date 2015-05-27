/**
 * Created by sagandotra on 11/4/14.
 */

'use strict';

var log4js          = require('log4js'),
    logger          = log4js.getLogger('jmeter_route'),
    jmeterManager   = require('../js/JmeterManager');

exports.configure = function(req,res) {

    var action = req.param('action'),
        script    = req.body.script,
        config = req.body.config,
        responseMsg = {
            'status' : '',
            'timestamp' : '',
             'error' : ''
        };
    logger.info("Received request to : " + action + " jmeter");
    if(!action && (action !== 'start' || action !== 'stop')) {

        responseMsg.status = "ERROR";
        responseMsg.timestamp = new Date();
        responseMsg.error = " Action invalid or undefined : " +action;

        res.json(500,responseMsg);
    }

    if( script && (action === 'start' || action === 'stop')) {
        try {
            logger.info("start jmeter with  script : " ,script);

            jmeterManager.start(config,script);
            jmeterManager.run().then(function(response) {
                if(response)
                    res.json(200,response);
                else
                    res.json(500,{
                        "status" : "ERROR",
                        "msg"    : "no response received from the scheduler",
                        "timestamp" : new Date()
                    });
            },function(response) {
                res.json(500,{
                    "status" : "ERROR",
                    "msg"    : response.message,
                    "timestamp" : new Date()
                });
            });

        } catch(err) {
            logger.info("Error in executing jmeter, error : ",err);
            res.json(500,{
                "status" : "ERROR",
                "msg"    : err.message,
                "timestamp" : new Date()
            });
        }

    } else  {
        responseMsg.status = "ERROR";
        responseMsg.timestamp = new Date();
        responseMsg.error = " Input invalid or undefined";

        res.json(500,responseMsg);
    }
};

exports.delete = function(req,res) {
    var pid = req.param('id');

    var response = jmeterManager.delete(pid);

    res.json(200, {
        "response" : "SUCCESS",
        "msg"       : response
    });

}


exports.getData = function(req,res) {
    var pid = req.param('id');

    if(pid) {
        var status = jmeterManager.getStatus(pid);
        if(status) {
            res.json(200,{
                'status' : 'SUCCESS',
                'timestamp' : new Date(),
                'data'      : status
            });
        } else {
            res.json(500,{
                "status" : "error",
                "message" : " pid does not exist",
                "timestamp" : new Date(),
                'data' : {}
            });
        }
    }

    res.json(500, {
            "status" : "error",
            "timestamp" : new Date()
    });
};