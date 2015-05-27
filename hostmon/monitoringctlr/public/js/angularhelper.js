/**
 * Created with JetBrains WebStorm.
 * User: sagandotra
 * Date: 9/9/14
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
"use strict";

angular.module('angapp',['ngResource'])
    .controller('timepickerCtlr',['$scope','$log',function($scope,$log) {
       console.log('timepicker');
    }])
    .controller('nmonctlr',['$scope','nmonService','$window','$log',function($scope,nmonService,$window,$log) {
        $scope.dormant = true;
        $scope.stopdormant = true;

        $scope.toggleOnActivate = function(state) {
            if(state) {
                $scope.dormant = false;
                $scope.running = true;
            } else {
                $scope.dormant = true;
                $scope.running = false;
            }
        };

        $scope.pushToNoty = function(type,msg) {
            noty({
                type: type,
                text: msg});

        };

        $scope.activatenmon = function() {
            $log.info("nmonselected date : ",angular.element("#nmonSelector input").val());
            $log.info("nmonsampling rate: ",$scope.nmonSamplingRate);

            $scope.toggleOnActivate(true);

            $scope.nmonSelectedDate  =  angular.element("#nmonSelector input").val();

            if($scope.nmonSelectedDate &&
                $scope.nmonSamplingRate &&
                !isNaN($scope.nmonSamplingRate) &&
                $scope.nmonSamplingRate > 0) {
                $log.info("evaluating input");
                var dates = $scope.nmonSelectedDate.split("-"),
                    month = dates[0],
                    date  = dates[1],
                    year  = dates[2],
                    selectedDate = new Date(year,month,date).getTime(),
                    now           = new Date().getTime(),
                    duration      = parseInt(((selectedDate - now )/1000),10);
                $log.info("activating nmon for : ",duration)
                $scope.pushToNoty("success","Activating Monitoring for : " +duration + " s , with interval : "+$scope.nmonSamplingRate);

                var postData = {
                        "af":
                        {
                            "location" : "/opt/performance-automation-framework",
                            "nmon":
                            {
                                "frequency" : $scope.nmonSamplingRate,
                                "samples"   : duration,
                                "graphite"   : true

                            }
                        }
                };
                $scope.pushToNoty("success","Removing any existing instances");
                var response = nmonService.deactivate({action : 'deactivate'},function(data) {
                    console.log(data);
                    $scope.pushToNoty("success","Successfully removed any existing monitoring..");
                    $scope.pushToNoty("success","Starting monitoring..");

                    nmonService.activate({action : 'activate'},postData,function(data) {
                        console.log(data);
                        $scope.pushToNoty("success","Monitoring started successfully...");
                        $scope.toggleOnActivate(false);
                    });
                },function(error) {
                    $scope.pushToNoty("error","Deactivation failed, cannot continue. Error message : "+error.message);
                    $scope.toggleOnActivate(false);
                })

            } else {
                $window.alert("Invalid input, please select valid values for sampling rate and end date!");
                $scope.toggleOnActivate(false);
                return;
            }


        }

        $scope.deactivatenmon = function() {
            $scope.stopdormant = false;
            $scope.stoprunning = true;

            var response = nmonService.deactivate({action : 'deactivate'},function(data) {
                console.log(data);
                $scope.pushToNoty("success","Successfully removed any existing monitoring..");
                $scope.stopdormant = true;
                $scope.stoprunning = false;
            },function(error) {
                $scope.pushToNoty("error","Deactivation failed, cannot continue. Error message : "+error.message);
                $scope.stopdormant = true;
                $scope.stoprunning = false;
            })
        }
    }])
    .controller('jmonCtlr',['$scope','jmonService','$window','$log',function($scope,jmonService,$window,$log) {
        $scope.pushToNoty = function(type,msg) {
            noty({
                type: type,
                text: msg});

        };

        $scope.toggleJmon = function(state) {
            if(state) {
                $scope.dormant = true;
                $scope.running =false;
            } else {
                $scope.dormant = false;
                $scope.running = true;
            }
        }

        $scope.toggleJmonStop = function(state) {
            if(state) {
                $scope.dormantstop = true;
                $scope.runningstop =false;
            } else {
                $scope.dormantstop = false;
                $scope.runningstop = true;
            }
        }

        $scope.jmonactivate = function() {

            var selected = angular.element("#select2Java").select2('data');
            if(!selected || selected.length == 0) {
                $window.alert("Please select atleast one java service to continue");
                return;
            }
            $scope.toggleJmon(false);
            var serviceNames = [],
                serviceNamesAsText = [];
            angular.forEach(selected,function(val,index) {
                serviceNames.push({
                    name : val.text
                });
                serviceNamesAsText.push(val.text);
            })
            $log.info("selected services : ",serviceNames);
            $scope.pushToNoty("success","Activating services : "+serviceNamesAsText);

            var postData = {
                "af" : "/opt/performance-automation-framework",
                "services": serviceNames
            };

            var response = jmonService.deactivate({action : 'deactivate'},postData, function(data) {
                console.log(data);
                $scope.pushToNoty("success","Successfully removed any existing monitoring..");
                $scope.pushToNoty("success","Starting monitoring..");

                jmonService.activate({action : 'activate'},postData,function(data) {
                    console.log(data);
                    $scope.pushToNoty("success","Monitoring started successfully...");
                    $scope.toggleJmon(true);

                },function(error) {
                    $scope.pushToNoty("error","Activation failed, cannot continue. Error message : "+error.message);
                    $scope.toggleJmon(true);
                });
            },function(error) {
                $scope.pushToNoty("error","Deactivation failed, cannot continue. Error message : "+error.message);
                $scope.toggleJmon(true);
            })

        }

        $scope.jmondeactivate = function() {

            var selected = angular.element("#select2Java").select2('data');

            $scope.toggleJmonStop(false);
            var serviceNames = [{}];


            $scope.pushToNoty("success","Stopping Monitoring services");

            var postData = {
                "af" : "/opt/performance-automation-framework",
                "services": serviceNames
            };

            var response = jmonService.deactivate({action : 'deactivate'},postData, function(data) {
                console.log(data);
                $scope.pushToNoty("success","Successfully removed any existing monitoring..");
                $scope.toggleJmonStop(true);
            },function(error) {
                $scope.pushToNoty("error","Deactivation failed, cannot continue. Error message : "+error.message);
                $scope.toggleJmonStop(true);
            })

        }
        $scope.toggleJmonStop(true);
        $scope.toggleJmon(true);

    }])
    .controller('nodejsCtrl',['$scope','$log',function($scope,$log) {
        $scope.toggleNode = function(state) {
            if(state) {
                $scope.dormant = true;
                $scope.running =false;
            } else {
                $scope.dormant = false;
                $scope.running = true;
            }
        }

        $scope.toggleNodeStop = function(state) {
            if(state) {
                $scope.dormantstop = true;
                $scope.runningstop =false;
            } else {
                $scope.dormantstop = false;
                $scope.runningstop = true;
            }
        }


        $scope.toggleNodeStop(true);
        $scope.toggleNode(true);
    }])
    .controller('listcontroller',['listservice','statPidService','$timeout','$scope','$rootScope',
        function(listservice,statPidService,$timeout,$rootScope,$scope) {
        var getServiceView = function() {

            if($scope.selectJavaOnly) {
                $scope.services = $rootScope._services;
                $scope.services = _.filter($scope.services,function(service) {
                    return service.isJava === true;
                });

                $scope._restarted = _.filter($scope.restarted,function(service) {
                   return service.processName.match(/java/);
                });


            } else {
                $scope._restarted = $scope.restarted;
                $scope.services = $rootScope._services;
            }
        },
        getTimestamp = function(restartedService) {
            var serviceName = restartedService.processName.replace('/','+')
            statPidService.get({folder :serviceName},function(response) {

                $timeout(function() {
                    if(response.data.mtime) {
                        restartedService.timestamp = new Date(response.data.mtime).toLocaleString();
                    } else {
                        restartedService.timestamp = "n/a";
                    }
                });

            });
        },
        compare = function(one,two,mode) {
            _.each(one,function(_service) {
                var _process = _.where(two,{'processName' : _service.processName});

                if(!(_service) || !(_process[0]) || (_service.processName === _process[0].processName &&
                    _service.processid !== _process[0].processid)) {
                    $scope.restarted = $scope.restarted || [];
                    var restartedService = {
                        'processName' : _service.processName,
                        'oldpid' : _service.processid || "N/A",
                        'newpid' : _process[0] && _process[0].processid || "N/A",
                        'timestamp' : ''
                    };
                    getTimestamp(restartedService);
                    $scope.restarted.push(restartedService);
                }
            })
        },
        testPidChanges = function() {
            if($rootScope._services) {
                if($rootScope._services.length >= $scope._services.length) {
                    compare($rootScope._services,$scope._services,1);
                }
                else {
                    compare($scope._services,$rootScope._services,2);
                }
            }
        },
        refresh = function() {
            setTimeout(function() {
                listservice.get(function(response) {
                    $scope._services = response.data;
                    testPidChanges();
                    $rootScope._services = $scope._services;
                    getServiceView();
                });
                refresh();
            },5000);
            var myvalues = [10,8,5,7,4,4,1];
            $('.dynamicsparkline').sparkline(myvalues);
        };




        $scope.$watch('selectJavaOnly',function(oldValue,newValue) {
            getServiceView();
        }) ;

        refresh();

    }])
    .value('ASYNC_ENDPOINT','/java/servicepids')
    .value('ASYNC_STAT_ENDPOINT','/java/statpid')
    .value('NMON_ENDPOINT','/nmon')
    .value('JMON_ENDPOINT','/jmon')
    .factory('listservice',['$resource','ASYNC_ENDPOINT','$log',function($resource,ASYNC_ENDPOINT,$log) {
        var ListService = $resource(ASYNC_ENDPOINT)

        return ListService;
    }])
    .factory('statPidService',['$resource','ASYNC_STAT_ENDPOINT','$log',function($resource,ASYNC_STAT_ENDPOINT,$log) {
        var StatPidService = $resource(ASYNC_STAT_ENDPOINT+"/:folder", {folder : '@folder'});

        return StatPidService;
    }])
    .factory('nmonService',['$resource','NMON_ENDPOINT','$log',
        function($resource,NMON_ENDPOINT,$log) {

            var NmonService = $resource(NMON_ENDPOINT+"/:action",{action : '@action'},{
                'activate' : {
                    method : 'POST',
                },
                'deactivate' : {
                    method : 'GET',
                }
            })

        return NmonService;

    }])
    .factory('jmonService',['$resource','JMON_ENDPOINT','$log',
        function($resource,NMON_ENDPOINT,$log) {

            var JmonService = $resource(NMON_ENDPOINT+"/:action",{action : '@action'},{
                'activate' : {
                    method : 'POST',
                },
                'deactivate' : {
                    method : 'POST',
                }
            })

            return JmonService;

    }]);
