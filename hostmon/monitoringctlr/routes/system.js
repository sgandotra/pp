
/*handle request to provide system details*/

var os   = require('os');

exports.list = function(req,res) {
    res.set('Content-Type','application/json');
    res.json({
        "hostname" : os.hostname(),
        "type"     : os.type(),
        "platform" : os.platform(),
        "arch"     : os.arch(),
        "uptime"   : os.uptime(),
        "loadavg"  : os.loadavg(),
        "memory"   : {
            "totalmem" : os.totalmem(),
            "freemem"  : os.freemem()
        },
        "cpus"     : os.cpus(),
        "networkInterfaces" : os.networkInterfaces(),
        "lastupdated_utc" : Date.now()
    });
}