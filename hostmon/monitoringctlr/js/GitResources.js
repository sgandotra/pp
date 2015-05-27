/**
 * Created with JetBrains WebStorm.
 * User: sagandotra
 * Date: 4/10/15
 * Time: 1:59 PM
 * To change this template use File | Settings | File Templates.
 */


var https = require('https'),
    path  = require('path'),
    RemoteExec = require('../js/RemoteExec'),
    git         = require('../routes/git'),
    Q           = require('q'),
    fs          = require('fs'),
    _           = require('underscore'),
    log4js     = require('log4js'),
    logger     = log4js.getLogger('gitResources'),
    sw   = require("swagger-node-express"),
    paramTypes = sw.paramTypes,
    swe  = sw.errors;


var gitResources = (function() {

    return {

        'details' : {
            'spec': {
                "description" : "find performance organization details",
                "path" : "/git/details",
                "notes" : "Returns details of the performance organization",
                "summary" : "Returns details of the performance organization",
                "method": "GET",
                "parameters" : [
                    paramTypes.query("access_token", "oAuth Access Token", "string", true,['f8d8dc2f42e1a5def189bba455b76a27825d6e7d'],'f8d8dc2f42e1a5def189bba455b76a27825d6e7d')
                ],
                "responseMessages" : [],
                "nickname" : "details"
            },
            'action': function (req,res) {
                git.details(req,res);
            }
        },
        'list' : {
            'spec': {
                "description" : "Requires the clone to be done first, after that invoking this api returns all the \
                                 available JMeter scripts on this host machine",
                "path" : "/git/list",
                "notes" : "Requires the clone to be done first, after that invoking this api returns all the \
                                 available JMeter scripts on this host machine",
                "summary" : "Requires the clone to be done first, after that invoking this api returns all the \
                                 available JMeter scripts on this host machine",
                "method": "GET",
                "parameters" : [],
                "responseMessages" : [],
                "nickname" : "details"
            },
            'action': function (req,res) {
                git.list(req,res);
            }
        },
        'clone' : {
            'spec': {
                "description" : "clone the provided gitrepo format 'https://{access_token}@github.paypal.com/{organization_name}/{git_reponame}'",
                "path" : "/git/clone",
                "notes" : "clone the provided gitrepo format 'https://{access_token}@github.paypal.com/{organization_name}/{git_reponame}'",
                "summary" : "clone the provided gitrepo format 'https://{access_token}@github.paypal.com/{organization_name}/{git_reponame}'",
                "method": "GET",
                "parameters" : [
                    paramTypes.query("cloneurl", "the absolute url to clone",
                        "string", true,['https://f8d8dc2f42e1a5def189bba455b76a27825d6e7d@github.paypal.com/performance/lnptestscripts.git'],
                        'https://f8d8dc2f42e1a5def189bba455b76a27825d6e7d@github.paypal.com/performance/lnptestscripts.git')
                ],
                "responseMessages" : [],
                "nickname" : "details"
            },
            'action': function (req,res) {
                git.clone(req,res);
            }
        }


    }

})();

module.exports = gitResources;