
var yaml = require('js-yaml'),
    path = require('path'),
    fs   = require('fs'),
    log4js  = require('log4js'),
    logger  = log4js.getLogger('config');


var config = (function() {
    "use strict";

    var folderPath  = path.join(__dirname,'..','conf');

    return {
        load : function(fileName) {
            try {
                var filePath = path.join(folderPath,fileName);
                var doc = yaml.safeLoad(fs.readFileSync(filePath,'utf8'));
                logger.debug(doc);
                return doc;
            } catch(e) {
                logger.error(e);
            }
        },
        getGraphitehost : function(fileName) {
            try {
                var doc = this.load(fileName);
                return doc.config.graphitehost;
            } catch(e) {
                logger.error(e);
            }
        }


    };

})();

module.exports=config;