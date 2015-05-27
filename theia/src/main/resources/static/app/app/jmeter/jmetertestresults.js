'use strict';

angular.module('myApp.jmetertestresult', ['ngRoute','ui.bootstrap','ngMaterial','duScroll'])

.config(['$routeProvider',function($routeProvider) {
		 
  $routeProvider.when('/jmetertestresults', {
    templateUrl: 'theia/app/app/jmeter/jmetertestresults.html',
    controller: 'JmeterTestResultCtrl'
  });
}])
.controller('JmeterTestResultCtrl',['$scope','testSvc','$log',function($scope,testSvc,$log) {
	
	
	$scope.completedpages = {};
	$scope.completedpages.count = [];
	$scope.runningpages = {};
	$scope.runningpages.count = [];
	$scope.sharedpages = {};
	$scope.sharedpages.count = [];
	
	$scope.isCompletedActive = function(index) {
	//	$log.info("Verifying index : ",index) 
	//	$log.info("Currently selected :",$scope.sharedpages.currentSelected);
		return ($scope.completedpages.currentSelected === index);
		
		
	}
	
	$scope.getByIndexCompleted = function(index) {
		testSvc.get({
			'status' : 'COMPLETED',
			'page'   : index
		},function(response) {
			$scope.completedTests = response.content;
			$scope.completedTestsCount = response.totalElements;
			$scope.completedTotalPages = response.totalPages;
			
			
			
			for(var i = 1; i < $scope.completedTotalPages; i++) {
				$scope.completedpages.count[i-1] = i;
			}	
				
			if($scope.completedpages.currentSelected) {
				$scope.completedpages.currentSelected = index;
			} else {
				$scope.completedpages.currentSelected = 1;
			}
			
		});
	}
	
	$scope.getByIndexRunning = function(index) {
		testSvc.get({
			'status' : 'RUNNING',
			'page'   : index
		},function(response) {
			$scope.runningTests = response.content;
			$scope.runningTestsCount = response.totalElements;
			$scope.runningTotalPages = response.totalPages;
			
		
			$scope.runningpages.currentSelected = 1;
			for(var i = 1; i < $scope.runningTotalPages; i++) {
				$scope.runningpages.count[i-1] = i;
			}	
			
		});
	}
	
	$scope.getByIndexShared = function(index) {
		testSvc.get({
			'status' : 'SHARED',
			'page'   : index
		},function(response) {
			$scope.sharedTests = response.content;
			$scope.sharedTestsCount = response.totalElements;
			$scope.sharedTotalPages = response.totalPages;
			
			$scope.sharedpages.currentSelected = 1;
			for(var i = 1; i < $scope.sharedTotalPages; i++) {
				$scope.sharedTotalPages.count[i-1] = i;
			}	
			
		});
	}
	
	$scope.getNextCompleted = function() {
		if($scope.completedpages.currentSelected === $scope.completedTotalPages - 1)
			return;
		
		$scope.completedpages.currentSelected = $scope.completedpages.currentSelected + 1;
		$scope.getByIndexCompleted($scope.completedpages.currentSelected);
	}
	
	$scope.getPreviousCompleted = function() {
		
		if($scope.completedpages.currentSelected == 1)
			return;
		$scope.completedpages.currentSelected = $scope.completedpages.currentSelected - 1;
		$scope.getByIndexCompleted($scope.completedpages.currentSelected);
	}
	
	$scope.msg  = {
		'hidden' : true
	};
	
	$scope.getByIndexShared(0);
	$scope.getByIndexRunning(0);
	$scope.getByIndexCompleted(0);
	
}])
.factory('testSvc',['$resource','$log',function($resource,$log) {
	var testSvc = $resource('/theia/jmeter/all/:status',{'status' : '@testStatus',
															'page' : '@pageId',
															'size' : '10'});	
	return testSvc;
}]);