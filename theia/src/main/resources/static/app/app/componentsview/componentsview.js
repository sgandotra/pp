'use strict';

angular.module('myApp.componentsview', ['ngRoute','ngResource','xeditable'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/componentsview', {
    templateUrl: 'theia/app/app/componentsview/componentsview.html',
    controller: 'ComponentsviewCtrl'
  });
  
  $routeProvider.when('/componentsview/:id', {
	    templateUrl: 'theia/app/app/componentsview/componentsview.html',
	    controller: 'ComponentsviewCtrl'
  });
}])

.controller('ComponentsviewCtrl', ['$scope','ComponentsCRSvc',
                                   'ComponentRead',
                                   'ComponentUDSvc','$filter',
                                   '$log',function($scope,ComponentsCRSvc,
                                		   ComponentRead,
                                		   ComponentUDSvc,
                                		   $filter,$log) {
	$scope.hidden   = true;
	
	$scope.addComponent = function() {
		var componentModal = angular.element("#componentModel");
		componentModal.modal('hide');
		
		var input;
		
		if(!$scope.name || ($scope.name && $scope.name.length < 5)) {
			$log.info("Component name failed validation : [" + $scope.name + "]");
			$scope.danger = true;
			$scope.hidden = false;
			$scope.message = "Component name failed validation : [" + $scope.name + "]"
			 + " should be non-null and greater than 5 characters";
		} else {
			var input  = $scope.name.toLowerCase(),
				component = ComponentRead.get({name :input}, function() {
				if(component && component.id) {
					$log.info("Component : "+ input + " already exists");
					$scope.hidden = false;
					$scope.warning = true;
					$scope.message = "Component : " + input + " already exists, cannot have duplicates";
				} else {
					ComponentsCRSvc.save({name : input},function() {
						$scope.response = ComponentsCRSvc.query();
						$log.info("Record created successfully");
						$scope.hidden = false;
						$scope.success = true;
						$scope.message = "Component : " + input + " created!";
					});
				
					
				}
			});
			
				
		}
		
	};
	
	$scope.deleteComponent = function(_id,name) {
		$log.info("Deleting component with id : ",_id);
		if(!_id) {
			$scope.hidden = false;
			$scope.message = "Cannot delete component with undefined id";
			$scope.danger  = true;
		} else {
			
			ComponentUDSvc.delete({id : _id},function() {
				var records = ComponentsCRSvc.query({},function() {
					$scope.response = records;
					$scope.hidden = false;
					$scope.success = true;
					$scope.message = "Component : " + name + " deleted!";
				});
			},function(err) {
				var errmsg = err.data.message;
				if(errmsg && errmsg.indexOf("ConstraintViolationException") > -1 ) {
					$scope.hidden = false;
					$scope.danger = true;
					$scope.message = "Component : [" + name + "] could not be a deleted, " 
						+ "a role is referencing this component"; 
				}
			});
			
			
			
			
		}
	};
	
	$scope.updateComponent= function(_id,name,newname) {
		$log.info("Received : " +_id + " for update to name : " + name  + " to ", newname);
		
		var component = ComponentRead.get({'name' : name},function() {
			if(name != newname) {
				var records = ComponentUDSvc.save({'id' : _id, 'name' : newname} , function() {
					$scope.response = records;
					$scope.hidden = false;
					$scope.success = true;
					$scope.message = "Component : " + newname + " updated successfully!";
					$scope.response = ComponentsCRSvc.query();
				});
			}
		});
		
	};
	
	
	
	
	$scope.response = ComponentsCRSvc.query();

}])

.factory('ComponentsCRSvc',['$resource','$log',function($resource,$log) {
	var ComponentsCRSvc = $resource('theia/components',{'name' : '@name' }, {
		'query' : {
			method : 'GET',
			isArray : true
		} 
	});
	
	return ComponentsCRSvc;
}])
.factory('ComponentRead',['$resource','$log',function($resource,$log) {
	var ComponentRead = $resource('theia/components/component/:name',{'name' : '@name' });
	
	return ComponentRead;
}])
.factory('ComponentUDSvc',['$resource','$log',function($resource,$log) {
	var ComponentUDSvc = $resource('theia/components/component/:id',{'id' : '@id','name' : '@name' });
	
	return ComponentUDSvc;
}]);