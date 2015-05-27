/**
 * Created by sagandotra on 4/29/14.
 */


'use strict'

var exec            = require('child_process').exec,
    spawn           = require('child_process').spawn,
    Q               = require('q'),
    _               = require('underscore'),
    util            = require('util'),
    log4js          = require('log4js'),
    logger          = log4js.getLogger('exec');

/**
 *
 * @constructor
 *
 * @param {object} req - request object
 * @param {object} res - response Object
 *
 */
var RemoteExec = function(req) {
    this.opts  = {},
    this.activateopts = {
        samples : null,
        frequency : null,
        graphite : null
    }
}

/**
 *
 * Default values
 *
 * @type {{cwd: string}}
 */
RemoteExec.DEFAULTS = {
    activate : {
        samples : 8640,
        frequency : 10,
        graphite : true
    }
}




RemoteExec.prototype.setExecOpts = function(opts) {
    this.opts = _.defaults(opts,RemoteExec.DEFAULTS);
}

/**
 *
 * @returns {opts} - return exec options
 */
RemoteExec.prototype.getExecOpts = function() {
    return this.opts;
}


/**
 *
 * @param {string} cmd
 * @returns {Error} {*}
 * @returns {object} deferred.promise
 */
RemoteExec.prototype.execCmd  = function(cmd,deferred) {
    var self = this;
    if(cmd) {
        logger.info("Received [exec] for " +cmd);
        var child = exec(cmd,this.opts,function(error,text,stderr) {
            if(error !== null) {
                logger.info("[ERROR] cmd : " +cmd + " - '" +error+"'");
                deferred.reject(new Error(error));
            } else {
                logger.info("[SUCCESS] cmd : " +cmd + " - " +text);
                deferred.resolve(text);
            }
        });

    }
    return deferred.promise;
}

RemoteExec.prototype.spawnCmd = function(cmd) {
    logger.info("Running cmd :",cmd);

    var self     = this,
        deferred = Q.defer(),
        response = "",
        run  = spawn(cmd.shift(),cmd,this.opts);

    run.stdout.on('data', function (data) {
        response += data;
    });

    run.stderr.on('data', function (data) {
        response += data;
    });

    run.on('exit', function (code) {
        logger.info('child process exited with code ' + code);
        deferred.resolve(response);
    });
    return deferred.promise;
}

RemoteExec.prototype._spawnCmd = function(cmd,deferred,checkStderr) {
    var self = this,
        response = "",
        _cmd    = cmd.shift();

    logger.info("Running command : " + _cmd +" with options : " + cmd);
    var run  = spawn(_cmd,cmd,this.opts);

    run.stdout.on('data', function (data) {
        response += data;
    });

    run.stderr.on('data', function (data) {
       if(checkStderr)
            deferred.reject(data.toString());
    });

    run.on('exit', function (code) {
        logger.info('child process exited with code ' + code);
        deferred.resolve(response);
    });
}

module.exports = RemoteExec