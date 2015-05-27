'use strict'

/**
 *
 * @type {*}
 */

var util                 = require('util'),
    nodemon              = require('../js/NodeMonCommands');


exports.activate = function(req,res) {

    var services = req.body.services;
    nodemon.activate(services).then(function(response) {
        response.timestamp = new Date();
        response.status   = "SUCCESS";
        res.json(200,response);
    },function(error) {
        response.status   = "ERROR";
        response.timestamp = new Date();
        res.json(500,error);
    });
};


exports.deactivate = function(req,res) {
    var services = req.body.services;
    nodemon.deActivate(services).then(function(response) {
        response.timestamp = new Date();
        response.status   = "SUCCESS";
        res.json(200,response);
    },function(error) {
        response.status   = "ERROR";
        response.timestamp = new Date();
        res.json(500,error);
    });
};


exports.status = function(req,res) {
    var services = req.body.services;
    nodemon.status(services).then(function(response) {
        response.timestamp = new Date();
        response.status   = "SUCCESS";
        res.json(200,response);
    },function(error) {
        response.status   = "ERROR";
        response.timestamp = new Date();
        res.json(500,error);
    });
};