'use strict';

/**
 *
 * @type {*}
 */
var os                      = require('os'),
    fs                      = require('fs'),
    util                    = require('util');


module.exports = (function () {
    /**
     *
     * PRIVATE
     *
     * @type {number}
     */
    var DELAY       = 5000,
        endPoint    = {
            host: '@GRAPHITEHOST',
            port: 8125,
            debug: true
        },
        nodeapp     = process.cwd().substring(process.cwd().lastIndexOf('/')+1),
        cpuprefix   = [os.hostname(),nodeapp,"cpu"],
        memprefix   = [os.hostname(),nodeapp,"memory"],
        procFile    = "/proc/"+process.pid+"/stat",
        sysFile     = "/proc/stat",
        cpuCount    = os.cpus().length,
        cputime     = {
            utime : 0,
            stime : 0,
            total_time_before : 0
        },
        delta = {
            utime : 0,
            stime : 0,
            total_time_after : 0
        },
        reduce      = function(dataArr,func,result) {
            var i;
            for (i = 0 ; i < dataArr.length; i++) {
                result = func(result,Number(dataArr[i]));
            }
            return result;
        },
        sum         = function(a,b) {
            return a + b;
        },
        getCPU      = function(compute,func) {
            try {
                var processData  = fs.readFileSync(procFile),
                    elems        = processData.toString().split(' '),
                    utime        = parseInt(elems[13],10),
                    stime        = parseInt(elems[14],10),
                    systemData   = fs.readFileSync(sysFile).toString().split('\n'),
                    cpusum       = 0;

                var i;
                for (i = 0 ; i < systemData.length; i++) {
                    if(systemData[i].toString().match(/cpu /)) {
                        var data = systemData[i].split(' ').slice(2);
                        cpusum = reduce(data,sum,cpusum);
                        break;
                    }
                }
                if(cpusum) {
                    if(compute) {
                        var _utime = 100 * (utime - cputime.utime)/ (cpusum - cputime.total_time_before),
                            _stime = 100 * (stime - cputime.stime)/(cpusum - cputime.total_time_before);
                        delta.stime = _stime.toPrecision(2);
                        delta.utime = _utime.toPrecision(2);
                        if(func) {
                            func(delta);
                        }
                        //console.log("(%) utime : " +delta.utime  + " (%) stime : " +delta.stime);
                    } else {

                        cputime.utime = utime;
                        cputime.stime = stime;
                        cputime.total_time_before = cpusum;

                    }

                } else {
                    util.debug("cpustats cannot be empty, no data would be produced");
                    util.debug("cpustats cannot be empty, no data would be produced");
                }
            }
            catch(e) {
                util.debug(e);
                throw new Error(e);
            }
        };



    return {

        /**
         * Public - after import use like mon.memory()
         */

        memory : function (func) {
            var self = this;

            setTimeout(function() {
                    if(func) {
                        func(process.memoryUsage());
                    }
                    self.memory(func);
                },
                DELAY);
        },

        /**
         *
         * Public - after import use like mon.cpu()
         *
         */

        cpu : function(func) {
            setInterval(function() {
                    getCPU();
                    setTimeout(function() {
                        if(func) {
                            getCPU(true,func);
                        }
                        else {
                            getCPU(true);
                        }
                    },1000);
                },
                DELAY);

        }  ,
        graphite : function() {
            var SDC          = require('statsd-client'),
                client          = new SDC(endPoint),
                sendMemoryData  = function(data) {

                    for(var name in data) {
                        if(data.hasOwnProperty(name)) {
                            var prefix = memprefix.join(".");
                            prefix = prefix + "." + name;
                            client.timing(prefix,data[name]);
                        }
                    }
                },
                sendCPUData     = function(data) {
                    for(var name in data) {
                        if(data.hasOwnProperty(name)) {
                            var prefix = cpuprefix.join(".");
                            if(name !== 'total_time_after') {
                                prefix = prefix + "." + name;
                                client.timing(prefix,(data[name]*cpuCount));
                            }
                        }
                    }
                };

            this.cpu(sendCPUData);
            this.memory(sendMemoryData);
        }

    };
})();
