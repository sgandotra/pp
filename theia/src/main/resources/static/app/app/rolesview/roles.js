'use strict';

angular.module('myApp.rolesview', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/rolesview', {
    templateUrl: 'theia/app/app/rolesview/roles.html',
    controller: 'RolesCtrl'
  });
}])

.controller('RolesCtrl', ['$scope','RolesRead','RoleRead','RoleDelete',
                          'RoleComponentDelete','ComponentsCRSvc','$filter','$log',
                          function($scope,RolesRead,RoleRead,RoleDelete,
                        		  RoleComponentDelete,ComponentsCRSvc,$filter,$log) {

	
	$scope.response = RolesRead.query();
	
	$scope.reload	= function() {
		return Components.query({});
	};
	
	$scope.addRole = function() {
		$log.info("Selected role : ",$scope.selectedrole);
		$log.info("Selected components : ",$scope.selectedrole.mycomponents);
		
		var roleModal = angular.element("#roleModel");
		roleModal.modal('hide');
		
		var role = {};
		role.roleName = $scope.selectedrole.name;
		
		var resources = $scope.selectedrole.mycomponents,
			names	  = [];
		
		for(var key in resources) {
			names.push(resources[key].name);
		}		
		role.components = names;
		
		//validate

		if(!role.roleName || (role.roleName && role.roleName.length < 5)
				|| (role.components && role.components.length === 0)) {
			$log.info("Role failed validation");
			$scope.hidden = false;
			$scope.danger = true;
			$scope.message = "Validation Filed : [" + role.roleName + "] should be greater than 5 characters" +
					" and atleast one component needs to be selected!";
			
			return;
		}
		
		
		RolesRead.save(role,function() {
			$log.info("Role successfully saved");
			$scope.hidden = false;
			$scope.success = true;
			$scope.message = "Role : " + role.roleName + " created!";
			$scope.response = RolesRead.query();
		},function(err) {
			$log.info("Error creating role");
			$scope.hidden = false;
			$scope.danger = true;
			$scope.message = "Role : " + role.roleName + " could not be created!" +
					" error message : " + err.error;
			$log.error(err);
		});

	};
	
	$scope.deleteRole = function(id) {
		$log.info("Request to delete role with id: ",id);
		
		RoleDelete.delete({'id' : id} , function() {
			$log.info("Role deleted ");
			$scope.hidden = false;
			$scope.success = true;
			$scope.message = "Role : " + id + " deleted!";
			$scope.response = RolesRead.query();
		},function(err) {
			var errmsg = err.data.message;
			if(errmsg && errmsg.indexOf("ConstraintViolationException") > -1 ) {
				$scope.hidden = false;
				$scope.danger = true;
				$scope.message = "Role : [" + id + "] could not be a deleted, " 
					+ "a Host is referencing this Role"; 
			}
		});
	};
	
	$scope.deleteSingleComponent = function(id,componentName) {
		$log.info("Deleting single component : " + componentName +"  for Role : " +id);

		RoleComponentDelete.delete({'id' : id, 'component': componentName},function() {
			$log.info("Component deleted");
			$scope.hidden = false;
			$scope.success = true;
			$scope.message = "Component : " + componentName + " deleted for " + id;
			$scope.response = RolesRead.query();
		},function(err) {
			$log.info("Error deleting component");
			$scope.hidden = false;
			$scope.danger = true;
			if(err.status === 400) {
				$scope.message = "Component : " + componentName + " could not be deleted!" +
				", atleast one component is required in a role";
			} else {
				$scope.message = "Component : " + componentName + " could not be deleted!" +
					" error message : " + err.statusText;
			}
			$log.error(err);
		});
	}
	
	$scope.setupEditRole = function(role) {
		$scope.selectedrole = $scope.selectedrole || {} ;
		$scope.selectedrole.id   = role.id;
		$scope.selectedrole.name = role.name;
		$scope.selectedrole.mycomponents = role.components;
	}
	
	$scope.editRole = function() {
		$log.info("updating role  : ",$scope.selectedrole);
		if(!$scope.selectedrole.mycomponents || $scope.selectedrole.mycomponents.length == 0) {
			$scope.hidden = false;
			$scope.danger = true;
			$scope.message = "Components must be defined!";
			return;
		}
		
		var resources = $scope.selectedrole.mycomponents,
		names	  = [];
	
		for(var key in resources) {
			names.push(resources[key].name);
		}		
		
		
		var roles = RoleDelete.save({'id' : $scope.selectedrole.id,
			'roleName' : $scope.selectedrole.name,
			'components' : names
			} , function () {
				$scope.selectedrole.mycomponents = roles.components;
				$scope.selectedrole.name		 = roles.name;
				$scope.hidden = false;
				$scope.success = true;
				$scope.message = "Role  was modified successfuly!";
				$scope.response = RolesRead.query();
		});
		
		var edit = angular.element('#updateRoleModel');
		edit.modal('hide');
		
		
	}
	
	
	$scope.components = ComponentsCRSvc.query();
}])
.factory('RolesRead',['$resource','$log',function($resource,$log) {
	var RolesRead = $resource('/theia/roles',{'roleName' : '@roleName','components' : '@components'}, {
		'query' : {
			method : 'GET',
			isArray : true
		} 
	});
	
	return RolesRead;
}])
.factory('RoleRead',['$resource','$log',function($resource,$log) {
	var RoleRead = $resource('/theia/roles/role/:name',{'name':'@name'});
	
	return RoleRead;
}])
.factory('RoleDelete',['$resource','$log',function($resource,$log) {
	var RoleRead = $resource('/theia/roles/role/:id',{'id':'@id','roleName' : '@roleName','components' : '@components'});
	
	return RoleRead;
}])
.factory('RoleComponentDelete',['$resource','$log',function($resource,$log) {
	var RoleRead = $resource('/theia/roles/role/component/:id',{'id':'@id','component' : '@component'});
	
	return RoleRead;
}])
.directive('rolename',['$q','RoleRead','$timeout','$log',function($q,RoleRead,$timeout,$log) {
	return {
	  require: 'ngModel',
	  link: function(scope, elm, attrs, ctrl) {
	      ctrl.$asyncValidators.rolename = function(modelValue, viewValue) {

	        if (ctrl.$isEmpty(modelValue)) {
	          // consider empty model valid
	          return $q.when();
	        }

	        var def = $q.defer();
	        $timeout(function() {
	        	var role = RoleRead.get({'name':modelValue},function() {
	 	        if(role && role.id) {
	 	        	def.reject();
	 	        } else {
	 	        	def.resolve();
	 	        }
	        },5000);
	       
	        	
	        });
	        return def.promise;
	      };
	    }
	  };	
}]);