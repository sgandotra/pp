/**
 * Created by sagandotra on 12/4/14.
 */
var Sqlite = require('../js/SqliteManager'),
    should = require('should'),
    chai   = require('chai'),
    expect = chai.expect,
    chaiAsPromised = require("chai-as-promised"),
    log4js  = require('log4js'),
    logger  = log4js.getLogger(),
    util   = require('util');

chai.use(chaiAsPromised);

suite('sqlite', function () {
    test('initialize sqlite',function(done) {
        var sqlite = new Sqlite();
        sqlite.should.be.ok;
        sqlite.close();
        done();
    });

    test('test valid type', function(done) {
        var sqlite = new Sqlite();
        sqlite.select('nmon').then(function(success) {
            logger.info(success);
            success.status.should.be.eql('SUCCESS');
            success.data[0].type.should.be.eql('nmon');
            success.data.length.should.equal(1);
            done();
        }, function(err) {

            done();
        }).done();
    });

    test('test update', function(done) {
        var sqlite = new Sqlite(),
            start  = new Date().getTime(),
            end    = new Date().getTime();

        sqlite.update('nmon',start,end);

        sqlite.select('nmon').then(function(success) {
            logger.info(success);
            success.status.should.be.eql('SUCCESS');
            success.data[0].type.should.be.eql('nmon');
            success.data[0].started.should.be.eql(start);
            success.data[0].ended.should.be.eql(end);
            done();
        }, function(err) {

            done();
        }).done();
    });


    test('test with values')

});