
'use strict';

var xmljs   = require('libxmljs'),
    fs      = require('fs'),
    path    = require('path'),
    _       = require('underscore'),
    log4js  = require('log4js'),
    logger  = log4js.getLogger('xml');

/**
 * @param templateLocation - location of the template file which would be used for creating the actual Jmon input
 * @param graphiteHost - graphite host from configuration
 *
 * @constructor
 */
var ManageXmlTemplate = function(templateLocation,graphiteHost) {
    if(!fs.existsSync(templateLocation)) {
        new TypeError("Invalid template location");
    }
    this.templateLocation               = templateLocation;
    this.template                       = path.join(templateLocation,"template.xml");
    this.templatedoc                    = null;
    this.hostname                       = "localhost";//os.hostname().toUpperCase();
    this.graphiteHost                   = graphiteHost || "stage2lp48.qa.paypal.com";
    this.init();

    ManageXmlTemplate.DEFAULTS.conf.MetricsCollectorAddress = this.graphiteHost + ":2003";
    logger.info("Using graphite host : ",ManageXmlTemplate.DEFAULTS.conf.MetricsCollectorAddress );
};

/**
 * Defaults.supportedJmonTemplates - current supported type
 * @type {{supportedJMonTemplates: Array}}
 */

ManageXmlTemplate.DEFAULTS = {
    conf : {
        AppPort                : "1999",
        Username               : "system",
        Password               : "manager",
        ConnectTimeout         : "3000",
        MetricsPingInterval    : "5000",
        MetricsEndPointType    : "Graphite",
        MetricsCollectorAddress: "stage2lp48.qa.paypal.com:2003"
    },
    jmxconf :  {
        geronimo        : "service:jmx:rmi:///jndi/rmi://STAGE_PORT/JMXConnector",
        jboss           : "service:jmx:remoting-jmx://"
    },
    JMXServiceUrl       : "JMXServiceURL"
};


/**
 *
 * initialization - this is where we are loading the XML template file based on
 * location and file type provided
 *
 */
ManageXmlTemplate.prototype.init = function() {

    try {
        //read xml cp file
        this.templatedoc = xmljs.parseXmlString(fs.readFileSync(this.template));
    }
    catch(e) {
        throw new Error("Error in reading file : " +e);
    }
};

/**
 * List of services which need to be appended of the specific type
 *
 * <pre>
 *     <code>
 *          &lt;JMXAgentConfig&gt;
 * &lt;AppName&gt;helixtestapiserv&lt;/AppName&gt;
 * &lt;AppPort&gt;1999&lt;/AppPort&gt;
 * &lt;!-- use this format for Jboss based application --&gt;
 * &lt;JMXServiceURL&gt;service:jmx:remoting-jmx://@STAGE:10364&lt;/JMXServiceURL&gt;

 * &lt;!-- use this format for Geronimo based applications --&gt;
 * &lt;!-- &lt;JMXServiceURL&gt;service:jmx:rmi:///jndi/rmi://@STAGE:10781/JMXConnector&lt;/JMXServiceURL&gt; --&gt;

 * &lt;Username&gt;system&lt;/Username&gt;
 * &lt;Password&gt;manager&lt;/Password&gt;
 * &lt;ConnectTimeout&gt;3000&lt;/ConnectTimeout&gt;
 * &lt;MetricsPingInterval&gt;5000&lt;/MetricsPingInterval&gt;
 * &lt;MetricsEndPointType&gt;Graphite&lt;/MetricsEndPointType&gt;
 * &lt;!--&lt;MetricsOutputFileName&gt;/x/opt/performance-automation-framework/Tools/jmonitor/output/helixrestapi-metrics.txt&lt;/MetricsOutputFileName&gt;--&gt;
 * &lt;MetricsCollectorAddress&gt;stage2lp48.qa.paypal.com:2003&lt;/MetricsCollectorAddress&gt;
 * &lt;/JMXAgentConfig&gt;
 *
 *     </code>
 * </pre>
 *
 *
 * @param {object} services
 */

ManageXmlTemplate.prototype.genTemplate = function(services) {
    try {

     //filter out jboss

        if(services !== null && services.length > 0) {
            this.append(this.templatedoc,services);

            logger.info("fragment : " +this.templatedoc.toString() + "\n");
            this.writeToDisk(this.templatedoc.toString());
        }
    } catch(e) {
        throw new Error(e);
    }

};

ManageXmlTemplate.prototype.append = function(parentDoc,services) {
    var self            = this,
        parentElement   = parentDoc.get('//JMXAgentConfigs');

    _.each(services,function(service) {
        logger.info("Adding port number : " +service.name);

        var root = new xmljs.Document().node('JMXAgentConfig');
        //add default properties
        _.each(ManageXmlTemplate.DEFAULTS.conf,function(value,name) {
            root.node(name,value);
        });
        //add appname
        root.node("AppName",service.name);

        if(service.type === 'HELIX_JBOSS_7' || service.type === 'SPARTA_JBOSS_7') {
            root.node(ManageXmlTemplate.DEFAULTS.JMXServiceUrl,
                ManageXmlTemplate.DEFAULTS.jmxconf.jboss + self.hostname + ":"+service.port);
        } else if (service.type === 'SPARTA_GERONIMO' || service.type === 'HELIX_GERONIMO') {
            root.node(ManageXmlTemplate.DEFAULTS.JMXServiceUrl,self.getSpartaGeronimoUrl(service.port));
        } else {
            throw new TypeError("Unknown type : " + service.type);
        }
        parentElement.addChild(root);
    });
};

/**
 *
 *
 * @param port
 * @returns {string}
 */

ManageXmlTemplate.prototype.getSpartaGeronimoUrl = function(port) {
    return ManageXmlTemplate.DEFAULTS.jmxconf.geronimo.replace(/STAGE_PORT/,this.hostname + ":" + port);
};

/**
 *
 * @param port
 * @returns {string}
 */

ManageXmlTemplate.prototype.getHelixGeronimoUrl = function(port) {
    return ManageXmlTemplate.DEFAULTS.jmxconf.geronimo.replace(/STAGE_PORT/,this.hostname + ":" + port);
};


/**
 *
 * creates a file from the updated template
 *
 */
ManageXmlTemplate.prototype.writeToDisk = function(payload) {
    var fileName = path.join(this.templateLocation,"JMX.xml");
    logger.warn("Writing to file " +fileName);

    fs.writeFileSync(fileName,payload);
};



module.exports = ManageXmlTemplate;
