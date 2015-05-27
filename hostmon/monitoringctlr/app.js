'use strict';
/**
 * Module dependencies.
 *
 *
 * @Description :
 *
 * <pre>
 * .response       = {
 *       status : SUCCESS|ERROR|INTERNAL_ERROR,
*       timestamp : new Date(),
 *       description : (usually error.toString() for failure)
 *   };
 * </pre>
 *
 * SUCCESS = successful completion
 * ERROR   = Invalid payload , description should give more details
 * INTERNAL_ERROR = something unexpected, internal to the application, probably a bug. See description for details.
 *
 * In all cases an error would a HTTP 500, and SUCCESS should be a HTTP 200 OK.
 *
 */

var express = require('express')
    , url = require("url")
    , app = express()
    , routes = require('./routes')
    , user = require('./routes/user')
    , services = require('./routes/services')
    , servicelist = require('./routes/servicelist')
    , system = require('./routes/system')
    , nmon = require('./routes/nmon')
    , jmon = require('./routes/jmon')
    , nodemon = require('./routes/nodemon')
    , mon  = require('./js/nodemon')
    , http = require('http')
    , path = require('path')
    , log4js = require('log4js')
    , logger = log4js.getLogger('app')
    , errorHandler = require('./middleware/errorhandler')
    , jmeter = require('./routes/jmeter')
    , git    = require('./routes/git')
    , models = require('./js/models')
    , nmonResources = require('./js/NmonResources')
    , jmonResources = require('./js/JmonResources')
    , nodeResources = require('./js/NodeMonResources')
    , gitResources = require('./js/GitResources')
    , jmeterResources = require('./js/JmeterResources')
    , swagger = require("swagger-node-express").createNew(app)

//set logging
log4js.configure({
    appenders: [ { type: 'console' }, { type: 'file', filename: __dirname + '/logs/monitoringctlr.log' ,
        "maxLogSize": 20480, "backups": 3 }],
    "replaceConsole" : true

});
logger.setLevel("debug");

//configure app
app.configure(function() {
    app.set('port', process.env.PORT || 3000);
    app.set('views', __dirname + '/views');
    app.set('view engine', 'jade');
    app.use(log4js.connectLogger(logger, { level: 'auto' }));
    app.use(express.bodyParser());
    app.use(express.methodOverride());
    app.use(express.json());
    app.use(express.urlencoded());
    app.use(app.router);
    //  app.use(express.static(path.join(__dirname, 'public')));
    //  app.use(express.static(path.join(__dirname,'doc')));
});

app.configure('development', function(){
    app.use(errorHandler.defaultHandler);
    app.locals.pretty = true;
    //mon.graphite();

});


//monitoring urls
app.get('/nmon/status',nmon.status);
app.post('/nmon/activate',nmon.activate);
app.get('/nmon/deactivate',nmon.stop);

app.post('/jmon/activate',jmon.activate);
app.get('/jmon/deactivate',jmon.deactivate);
app.get('/jmon/status',jmon.status);

//app.get('/', routes.index);
app.get('/services/list',services.list);
app.post('/services/listtype',servicelist.enumerate);
app.get('/java/statpid/:servicename',services.getpid);
app.get('/appstat/:list',services.appstat);
app.get('/java/servicepids',services.javarestart);
app.get('/java/meminfo',services.meminfo);
app.get('/users', user.list);
app.get('/system/details',system.list);

app.post('/nodemon/activate',nodemon.activate);
app.post('/nodemon/deactivate',nodemon.deactivate);
app.post('/nodemon/status',nodemon.status);

app.post('/jmeter/configure/:action',jmeter.configure);
app.get('/jmeter/data/:id',jmeter.getData);
app.delete('/jmeter/configure/:id',jmeter.delete);
/**
 * jmeter apis
 */
app.get('/git/details',git.details);
app.get('/git/list',git.getList);
app.get('/git/clone',git.clone);

//add swagger support
swagger.addModels(models)
    .addGet(nmonResources.findNmonStatus)
    .addGet(nmonResources.deactivate)
    .addPost(nmonResources.activate)
    .addGet(jmonResources.findJmonStatus)
    .addGet(jmonResources.deactivate)
    .addPost(jmonResources.activate)
    .addPost(nodeResources.findNodeMonStatus)
    .addPost(nodeResources.activate)
    .addPost(nodeResources.deactivate)
    .addGet(gitResources.details)
    .addGet(gitResources.list)
    .addGet(gitResources.clone)
    .addPost(jmeterResources.configurestart)
    .addDelete(jmeterResources.configurestop)
    .addGet(jmeterResources.getStatus);

swagger.configureSwaggerPaths("", "api-docs", "");
swagger.configure("http://stage2lpdt01.qa.paypal.com:3000", "1.0.0");

// Serve up swagger ui at /docs via static route
var docs_handler = express.static(__dirname + '/node_modules/swagger-node-express/swagger-ui/');
app.get(/^\/docs(\/.*)?$/, function(req, res, next) {
    if (req.url === '/docs') { // express static barfs on root url w/o trailing slash
        res.writeHead(302, { 'Location' : req.url + '/' });
        res.end();
        return;
    }
    // take off leading /docs so that connect locates file correctly
    req.url = req.url.substr('/docs'.length);
    return docs_handler(req, res, next);
});

http.createServer(app).listen(app.get('port'), function(){
    logger.info("Express server listening on port " + app.get('port'));
    logger.info("Server started");
});



