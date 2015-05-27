/**
 * Created with JetBrains WebStorm.
 * User: sagandotra
 * Date: 4/13/15
 * Time: 1:24 PM
 * To change this template use File | Settings | File Templates.
 */

'use strict';

var jmeterManager = require('../js/JmeterManager'),
    should = require('should'),
    chai   = require('chai'),
    expect = chai.expect,
    chaiAsPromised = require("chai-as-promised"),
    log4js  = require('log4js'),
    logger  = log4js.getLogger('testjmeter'),
    util   = require('util'),
    os     = require('os'),
    path   = require('path'),
    Q      = require('q');

chai.use(chaiAsPromised);

suite('jmeterManager',function() {
    //get status
    test('initialize jmeter object', function (done) {
        jmeterManager.should.be.ok;
        done();

    });

    test('get status for non-existing pid', function(done) {
        expect(jmeterManager.getStatus(1234)).to.be.an('undefined');
        done();
    });

    test('get status for null pid', function(done) {
        expect(jmeterManager.getStatus(null)).to.be.an('undefined');
        done();
    });

    test('get status for invalid datatype pid', function(done) {
        expect(jmeterManager.getStatus('1234')).to.be.an('undefined');
        done();
    });

    //put status
    test('pid is null', function(done) {
       jmeterManager.putStatus(null,'status');
       expect(jmeterManager.getStatus(1234)).to.be.an('undefined');
       done();
    });

    test('pid is not a number', function(done) {
        jmeterManager.putStatus('abc','status');
        expect(jmeterManager.getStatus('abc')).to.be.an('undefined');
        done();
    });

    test('valid pid', function(done) {
        jmeterManager.putStatus(1234,'status');
        expect(jmeterManager.getStatus(1234).status).to.eql('status');
        done();
    });

    //delete
    test('pid is null', function(done) {
        expect(jmeterManager.delete(null)).to.eql('pid not found');
        done();
    });

    test('pid not found', function(done) {
        expect(jmeterManager.delete(11111)).to.eql('pid not found');
        done();
    });

    test('valid pid found', function(done) {
        jmeterManager.putStatus(1234,'status');
        expect(jmeterManager.delete(1234)).to.eql('jmeter test run with execution id 1234 stopped');
        done();
    });

    //start
    test('start jmeter with no config or cmd', function(done) {
        expect(jmeterManager.start).to.throw('Invalid configuration or cmd');
        done();

    });

    test('start jmeter with config and no cmd', function(done) {
        expect(jmeterManager.start.bind({})).to.throw('Invalid configuration or cmd');
        done();

    });

    test('start jmeter with no config and and a cmd', function(done) {
        expect(jmeterManager.start.bind(null,{})).to.throw('Invalid configuration or cmd');
        done();

    }) ;


    test('start jmeter with valid config and script' , function(done) {
       this.timeout(0);

       var script = '/x/home/sagandotra/hostmon/monitoringctlr/repo/lnptestscripts/test/home_paypal.jmx',
           config = {
               'vusers' : '-Dvusers=20',
               'duration' : '-Dduration=3600',
               'stagename1' : '-Dstagename1=abc'
           };

       jmeterManager.start(config,script);
            jmeterManager.run().then( function(response){
                response.executionID.should.not.be.empty;
            });

    });

    //stop
    test('stop jmeter with null executionID', function(done) {
       expect(jmeterManager.delete(null)).to.eql('pid not found');
    });

    test('stop jmeter with invalid executionID', function(done) {
        expect(jmeterManager.delete(88338)).to.eql('pid not found');
    });

    test('stop jmeter with valid executionID', function(done) {

        this.timeout(0);

        var script = '/x/home/sagandotra/hostmon/monitoringctlr/repo/lnptestscripts/test/home_paypal.jmx',
            config = {
                'vusers' : '-Dvusers=20',
                'duration' : '-Dduration=3600',
                'stagename1' : '-Dstagename1=abc'
            };

        jmeterManager.start(config,script);
        jmeterManager.run().then( function(response) {
            var pid = response.executionID;
            expect(jmeterManager.delete(pid)).to.eql('jmeter test run with execution id ' + pid + 'stopped');
        });
    });
});