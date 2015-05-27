'use strict';

angular.module('myApp.jmeter', ['ngRoute','ui.select','ngSanitize'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/jmeter', {
    templateUrl: 'theia/app/app/jmeter/jmeter.html',
    controller: 'JmeterCtrl'
  });
}])

.controller('JmeterCtrl',['$scope','$rootScope','testSvc','lgSvc','smSvc',
                          'gitCloneSvc','gitListSvc','jmeterRequestSvc','jmeterConsoleDataSvc','systemConfigSvc','$timeout','$log',
                          '$mdDialog',
                          function($scope,$rootScope,testSvc,lgSvc,smSvc,gitCloneSvc,gitListSvc,
                        		  jmeterRequestSvc,jmeterConsoleDataSvc,systemConfigSvc,$timeout,$log,$mdDialog) {
	$log.info("Loading jmeter controller");
	
	$scope.model = {};
	$scope.msg   = {
			'hidden' : true
	};
	$scope.testExecution = {};
	
	var cloneTracker = {},
		getJmxFileName = function(fullPath) {

		if(!fullPath || fullPath.length == 0)
			return
		
		var pathValues = fullPath.split('/'),
			fileName   = pathValues.pop(),
			parentFolder = pathValues.pop();
			return parentFolder + " / " + fileName;
		},
		setAlert = function(msg) {
			$scope.msg.hidden  = msg.hidden;
			$scope.msg.success = msg.success;
			$scope.msg.info    = msg.info;
			$scope.msg.warning   = msg.warning;
			$scope.msg.danger    = msg.danger;
			$scope.msg.message = msg.message;
		},
		setDangerAlert = function(msg) {
			setAlert({
				'hidden' : false,
				'danger' : true,
				'success' :  false,
				'info'    : false,
				'warning' : false,
				'message' : msg
			});
		},
		setSuccessAlert = function(msg) {
			setAlert({
				'hidden' : false,
				'danger' : false,
				'success' :  true,
				'info'    : false,
				'warning' : false,
				'message' : msg
			});
		},
		readJmeterStats	= function(testId) {
			
			var func    = function() {
					jmeterConsoleDataSvc.get({'tid': testId},function(success) {
						$log.info("Console data for test id : " + testId + " is : ",success);
						if(success.testResult) {
							$scope.metrics = {
								status : success.testResult.status,
								date : success.dateModified,
								errors : success.errors,
								latency : success.latency,
								responsetime : success.responsetime,
								runningthreads : success.runningthreads,
								samples : success.samples
							};
							
							dataSet_1.push([$scope.metrics.samples, $scope.metrics.errors]);							
							graph_1.setData([plotData_1]);
							graph_1.setupGrid();
							graph_1.draw();
							
							dataSet_2.push([$scope.metrics.samples, $scope.metrics.latency]);
							graph_2.setData([plotData_2]);
							graph_2.setupGrid();
							graph_2.draw();
							
							dataSet_3.push([$scope.metrics.samples, $scope.metrics.responsetime]);
							graph_3.setData([plotData_3]);
							graph_3.setupGrid();
							graph_3.draw();
							
							dataSet_4.push([$scope.metrics.samples, $scope.metrics.runningthreads]);
							graph_4.setData([plotData_4]);
							graph_4.setupGrid();
							graph_4.draw();
						
						} else {
							$scope.metrics = {
									status : 'WAITING'
							}
						}
					},function(error) {
						$log.error(error);
						setDangerAlert("Error in getting real time data from the server");
					});
					
					if(!$scope.metrics || $scope.metrics.status !== 'COMPLETED') {
						return $timeout(function() {
							return func();
						},10000);
						
						
					} else {
						alert("Test completed successfully!");
					}
			};
			
			
			$scope.promiseForTimeout = func();
			
		};
	
	
	
	$scope.selectedLgEvent = function(item,model) {
		
		$scope.model.lg = item;

		var now = new Date().getTime(),
			threshold = 5 * 60 * 1000;
		if(!cloneTracker[$scope.model.lg.lgname] && cloneTracker[$scope.model.lg.lgname] < now -  threshold) {
			//clone
			gitCloneSvc.get({'lgname' : $scope.model.lg.lgname},function(response) {
				$log.info('git clone successfully completed on ',$scope.model.lg.lgname)
			});
			//update timestamp
			cloneTracker[$scope.model.lg.lgname] = new Date().getTime();
		}
		//query and get the list of scripts
		gitListSvc.get({'lgname' : $scope.model.lg.lgname},function(gitList) {
			$log.info('git list successfully completed on ',$scope.model.lg.lgname);
			$scope.gitList = gitList.response;
			
			angular.forEach($scope.gitList,function(item) {
				item.jmx.formattedName = getJmxFileName(item.jmx.path);
			});
			;
		});
				
	}
	
	
	$scope.showLgPreview = function(lg) {
		
		if(lg.inuse || !lg.status) {
			return "Load Generator machine inuse or unavailable";
		}
	}
	
	$scope.showSmPreview = function(sm) {
		
		if(sm.inuse || !sm.status) {
			return "User Stage inuse or unavailable";
		}
	}	
	
	$scope.startJmeter = function() {
		
		$log.info("Starting jmeter and monitoring");
		
		
		
		var model = $scope.model;
				
		//collect input data
		var request = {
				"sut" : { 
					"stageName" : model.sm.stagename
				},
				"jmeter" : {
					"machineName" : model.lg.lgname,
					"config" : {
						"scriptName" : model.gitlist.jmx.path,
						"vusers"  : model.gitlist.jmx.config.vusers,
						"duration" : model.gitlist.jmx.config.duration,
						"params" : model.commandlineparams,
						"starttime" : new Date()						
					}
				},
				"description" : model.description,
				"corpId"      : $rootScope.corpId,
				"systemConfiguration" : model.sc.name
		}
		
		$log.info("Submitting request : ",request);
		
		//submit json request
		jmeterRequestSvc.save(request,function(payload) {
			$log.info(payload);
			if(payload.response.status === "SUCCESS") {
				angular.element("#intermediateModal").modal('hide');
				//handle success
				setSuccessAlert("Test Execution successfully started, " +
						"you can check the quick look report below or a " +
						"more comprehensive real time report here ");
				
				$log.info("Test id : ",payload.testId);
				$scope.testExecution = payload;
				$scope.testExecution.testId = payload.testId;
				readJmeterStats(payload.testId);
				$scope.toggle = !$scope.toggle;
				
			} else {
				angular.element("#intermediateModal").modal('hide');
				//handle error
				setDangerAlert("Uh-oh something went wrong during the activation. " +
						"The server says : [" + payload.response.message + "]");
			}
			
		}, function(error) {
			$log.error(error);
			setDangerAlert("Uh-oh something went wrong during the activation. " +
					"The server says : [" + error.status + "," + error.statusText + "]");
			angular.element("#intermediateModal").modal('hide');
	
		});				
	};
	
	$scope.stopTest = function(tid) {
		$log.info("Recieved request to stop test with tid : ",tid);
		jmeterRequestSvc.delete({'tid' : tid},function(success) {
			setSuccessAlert("Test Execution stopped successfully");
			
		}, function(error) {
			$log.error(error);
		})		
	};
	
	
	lgSvc.query({
		'query' : 'all'
	}, function(response) {
		$scope.lgs = response;
	});
	
	smSvc.query({
		'query' : 'all'
	}, function(response) {
		$scope.sms = response;
	});
	
	systemConfigSvc.query({},function(response) {
		$scope.scs = response;
	});
	
	$rootScope.corpId = angular.element("#corpid").text();
	
	angular.element("#bootstrap-wizard-1").bootstrapWizard({
		'tabClass' : 'form-wizard',
		'onNext'   : function(tab,navigation,index) {
			var model = $scope.model;
			if(index == 1) {
				if(!model.sm) {
					setDangerAlert('User Stage Machine cannot be empty');					
					return false;
				} else if(!model.lg) {
					setDangerAlert('Load Generator machine cannot be empty');					
					return false;
				}
				setAlert({'hidden' : true});
			} else if( index == 2 ) {
				if(!model.gitlist) {
					setDangerAlert('Jmeter script cannot be empty');					
					return false;
				}
				else if(!model.gitlist.jmx.config.duration || isNaN(model.gitlist.jmx.config.duration)) {
					setDangerAlert('Duration is a required field, should be a numeric value');
					return false;
				}
				else if(!model.gitlist.jmx.config.vusers || isNaN(model.gitlist.jmx.config.vusers)) {
					setDangerAlert('Vusers is a required field, should be a numeric value');
					return false;
				} else if(!model.description) {
					setDangerAlert('Description is a required field');					
					return false;
				}
				setAlert({'hidden' : true});				
			} else if ( index == 3) {
				if(!model.sc) {
					setDangerAlert('Description is a required field');					
					return false;
				}
			}
		},
		'onTabShow' : function(tab, navigation,index) {
			
			var $total = navigation.find('li').length,
				$current = index+1;
			
			if($current >= $total) {
				$('#bootstrap-wizard-1').find('.pager .next').hide();
				$('#bootstrap-wizard-1').find('.pager .finish').show();
				$('#bootstrap-wizard-1').find('.pager .finish').removeClass('disabled');
			} else {
				$('#bootstrap-wizard-1').find('.pager .next').show();
				$('#bootstrap-wizard-1').find('.pager .finish').hide();
			}
			
		}
	});
	
	//initialize graphs
	var options = {
			lines: {
				show: true
			},
			points: {
				show: true
			},
			series : {
				lines : {
					lineWidth : 1,
					fill : true,
					fillColor : {
						colors : [{
							opacity : 0.4
						}, {
							opacity : 0
						}]
					},
					steps : false,
					yaxis : {
						min : 0.0,
						tickLength:0
					},
				    xaxis: {tickLength:0}
				}
			}
		},
		dataSet_1 = [],
		dataSet_2 = [],
		dataSet_3 = [],
		dataSet_4 = [],//initialize
		plotData_1 = { label: "Error(s)", data:  dataSet_1 },
		plotData_2 = { label: "Latency(s)", data:  dataSet_2 },
		plotData_3 = { label: "Response Time(s)", data:  dataSet_3 },
		plotData_4 = { label: "Threads", data:  dataSet_4 };
		
		var graph_1 = $.plot("#graph_1", [plotData_1], options);
		var graph_2 = $.plot("#graph_2", [plotData_2], options);
		var graph_3 = $.plot("#graph_3", [plotData_3], options);
		var graph_4 = $.plot("#graph_4", [plotData_4], options);
	
}])
.factory('lgSvc',['$resource','$log',function($resource,$log) {
	var lgSvc = $resource('/theia/loadgenerators',{'query' : 'all'}, 
			{
			'query' : {
				method : 'GET',
				isArray : true
			} 
	});
	return lgSvc;
}])
.factory('smSvc',['$resource','$log',function($resource,$log) {
	var smSvc = $resource('/theia/sut',{'query' : 'all'}, 
			{
			'query' : {
				method : 'GET',
				isArray : true
			} 
	});
	return smSvc;
}])
.factory('gitCloneSvc',['$resource','$log',function($resource,$log) {
	var gitCloneSvc = $resource('/theia/git/clone/:lgname',{'lgname' : '@lgname'});
	
	return gitCloneSvc;
}])
.factory('gitListSvc',['$resource','$log',function($resource,$log) {
	var gitListSvc = $resource('/theia/git/:lgname/list',{'lgname' : '@lgname'});
	
	return gitListSvc;
}])
.factory('jmeterRequestSvc',['$resource','$log',function($resource,$log) {
	var jmeterRequestSvc = $resource('/theia/jmeter/:tid',{'tid' : '@tid', 'request' : '@request'});
	
	return jmeterRequestSvc;
}])
.factory('jmeterConsoleDataSvc',['$resource','$log',function($resource,$log) {
	var jmeterConsoleDataSvc = $resource('/theia/jmeter/console/data/:tid',{'tid' : '@tid'});
	
	return jmeterConsoleDataSvc;
}])
.factory('systemConfigSvc',['$resource','$log',function($resource,$log) {
	var systemConfigSvc = $resource('/theia/systemconfiguration',{} ,
			{
			'query' : {
				method : 'GET',
				isArray : true
			}
			});
	
	return systemConfigSvc;
}])
.directive('flotChart',function() {
	return {
		restrict : 'E',
		templateUrl : 'chart.html'
	}
})
