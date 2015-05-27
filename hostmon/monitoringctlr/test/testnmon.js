'use strict';

var nmon = require('../js/NmonCommands'),
    should = require('should'),
    chai   = require('chai'),
    expect = chai.expect,
    chaiAsPromised = require("chai-as-promised"),
    log4js  = require('log4js'),
    logger  = log4js.getLogger(),
    util   = require('util'),
    Q      = require('q');

    chai.use(chaiAsPromised);

suite('nmon', function () {
    var _nmon,
        req      = {},
        res      = {};

    test('initialize nmon object', function (done) {
        nmon.should.be.ok;
        done();
    });

    test('status' , function(done) {
       nmon.status('nmon').then(function(success) {
           logger.info("response : ",JSON.stringify(success));
            success.status.should.equal("SUCCESS");
            done();
        },function(error) {
           done(error);
       }).done();

    });

    test('_activate nmon',function(done) {
        var input = {
            frequency : 100,
            samples  : 100,
            graphite : true
        };
        Q.when(nmon.activate(input),function(success) {
            logger.info("success : ",success);

            success.status.should.equal("SUCCESdS");
        },function(error) {
            logger.info("ERROR: ",error);
            done(error);
        });
        done();
    });


    test('deactivate nmon', function(done) {
        this.timeout(5000);
        nmon.deactivate().then(function(success) {
            success.status.should.equal("SUCCESS");
            done();
        },function(error) {
            logger.info("ERROR: ",error);
            done(error);
        }).done();


    })
});
