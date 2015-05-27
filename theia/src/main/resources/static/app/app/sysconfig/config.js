'use strict';

angular.module('myApp.config', ['ngRoute','ui.bootstrap','ngMaterial','duScroll'])

.config(['$routeProvider',function($routeProvider) {
		 
  $routeProvider.when('/systemconfiguration', {
    templateUrl: 'theia/app/app/sysconfig/config.html',
    controller: 'SysConfigCtrl'
  });
}])
.controller('SysConfigCtrl', ['$scope','$route','SysConfigRWSvc','SysConfigUDSvc','SysConfigReadByName',
                              'HostsRead','$mdDialog',
                              '$filter','$log',
                          function($scope,$route,SysConfigRWSvc,SysConfigUDSvc,SysConfigReadByName,
                        		  HostsRead,$mdDialog,$dfilter,$log) {
	
	var setAlert = function(msg) {
		$scope.hidden  = msg.hidden;
		$scope.success = msg.success;
		$scope.info    = msg.info;
		$scope.warning   = msg.warning;
		$scope.danger    = msg.danger;
		$scope.message = msg.message;
	};
	
	var _delete		= function(configuration) {
		$scope.configuration = configuration;
		SysConfigUDSvc.delete({'id' : configuration.id},function() {
			$log.info("Successfully deleted configuration : ",$scope.configuration.name);
			setAlert({
				'hidden' : false,
				'success' : true,
				'message' : 'Host : ' + $scope.configuration.name + ' successfully deleted!'
			});
			var configs = SysConfigRWSvc.query({},function() {
				$scope.configurations = configs;
			});

		},function(error) {
			$log.error("ERROR : ",error);
			setAlert({
				'hidden' : false,
				'danger' : true,
				'message' : 'Host : ' + $scope.configuration.name + ' could not be deleted! '
			});
		});
	};
	
	$scope.getActiveHosts = function() {
		var active = 0,
			inactive = 0;
		
		$scope.hosts.forEach(function(host) {
			if(host.active === true)
				active++;
			else
				inactive++;
		});
		
		$scope.inactive = inactive;
		return active;
		
	}
	
	$scope.refresh = function(host) {
		$route.reload();
	}
	
	$scope.isComponentActive = function(host,component) {
	
		var status = JSON.parse(host.componentstatus);
	
		if(status && component && component.name) {
			return status[component.name];
		} return false;
		
	}
	
	
	$scope.editConfiguration = function(configuration) {
		var editSysConfig = angular.element("#editSysconfigModel");
		editSysConfig.modal('show');
		
		$log.info(configuration.name);
		$scope.selectedconfig = {};
		$scope.selectedconfig.name = configuration.name;
		$scope.selectedconfig.id   = configuration.id;
	}
	
	$scope.editSysConfigSave = function() {
	
		var selectedconfig = $scope.selectedconfig;
		
		if(!selectedconfig.hosts || selectedconfig.hosts.length == 0) {
			setAlert({
				'hidden' : false,
				'warning' : true,
				'message' : 'Cannot edit, no Hosts defined for this configuration'
			});
		} else {
			var configuration = {
					name : selectedconfig.name,
					hosts : []
			};
			
			selectedconfig.hosts.forEach(function(host) {
				configuration.hosts.push(host.hostName);
			})
			
			$log.info(configuration);
			
			SysConfigUDSvc.save({'id':selectedconfig.id,'name':configuration.name,'hosts':configuration.hosts},function() {
				setAlert({
					'hidden' : false,
					'success' : true,
					'message' : 'System Configuration : ' + configuration.name + ' successfully updated!'
				});
				$scope.configurations = SysConfigRWSvc.query();
			});
		}
		
		var editSysConfig = angular.element("#editSysconfigModel");
		editSysConfig.modal('hide');
		
	}
	
	$scope.addSysConfig = function(id) {
		$log.info("Trying to save configuration with name ",$scope.selectedconfig.name);
		$log.info("Trying to save host for configuration : ",$scope.selectedconfig.hosts)
		
		var hostModal = angular.element("#sysconfigModel");
		hostModal.modal('hide');
		
		var request = {
				name : $scope.selectedconfig.name,
				hosts : []
		};
		
		$scope.selectedconfig.hosts.forEach(function(host) {
			request.hosts.push(host.hostName);
		})
		
		
		if(id) {
			$log.info("Update Request with id  : ",id);
			var host = SysConfigUDSvc.save({'id' : id , 'configuration' : request},function() {
				$log.info("Response : ",host);
			})
		} else {
		
			$log.info("Submitting request with payload : ",request);
			var SysConfig = SysConfigRWSvc.save({ 'name' : request.name , 'hosts' : request.hosts},function() {
				$log.info("Successfully created new host : ",request.name);
				setAlert({
					'hidden' : false,
					'success' : true,
					'message' : 'System Configuration : ' + request.name + ' successfully created!'
				});
				$scope.configurations = SysConfigRWSvc.query();
				
			},function(error) {
				$log.error("ERROR : ",error);
				setAlert({
					'hidden' : false,
					'danger' : true,
					'message' : 'System Configuration : ' + request.name + ' could not be created! '
				});
			});
		}
	};
	
	$scope.showConfirm = function(ev,configuration) {
		
	    var confirm = $mdDialog.confirm(configuration)
	    
	      .title('Would you like to delete Configuration ?')
	      .content('Delete Configuration ')
	      .ariaLabel('Delete Configuration')
	      .ok('Please do it!')
	      .cancel('Canceled')
	      .targetEvent(ev);
	    $log.info("Request for host : ",configuration.id);
	    $mdDialog.show(confirm).then(function() {
	    	$log.info("sending request for delete for : ",configuration.id);
	    	_delete(configuration);
	    }, function() {
	      
	    });
	  };
	
	$scope.configurations = SysConfigRWSvc.query();
	$scope.hosts		  = HostsRead.query();

	
}])
.factory('SysConfigReadByName',['$resource','$log',function($resource,$log) {
	var HostRead = $resource('/systemconfiguration/config/name/:name',{'name': '@name'}, {
		'query' : {
			method : 'GET',
			isArray : true
		}
	});
	
	return HostRead;
}])
.factory('SysConfigUDSvc',['$resource','$log',function($resource,$log) {
	var SysConfigRead = $resource('/systemconfiguration/config/:id',{'id' : '@id','name' : '@name','hosts':'@hosts'}, {
		'query' : {
			method : 'GET',
			isArray : true
		} 
	});
	
	return SysConfigRead;
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
.directive('sysconfig',['$q','SysConfigReadByName','$log',function($q,SysConfigReadByName,$log) {
	return {
	  require: 'ngModel',
	  link: function(scope, elm, attrs, ctrl) {
	      ctrl.$asyncValidators.sysconfig = function(modelValue, viewValue) {

	        if (ctrl.$isEmpty(modelValue)) {
	          // consider empty model valid
	          return $q.when();
	        }

	        var def = $q.defer();

	        var sysConfig = SysConfigReadByName.get({'name':modelValue},function() {
	        	if(sysConfig && sysConfig.id) {
	        		def.reject();
	        	} else {
	        		def.resolve();
	        	}
	        	
	        });
	        return def.promise;
	      };
	    }
	  };	
}]);