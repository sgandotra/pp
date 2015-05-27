'use strict';

var config = require('../js/config'),
    should = require('should'),
    chai   = require('chai'),
    expect = chai.expect,
    chaiAsPromised = require("chai-as-promised"),
    util   = require('util'),
    Q      = require('q');

chai.use(chaiAsPromised);

suite('test config', function () {

    test('initialize config', function (done) {
        config.should.be.ok;
        done();
    });

    test('evaluate config object', function(done) {
        config.load('configuration.yaml').should.be.ok;
        done();
    })

    test('evaluate graphite host', function(done) {
        var doc = config.load('configuration.yaml');
        doc.config.graphitehost.should.eql('stage2lp48.qa.paypal.com');
        done();
    })

    test('getGraphiteHost',function(done) {
        var hostname = config.getGraphitehost('configuration.yaml');
        hostname.should.eql('stage2lp48.qa.paypal.com');
        done();
    })
});
