'use strict'


var Q = require('q');
/**
 * import child process module
 * @type {exec}
 */

var nmon    = require('../js/NmonCommands');

/**
 *
 *  @11/19 - sagandotra
 *
 *   SUPPORTED METHOD - GET
 *   updating the interface, now there is no need to specify the location of
 *   the installation.
 *
 *  --
 * <strike>
 * <ul> Handles <strong>/nmon/status</strong> requests
 *
 * We are handling the status requests in two ways
 * <li>
 *     <ul> if it's a HTTP GET request the automation framework location on the host machine is assumed to be
 *      <strong>"location": "/opt/performance-automation"</strong></ul>
 *     <ul> if it's a HTTP POST Request the location is picked up from req.body.af.location property.
 *      <span style="color:red"><strong> an incorrect location would throw an error</strong></span></ul>
 * </li>
 *
 *  </strike>
 */
exports.status = function(req,res) {

    nmon.status().then(function(success) {
        handleResponse(res,success);
    },function(error) {
        handleResponse(res,error);
    }).done();
};


/**
 *
 *  SUPPORTED METHOD - GET
 * Update , there is no need to provide the location of this package.
 *
 * @param {object} req - request object
 * @param {object} res - response object
 *
 * Handles <strong>/nmon/activate?frequency=x&graphite=true|false</strong> requests
 *
 *
 *
 * <strike>
 * We are handling the activate request in two ways
 * <ul>
 *     <li> For a HTTP GET Request, by default frequency is 10s, samples is 8640 (1D) and graphite is turned ON,
 *     the default location of automation framework is /opt/performance-automation-framework
 *     <li> For HTTP POST Request, all of the below fields can be used to override default values
 * </ul>
 *
 * </strike>
 */
exports.activate = function(req,res) {
     var _freq      = req.body.nmon && req.body.nmon.frequency,
        _samples    = req.body.nmon && req.body.nmon.samples,
        _graphite   = req.body.nmon && req.body.nmon.graphite,
        input       = {
            frequency : _freq,
            samples : _samples,
            graphite : _graphite
        };



    Q.when(nmon.activate(input),function(success) {
        return nmon.status().then(function(nmonStatus) {
            handleResponse(res,nmonStatus);
        },function(err) {
            handleResponse(res,err)
        });
    }).done();


};


/**
 * SUPPORTED METHOD - GET
 *
 * The replacement would be just a simple get request that kills the vmstat process
 *
 * <strike>
 * Handles <strong>/nmon/deactivate</strong> requests and kills all existing nmon and parsible instances
 *
 * We are handling the status requests in two ways
 * <li>
 *     <ul> if it's a HTTP GET request the automation framework location on the host machine is assumed to be
 *      <strong>"location": "/opt/performance-automation"</strong></ul>
 *     <ul> if it's a HTTP POST Request the location is picked up from req.body.af.location property.
 *      <span style="color:red"><strong> an incorrect location would throw an error</strong></span></ul>
 * </li>
 *
 *
 *
 * @param {object} req - request object
 * @param {object} res - response object
 *
 * @param {object} req.body -
 *                           <pre><code>
 *                           {
 *                              "af":
 *                                  {
 *                                      "location": "/opt/performance-automation-framework"
 *                                  }
 *                            }
 *                            </code></pre>
 *
 * </strike>
 */

exports.stop = function(req,res) {
    nmon.deactivate()
        .then(function(success) {
            return nmon.status().then(function(nmonStatus) {
                handleResponse(res,nmonStatus);
            },function(err) {
                handleResponse(res,err)
            })
        }).done();

};


var handleResponse = function(res,msg) {
    if(msg.status === 'SUCCESS') {
        res.json(200,msg);
    } else {
        res.json(500,msg);
    }


}
