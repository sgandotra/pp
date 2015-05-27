'use strict';

var jmon = require('../js/JmonCommands'),
    should = require('should'),
    chai   = require('chai'),
    expect = chai.expect,
    chaiAsPromised = require("chai-as-promised"),
    log4js  = require('log4js'),
    logger  = log4js.getLogger('testjmon'),
    util   = require('util'),
    os     = require('os'),
    path   = require('path'),
    Q      = require('q');

chai.use(chaiAsPromised);

suite('jmon', function () {

    test('initialize jmon object', function (done) {
        jmon.should.be.ok;
        done();
    });

    test("is service running", function(done) {
        var service = {
            name : "blah"
        };
        jmon.isServiceRunning(service).then(function(success) {
            success.trim().should.eql("0");
            done();
        },function(error) {
            logger.info("error : ",error);
            done();
        }).done();
    });

    test("is service running", function(done) {
        var service = {
            name : "authorizationserv_ca"
        };

        jmon.isServiceRunning(service).then(function(success) {
            success.trim().should.eql("1");
            done();
        },function(error) {
            logger.info("error : ",error);
            done();
        }).done();
    });

    test("get port number", function(done) {
        var data = "+9,5:bind_port->10785",
            portNumber = jmon.getPortNumber(data);

        portNumber.should.eql("10785");
        done();

    });

    test('get sparta jboss cdbdump', function(done) {
       var service = {
           'name' : 'merchantboardingspartaweb',
           'type' : 'SPARTA_JBOSS_7',
           'serviceDeploymentFolder' : '/x/web/STAGE2LPDT01/marketingspartaweb'
       };

       jmon.getCdbDump(service).then(function(cdbdump) {
           logger.info("cdb dump : ",cdbdump);
           cdbdump.should.match(/RmiRegistryPort/);
           done();
       },function(error) {
          logger.info("error cdbump ", error);
           done();
       }).done();

    });

    test('get invalid sparta jboss cdbdump', function(done) {
        var service = {
            'name' : 'blah',
            'type' : 'SPARTA_JBOSS_7',
            'serviceDeploymentFolder' : '/x/web/STAGE2LPDT01/blah'
        };

        jmon.getCdbDump(service).then(function(cdbdump) {
            logger.info("cdb dump : ",cdbdump);
            done();
        },function(error) {
            logger.info("error cdbdump ", error.message);
            error.message.should.equal("Error: spawn ENOENT");
            done();
        }).done();


    });


    test('get helix jboss cdbdump', function(done) {
        var service = {
            'name' : 'paymentreadserv',
            'type' : 'HELIX_JBOSS_7',
            'serviceDeploymentFolder' : '/x/web/STAGE2LPDT01/paymentreadserv'
        };

        jmon.getCdbDump(service).then(function(cdbdump) {
            logger.info("cdb dump : ",cdbdump);
            cdbdump.should.match(/rmi_naming_port/);
            done();
        },function(error) {
            logger.info("error cdbdump ", error.message);
            done();
        }).done();


    });

    test('get service type (helix)',function(done) {
        var service = {
            'name' : 'paymentreadserv'
            },
            hostname = os.hostname().toUpperCase();

        jmon.checkServiceType(service);

        service.should.have.properties({
            'name' : 'paymentreadserv',
            'serviceDeploymentFolder':path.join('/x/web',hostname,service.name),
            'type' : 'HELIX_JBOSS_7'});
        done();
    });


    test('get service type (sparta)',function(done) {
        var service = {
                'name' : 'merchantboardingspartaweb'
            },
            hostname = os.hostname().toUpperCase();

        jmon.checkServiceType(service);

        service.should.have.properties({
            'name' : 'merchantboardingspartaweb',
            'serviceDeploymentFolder':path.join('/x/web',hostname,service.name),
            'type' : 'SPARTA_JBOSS_7'});
        done();
    });

    test('restart undefined service type', function(done) {
        var service = {
            'name' : 'blah',
            'type' : 'UNDEFINED'
        };

        jmon.restartService(service).then(function(success) {
            logger.info("Success : ",success);
            done();
        },function(error) {
            logger.info("error : " +error.message);
            error.message.should.be.eql("Service Type undefined");
            done();
        }).done();
    });

    test('restart running service', function(done) {
        var service = {
            'name' : 'paymentreadserv',
            'type' : 'HELIX_JBOSS_7'
        };

        jmon.restartService(service).then(function(success) {
            logger.info("Success : "+success);
            service.should.have.properties({
                'name' : 'paymentreadserv',
                'type' : 'HELIX_JBOSS_7',
                'state' : 'isrunning'
            });
            done();
        },function(error) {
            throw new Error(error);
            done();
        }).done();
    })

    test('restart stopped service', function(done) {
        this.timeout(65000);
        var service = {
            'name' : 'paymentreadserv',
            'type' : 'HELIX_JBOSS_7',
            'serviceDeploymentFolder' : '/x/web/STAGE2LPDT01/paymentreadserv'
        };

        jmon.restartService(service).then(function(success) {
            service.should.have.properties({
                'name' : 'paymentreadserv',
                'type' : 'HELIX_JBOSS_7',
                'state' : 'isrunning'
            });
            done();
        },function(error) {
            throw new Error(error);
            done();
        }).done();
    });


    test('restart service with deploy errors', function(done) {
        this.timeout(65000);
        var service = {
            'name' : 'merchantboardingspartaweb',
            'type' : 'SPARTA_JBOSS_7',
            'serviceDeploymentFolder' : '/x/web/STAGE2LPDT01/merchantboardingspartaweb'
        };

        jmon.restartService(service).then(function(success) {
            service.should.have.properties({
                'name' : 'merchantboardingspartaweb',
                'type' : 'SPARTA_JBOSS_7',
                'state' : 'isrunning'
            });
            done();
        },function(error) {
            throw new Error(error);
            done();
        }).done();
    });

    test('verify service',function(done) {

        var  hostname = os.hostname().toUpperCase(),
            service  = {
                'name' : 'paymentreadserv'
            }


        jmon.verifyService(service)
            .should.be.eql('/x/web/'+hostname+'/paymentreadserv')
        done();

    });


    test('verify invalid service',function(done) {

        var hostname = os.hostname().toUpperCase(),
            service  = {
                'name' : 'blah'
            }
        jmon.verifyService(service).should.throw();

        done();

    });


    test('test assembly', function(done) {

        var  hostname = os.hostname().toUpperCase(),
            service  = {
                'name' : 'paymentreadserv'
            };

        jmon.assembleActivationSteps(service);
        done();

    });



});

