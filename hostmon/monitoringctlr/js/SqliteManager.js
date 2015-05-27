/**
 * Created by sagandotra on 12/4/14.
 */
var fs      = require('fs'),
    path    = require('path'),
    dbname  = "hostmon.db",
    file    = path.join(process.cwd(),"monitoringctlr","database",dbname),
    _sqlite = require("sqlite3").verbose(),
    util    = require('util'),
    log4js  = require('log4js'),
    logger  = log4js.getLogger('sqlite'),
    Q       = require('q');

Q.longStackSupport = true;

var sqlite = (function() {
    var response = {
        status : "",
        data : "",
        err : ""
        };

    return {
        handleSuccess : function(info) {
            return response = {
                status : "SUCCESS",
                data : info,
                err : ""
            };
        },
        handleError : function(err) {
            return response = {
                status : "ERROR",
                data : "",
                err : err.message
            };
        },
        select : function(type) {
            if(!type) {
                throw new TypeError("type cannot be null or undefined");
            }
            var defer = Q.defer(),
            db        = new _sqlite.Database(file),
            sql       = "SELECT * from hostmon WHERE type = $type";
            logger.info("Using db file : " + file);
            logger.info("Executing sql : ",sql + " with type : " +type);

            db.all(sql,{
                $type : type
            },function(err,rows) {
                if(err) {
                    defer.reject(err);
                }
                defer.resolve(rows);
            });

            return defer.promise;

        },
        update : function(args) {
            if(!args.type || typeof(args.started) === 'undefined' || typeof(args.ended) === 'undefined' || !args.status) {
                logger.info("args : ",JSON.stringify(args));
                throw new TypeError("input arguments not correct");
            }

            var defer = Q.defer(),
                db    = new _sqlite.Database(file),
                sql   = "UPDATE hostmon SET started = $started, ended = $ended, status = $status"
                        + " WHERE type = $type";

            db.run(sql,{
                $started : args.started,
                $ended   : args.ended,
                $status  : args.status,
                $type    : args.type
            },function(err) {
                if(err) {
                    defer.reject(err);
                }
                defer.resolve(this);
            });

            var promise = defer.promise;
            promise.fin(function() {
                db.close();
            });

            return promise;
        },
        insertAsync : function(args) {
            var db      = new _sqlite.Database(file),
                sql     = "INSERT into hostmon (type,servicename,state,started,ended)"
                          + " values ( $type,$servicename,$state,$started,$ended )";
        }
    }
})();



module.exports = sqlite;