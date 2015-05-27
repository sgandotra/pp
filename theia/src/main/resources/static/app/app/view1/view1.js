'use strict';

angular.module('myApp.view1', ['ngRoute','ui.bootstrap','ngMaterial','mgcrea.ngStrap','ngAnimate'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/view1', {
    templateUrl: 'theia/app/app/view1/view1.html',
    controller: 'View1Ctrl'
  });
}])

.controller('View1Ctrl', ['SysConfigRWSvc','CheckComponent','ActivateComponents',
                          'DeActivateComponents','$filter','$modal','$timeout',
                          '$scope','$log',function(SysConfigRWSvc,CheckComponent,
                        		  ActivateComponents,DeActivateComponents,$filter,
                        		  $modal,$timeout,$scope,$log) {
	
	var getActiveComponentByHost = function(host) {
		var activeComponents = [];
		if(host) {
			var components = JSON.parse(host.componentstatus) || {};
			for(var c in components) {
				var activeComponent = {
						name : c,
						hostName : host.hostName
				};
				activeComponents.push(activeComponent)
			}
		}
		return activeComponents;
	},
	setActiveComponents		= function(hosts) {
		
		angular.forEach(hosts,function(host) {
			host.monitoredServices = getActiveComponentByHost(host);
		});
		
	},
	getInActiveComponentByHost   = function(host) {
		
		var allComponents = [];
		
		angular.forEach(host.roles,function(role) {
			angular.forEach(role.components,function(component) {
				allComponents.push(component.name);
			});	
		});
		
		var activeComponents = [];
		angular.forEach(host.monitoredServices,function(service) {
			activeComponents.push(service.name);
		});
		
		host.inActiveComponents = _.difference(allComponents,activeComponents);
	},
	setInActiveComponents = function(hosts) {
		
		angular.forEach(hosts,function(host) {
			getInActiveComponentByHost(host);
		});
	},
	manageSpinner		= function(status) {

        var spinner = $scope.configuration.spinner = {};

        spinner.isSpinning = false;
        spinner.isSuccess  = false;
        spinner.isFailure  = false;
		
		if(status === 'success') {
            spinner.isSpinning = false;
            spinner.isSuccess  = true;
            spinner.isFailure  = false;
		} else if (status === 'failure') {
            spinner.isSpinning = false;
            spinner.isSuccess  = false;
            spinner.isFailure  = true;
		} else if (status === 'spinning') {
            spinner.isSpinning = true;
            spinner.isSuccess  = false;
            spinner.isFailure  = false;
		}
		
		
	},
	validateActivationResponse = function() {
		angular.forEach($scope.configurations,function(configuration) {
			
			var activationresponse = $scope.configuration.activationresponse,
				configName		   = activationresponse.configName,
				hosts			   = activationresponse.host;	
			
			if(configuration.name === configName ) {
				
				angular.forEach(hosts,function(host) {
					handleNmonResponse(host);
					handleJmonResponse(host);
					handleNodeMonResponse(host);
				});
				
				configuration.running = true;
			} else {
				//1. handle internal error
			}
		});
	},
	validateDeactivationResponse = function() {
		angular.forEach($scope.configurations,function(configuration) {
			var deactivationresponse = $scope.configuration.deactivationresponse,
				configName			 = deactivationresponse.configName,
				hosts				 = deactivationresponse.hosts;			
			if(configuration.name === configName ) {
				angular.forEach(hosts,function(host) {
					handleNmonResponse(host);
					handleJmonResponse(host);
					handleNodeMonResponse(host);
				});
				
				configuration.running = false;
			} else {
				//internal error
			}
		})
		
	},
	handleNmonResponse = function(host) {
		
		var nmonResponse = host.nmonResponseActivate && host.nmonResponseActivate.nmonStatus;
		
		if($scope.configuration.nmon && nmonResponse) {
			if(nmonResponse.status === 'ACTIVATED') {
				nmonResponse.status = true;
				nmonResponse.tooltip = "System Monitoring started";
			} else if(nmonResponse.status === 'INACTIVE') {
				nmonResponse.status = true;
				nmonResponse.tooltip = "System Monitoring stopped";
			} 
			else {
				nmonResponse.status = false;
				nmonResponse.tooltip = "System Monitoring FAILED!";
			}
			
		} else {
			host.nmonResponseActivate = {};
			host.nmonResponseActivate.status = false;
			host.nmonResponseActivate.tooltip = "No request for nmon";
		}
		
	},
	handleJmonResponse = function(host) {
		
		var status = false,
			message = "",
			unknown = false;
		
		if(host.jmonResponseActivate) {
			//jmon response
			var response = host.jmonResponseActivate.jmonresponse.join(" ");
			if(response.match("JMonitor is already running")) {
				status = false;
				host.jmonResponseActivate.tooltip = "A jmonitor process is already running";
			} else if(response.match("Started JMonitor Processes")) {
				status = true;
				host.jmonResponseActivate.tooltip = "A jmonitor process started successfully";
			} else {
				host.jmonResponseActivate.tooltip = "Unknown error!, check logs";
				unknown = true;
				status = false;
				message = response;
			}
			
			host.jmonresponse = host.jmonResponseActivate.jmonresponse.join(" ");
			
			angular.forEach(host.jmonResponseActivate.services.valid,function(service) {
				service.status = status;
				service.message = response;
				service.status = status;
				service.message = message;
				service.unknown = unknown;
			});
		} else if (host.jmonResponseDeActivate) {
			var response = host.jmonResponseDeActivate.jmonresponse.join(" ");
			if(response.match("Didn't find any JMonitor processes to stop")) {
				host.jmonResponseDeActivate.status = false;
				host.jmonResponseDeActivate.tooltip = "Did not find any jmonitor to stop";
			} else if(response.match("Successfully stopped JMonitor Processes")) {
				host.jmonResponseDeActivate.status = true;
				host.jmonResponseDeActivate.tooltip = "All java monitoring stopped successfully";
			}
		} else {}
	},
	handleNodeMonResponse = function(host) {
		//nodemon response
		var responses = host.nodeMonResponseActivate && 
			host.nodeMonResponseActivate.services &&
            host.nodeMonResponseActivate.services.valid;

		angular.forEach(responses,function(response) {
			
			var result = response.result.join(" ");
			if(result.match("Lock file exists")){
				response.status = false;
				response.tooltip = "The nodejs app is already <br/> being monitored";
			} else if(result.match("Successfully stopped and restarted nodejs Processes")) {
				response.status = true;
				response.tooltip = "The node app successfully restarted";
			} else if(result.match("Started nodemon Processes")) {
				response.status = true;
				response.tooltip = "The node app successfully restarted";
			} else {
				response.status  = false;
				response.unknown = true;
				response.tooltip = "Unknown error while activating of <br/> nodemon agent, please check logs";
			}
				
		});
    },
    getPropertyByHost = function(_hostName,propertyName,propertyValue) {

        var props = {};
        angular.forEach($scope.configuration.hosts,function(_host) {
            if(_hostName && _host.hostName === _hostName 
            		&& _host[propertyName] === propertyValue) {
                return _host[propertyName];
            } else {
            	if( _host[propertyName] === propertyValue) {
            		props[_host.hostName] = _host[propertyName];
            	}
            }

        });

        return props;
    },
    processHosts	= function(configuration) {
    	    	
    			
    		/*configuration.activationresponse.host.push({
				'hostName' : host.hostName
			}); */
    		
    		//Check Status
    		var response = CheckComponent.get({
    							'configName' : configuration.name    							
             	},
             	function() {
			
					configuration.step               = 2;
					configuration.highlightstepthree = true;
					configuration.completion         = "85";
				//	configuration.step3              = response.host;
					
									
				
				if(configuration.toggle) {
					
					angular.forEach(configuration.hosts,function(host) {
						response.hosts[host.hostName].nmon = host.nmon;
						response.hosts[host.hostName].nmonStart = host.nmonstart;
						response.hosts[host.hostName].nmonEnd = host.nmonstart;
					});
					
					var activationresponse = ActivateComponents.save({
							'hosts' : response.hosts,
							'configName' : response.configName
					}, function() {
						configuration.activationresponse = activationresponse;
						validateActivationResponse();
						configuration.completion = "100";
						manageSpinner('success');
					}, function(e) {
						configuration.completion = "100";
						manageSpinner('failure');
						$log.info(e);
					});
					} else {
						var deactivationresponse = DeActivateComponents.save({'configName' : configuration.name,
					                                          'host' : response.host
					                                         },function() {
					    $log.info("Response : ",deactivationresponse);
					    configuration.deactivationresponse = deactivationresponse;
					    validateDeactivationResponse();
					    configuration.completion = "100";
					    manageSpinner("success")
					}, function(e) {
							configuration.completion = "100";
							manageSpinner('failure');
							$log.info(e)
						});
					}
					
					},function(err) {
						$log.info(err);
					
					});
    	
    },
    doBlink		= function(configuration) {
    	
    	return $timeout(function() {
    		configuration.blink = !configuration.blink;
    		doBlink(configuration);
    	},500);
    	
    },
    cancelBlink = function(promise) {
    	$timeout.cancel(promise);
    };
	
	//give steps their name
	 var steps			= ['one','two','three'];
	 
	 //initialize configuration and set current step to 0
	 $scope.configuration = {
			 'step' : 0
	 };
	
	$scope.getCurrentStep = function() {
		var currentStep = $scope.configuration.step;
		$log.info("Getting current step :",$scope.configuration.step);
		return steps[currentStep];
	}


	
	
	$scope.manageComponents = function() {
		
		$timeout(function() {
			processHosts($scope.configuration);
		},2000);
	};
	

	
	$scope.onClose = function() {
		var e = angular.element("#startMonitoring");
		if(e) {
			e.modal('hide');
		}
		
		e = angular.element("#stopMonitoring");
		if(e) {
			e.modal('hide');
		}
		
		$scope.configuration = {};
	};
	

    /**
     * Primary method to enable monitoring for a configuration
     *
     *
     * @param configuration
     */
	$scope.enableMonitoring = function(configuration) {
		
		//configuration on which activation is requested
		$scope.configuration = configuration;
		
		//enable monitoring toggle		
		configuration.toggle 				= 1;
		
		//initialize to first step
		configuration.step  				= 0;
		
		//initial progress bar to 0
		configuration.completion 			= "0";
		
		//initialize model for step 3
		configuration.step3         		= {};
		
		//reset step highlights
		configuration.highlightstepone      = false;
		configuration.highlightsteptwo      = false;
		configuration.highlightstepthree    = false;

	//	$scope.compleleton		     = "1";
		
		
		var e = angular.element("#startMonitoring");
		if(e) {
			e.modal({ 
				'show' : true,
				'background' : 'static'
			});

           $log.info("Starting activation");
			

			manageSpinner('spinning');
			
			//highlight step one
			configuration.highlightstepone      = true;
			configuration.completion 			= "33";
			
			
			
			$timeout(function() {
				//highlight step two
				configuration.step = 1;

				configuration.completion = '40';
				configuration.highlightsteptwo = true;
				configuration.activeCount  = 0;
				
				
                angular.forEach(configuration.hosts,function(host) {
                    if(host.active) {
                        setActiveComponents(configuration.hosts);
                        setInActiveComponents(configuration.hosts);
                        configuration.activeCount++;
                    }

                });
                
                if(configuration.activeCount === 0) {
                	 enableMonitoring.modal('hide');
                     $scope.warning = true;
                     $scope.message="Atleast one host should be active for Activation to continue";
                     return;
                } 

				configuration.completion = '66';
				$scope.manageComponents();
				
				$log.info("Stopping activation");
				
			},5000);
		}
		
		
	};
	
	
	$scope.disableMonitoring = function(configuration) {
		var disableMonitoring = angular.element("#stopMonitoring");
		
		if(disableMonitoring) {

			//configuration on which activation is requested
			$scope.configuration = configuration;
			
			//enable monitoring toggle		
			configuration.toggle		= 0;
			
			//initialize to first step
			configuration.step			= 0;
			
			//initial progress bar to 0
			configuration.completion    = "0";
			
			//initialize model for step 3
			configuration.step3         = {};
			
			//reset step highlights
			configuration.highlightstepone      = false;
			configuration.highlightsteptwo      = false;
			configuration.highlightstepthree    = false;
		//	$scope.compleleton		     = "1";
			
			configuration.toggle = 0;
			
			disableMonitoring.modal('show');
			configuration.step 		 		 = 0;
			configuration.highlightstepone      = true;
			
			manageSpinner('spinning');
			
			//highlight step one
			configuration.highlightstepone      = true;
			configuration.completion 			 = "33";
			
			
			
			$timeout(function() {
				//highlight step two
                configuration.step = 1;


                var props = getPropertyByHost(null,'active',false);
                if(_.size(props) > 0) {
                    $log.info("There are some hosts which are inactive cannot disable");
                    disableMonitoring.modal('hide');
                    $scope.warning = true;
                    $scope.message="Some agent(s) are not responding, please make sure"
                    	+ " to start : " + _.keys(props) + " before continuing";
                    
                    return;
                }
                
                configuration.completion = "40";
                configuration.highlightsteptwo = true;

				setActiveComponents($scope.configuration.hosts);
				setInActiveComponents($scope.configuration.hosts);
                configuration.completion = "66";
				$scope.manageComponents();
			},5000);
		}
	};
	
	$scope.toggleNmon = function(configuration) {
		
		var globalNmon = configuration.nmon;
		
		angular.forEach(configuration.hosts,function(host) {
			host.nmon = globalNmon;
			$scope.setDefaultDate(host);
		})
		
		
	};
	
	$scope.setDefaultDate = function(host,index) {
		var hostNmonStatus = host.nmon;
		
		if(hostNmonStatus) {
			var d 		  = new Date();
			host.duration = new Date(d.getTime() + 86400000);
			$log.info("Duration : " , host.duration);
		} else {
			host.duration = "";
		}
		
	}
	
	
	
	
	$scope.getNmonPercent   = function(host,configuration) {

		var now  = new Date().getTime();
		
		if(angular.isNumber(host.nmonend) && angular.isNumber(host.nmonstart)) {
			
			var remaining = (100.00 - Math.round(((host.nmonend - now) / host.nmonend)*100)) ;
			
			if(remaining > 10 && !configuration.promise) {
				configuration.promise = doBlink(configuration);
				
				remaining = 0;

			} else if(remaining < 10 && configuration.promise){
				cancelBlink(configuration.promise);
			} else {}
				
			
			return remaining;
			
		}
		
	}
	
	$scope.configurations = SysConfigRWSvc.query();
	
	$('[data-toggle="popover"]').popover();
	
}])

.factory('SysConfigRWSvc',['$resource','$log',function($resource,$log) {
	var SysConfigRead = $resource('/theia/systemconfiguration',{'name' : '@name','hosts' : '@hosts'}, {
		'query' : {
			method : 'GET',
			isArray : true
		} 
	});
	
	return SysConfigRead;
}])
.factory('CheckComponent',['$resource','$log',function($resource,$log) {
	var CheckComponent = $resource('/theia/monitoring/:configName',{'configName' : '@config'});
	
	return CheckComponent;
	
}])
.factory('ActivateBatch',['$log',function($log) {
	
}])
.factory('ActivateComponents',['$resource','$log',function($resource,$log) {
	var ActivateComponents = $resource('/theia/monitoring/activate',{'config' : '@response'});
	
	return ActivateComponents;
	
}])
.factory('DeActivateComponents',['$resource','$log',function($resource,$log) {
	var DeActivateComponents = $resource('/theia/monitoring/deactivate',{'config' : '@config'});
	
	return DeActivateComponents;
	
}])
