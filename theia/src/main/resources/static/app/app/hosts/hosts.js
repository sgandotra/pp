'use strict';

angular.module('myApp.hosts', ['ngRoute','ngMaterial','duScroll'])

.config(['$routeProvider',function($routeProvider) {
		 
  $routeProvider.when('/hosts', {
    templateUrl: 'theia/app/app/hosts/hosts.html',
    controller: 'HostsCtrl'
  });
}])
.run(['$anchorScroll', function($anchorScroll) {
 // $anchorScroll.yOffset = -30;   // always scroll by 50 extra pixels
}])
.controller('HostsCtrl', ['$scope','HostsRead','HostRead','HostUpdate','RolesRead','$mdDialog','$anchorScroll',
                          '$location','$filter','$log',
                          function($scope,HostsRead,HostRead,HostUpdate,RolesRead,$mdDialog,$anchorScroll,
                        		  $location,$filter,$log) {
	
	var setAlert = function(msg) {
		$scope.hidden  = msg.hidden;
		$scope.success = msg.success;
		$scope.info    = msg.info;
		$scope.warning   = msg.warning;
		$scope.danger    = msg.danger;
		$scope.message = msg.message;
	};
	
	var _delete		= function(host) {
		$scope.host = host;
		HostRead.delete({'name' : host.id},function() {
			$log.info("Successfully deleted host : ",host);
			setAlert({
				'hidden' : false,
				'success' : true,
				'message' : 'Host : ' + host.hostName + ' successfully deleted!'
			});
			var hosts = HostsRead.query({},function() {
				$scope.hosts = hosts;
			});
		},function(error) {
			$log.error("ERROR : ",error);
			setAlert({
				'hidden' : false,
				'danger' : true,
				'message' : 'Host : ' + host.hostName + ' could not be deleted! '
			});
		});
	},
	groupHostsByRoles = function(hosts) {
		
		var roles = {};
		
		angular.forEach(hosts,function(host) {
			var _roles = host.roles;
			
			angular.forEach(_roles,function(role) {
				var roleName = role.name;
				if(!roles[roleName]) {
					roles[roleName] = [host];
				} else {
					var value = roles[roleName];
					value.push(host);
				}
			});
			
		});
		
		return roles;
	};
	
	
	
	$scope.formatMem = function(mem) {
		if(!mem) return 'N/A';
		
		return Math.round(mem/1024/1024/1024);
	}
	
	$scope.formatLoadAvg = function(loadAvg) {
		var formattedVal = [];
		if(!loadAvg) return 'N/A';
		JSON.parse(loadAvg).forEach(function(val) {
			formattedVal.push(val.toPrecision(2));
		});
		
		return formattedVal;
		
	}
	
	$scope.scrollTo = function(hostname) {
	      $location.hash(hostname);
	      $anchorScroll();
	};
	
	$scope.editHost = function(_host) {
		$log.info("editing host : ",_host.id);
		var hostModal = angular.element("#edithostModel");
		hostModal.modal('show');
		
		$scope.selectedhost = {};
		$scope.selectedhost.name = _host.hostName;
		$scope.selectedhost.id   = _host.id;
		
	};
	
	$scope.editHostSave = function() {
		
		var hostModal = angular.element("#edithostModel");
		hostModal.modal('hide');
		
		if(!$scope.selectedhost 
				|| !$scope.selectedhost.myroles 
				|| !$scope.selectedhost.myroles.length === 0) {
			setAlert({
				'hidden' : false,
				'danger' : true,
				'message' : 'Roles cannot be empty'
			});
			return;
		}
		
		var host = {
				name : $scope.selectedhost.name,
				roles : []
		};
		
		$scope.selectedhost.myroles.forEach(function(myrole) {
			host.roles.push(myrole.name);
		});
		
		$log.info(host);
		HostUpdate.save({'id' : $scope.selectedhost.id , 'name' : host.name,'roles' : host.roles},function() {
			var hosts = HostsRead.query({},function() {
				$scope.hosts = hosts;
			});
			setAlert({
				'hidden' : false,
				'success' : true,
				'message' : 'Host : ' + host.name + ' successfully updated!'
			});
		},function(err) {
			$log.error("ERROR : ",error);
			setAlert({
				'hidden' : false,
				'danger' : true,
				'message' : 'host : ' + host.name + ' could not be updated! '
			});
			
		});
		
	}
	
	$scope.getUptime = function(time) {
	
		var date = new Date().getTime()/1000,
			uptime = Math.round(date -time);
		
		return new Date(uptime*1000).toLocaleString().replace('"','');
		
	};
	
	$scope.getTime = function(time) {
		return new Date(time).toLocaleString().replace('"','');
	}
	
	$scope.refresh = function(host) {
		HostRead.get({'name' : host.hostName},function(_host) {
			$log.info(" Refresh returned for id : " + host.hostName + " ", _host);
			angular.forEach($scope.hosts , function(value,key) {
				$log.info("inside foreach");
				if(value.id === _host.id) {
					$scope.hosts[key] = _host;
				}
			}); 
		});
	}
	
	 	
	$scope.addHost = function(id) {
		$log.info("Trying to save host with name ",$scope.selectedhost.name);
		$log.info("Trying to save roles for host : ",$scope.selectedhost.myroles)
		
		var hostModal = angular.element("#hostModel");
		hostModal.modal('hide');
		
		var host = {
				name : $scope.selectedhost.name,
				roles : []
		};
		
		$scope.selectedhost.myroles.forEach(function(myrole) {
			host.roles.push(myrole.name);
		})
		
		
		if(id) {
			$log.info("Update Request with id  : ",id);
			var host = HostRead.save({'name' : id , 'host' : host},function() {
				$log.info("Response : ",host);
			})
		} else {
		
			$log.info("Submitting request with payload : ",host);
			HostsRead.save(host,function() {
				$log.info("Successfully created new host : ",host.name);
				setAlert({
					'hidden' : false,
					'success' : true,
					'message' : 'Host : ' + host.name + ' successfully created!'
				});
				var hosts = HostsRead.query({},function() {
					$scope.hosts = hosts;
				});
				
			},function(error) {
				$log.error("ERROR : ",error);
				setAlert({
					'hidden' : false,
					'danger' : true,
					'message' : 'host : ' + host.name + ' could not be created! '
				});
			});
		}
	};
	
	$scope.showConfirm = function(ev,host) {
		
	    var confirm = $mdDialog.confirm(host)
	    
	      .title('Would you like to delete host ?')
	      .content('Delete Host ')
	      .ariaLabel('Delete Host')
	      .ok('Please do it!')
	      .cancel('Canceled')
	      .targetEvent(ev);
	    $log.info("Request for host : ",host.id);
	    $mdDialog.show(confirm).then(function() {
	    	$log.info("sending request for delete for : ",host.id);
	    	_delete(host);
	    }, function() {
	      
	    });
	  };
	  
	  $scope.toggleView = function(byRole) {
		  if(byRole) {
			  $scope.currentView = "Group by Host";
			  $scope.view		 	= "roleview";
			  $scope.rolesWithHosts	= groupHostsByRoles($scope.hosts);
		  } else {
			  $scope.currentView = "Group by Role";
			  $scope.view		 = "hostview";
		  }
	  };
	
	
	//$scope.hosts = HostsRead.query();
	var hosts = HostsRead.query({},function() {
		$scope.hosts = hosts;
	})
	$scope.roles = RolesRead.query();
	$scope.isactive = false;
	$scope.view		 = "hostview";
	
}])
.factory('HostRead',['$resource','$log',function($resource,$log) {
	var HostRead = $resource('theia/hosts/:name',{'name': '@name','host' : '@host'}, {
		'query' : {
			method : 'GET',
			isArray : true
		}
	});
	
	return HostRead;
}])
.factory('HostUpdate',['$resource','$log',function($resource,$log) {
	var HostsUpdate = $resource('theia/hosts/host/:id',{'id' : '@id','name' : '@name','roles' : '@roles'}, {
		'query' : {
			method : 'GET',
			isArray : true
		} 
	});
	
	return HostsUpdate;
}])
.factory('HostsRead',['$resource','$log',function($resource,$log) {
	var HostsRead = $resource('theia/hosts',{'name' : '@name','roles' : '@roles'}, {
		'query' : {
			method : 'GET',
			isArray : true
		} 
	});
	
	return HostsRead;
}])
.directive('hostname',['$q','HostRead','$timeout','$log',function($q,HostRead,$timeout,$log) {
	return {
	  require: 'ngModel',
	  link: function(scope, elm, attrs, ctrl) {
	      ctrl.$asyncValidators.hostname = function(modelValue, viewValue) {

	        if (ctrl.$isEmpty(modelValue)) {
	          // consider empty model valid
	          return $q.when();
	        }
	        var def = $q.defer();

	        $timeout(function() {

		        var host = HostRead.get({'name':viewValue},function() {
		        $log.info("query returned : ",viewValue);	
		        	if(host && host.id) {
		        		def.reject();
		        	} else {
		        		def.resolve();
		        	}
		        	
		        });
	        },1000);
		    return def.promise;
		     
	      };
	    }
	  };	
}]);