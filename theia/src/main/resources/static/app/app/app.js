'use strict';

// Declare app level module which depends on views, and components
angular.module('myApp', [
  'ngRoute',
  'ui.select',
  'ngSanitize',
  'myApp.view1',
  'myApp.view2',
  'myApp.jmeter',
  'myApp.jmetertestresult',
  'myApp.componentsview',
  'myApp.rolesview',
  'myApp.hosts',
  'myApp.config',
  'myApp.version',
  'myApp.customfilter'
]).
config(['$routeProvider' ,'$locationProvider', function($routeProvider,$locationProvider) {
  $routeProvider.otherwise({redirectTo: '/view1'});
  
  $locationProvider.hashPrefix('!');
  
}]);