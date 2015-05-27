var filterModule = angular.module('myApp.customfilter', []);
 
filterModule.filter('titleCase', function () {
  return function (input) {
	  if(!input || angular.isNumber(input)) return;
	  var words = input.toString().split(' ') ;
	  if(words) {
    	for (var i = 0; i < words.length; i++) {
    		words[i] = words[i].charAt(0).toUpperCase() + words[i].slice(1);
    	}
    	return words.join(' ');
	  }
	  return input;
  }
});
filterModule.filter('propsFilter', function() {
	 return function(items, props) {
		    var out = [];

		    if (angular.isArray(items)) {
		      items.forEach(function(item) {
		        var itemMatches = false;

		        var keys = Object.keys(props);
		        for (var i = 0; i < keys.length; i++) {
		          var prop = keys[i];
		          var text = props[prop].toLowerCase();
		          if (item[prop].toString().toLowerCase().indexOf(text) !== -1) {
		            itemMatches = true;
		            break;
		          }
		        }

		        if (itemMatches) {
		          out.push(item);
		        }
		      });
		    } else {
		      // Let the output be the input untouched
		      out = items;
		    }

		    return out;
		  };
});
filterModule.filter('removeIds',function() {
	
	return function(items) {
		
		var out = [];
		if(angular.isArray(items)) {
			
			items.forEach(function(item) {
				out.push(item.name.toLowerCase());
			})
			
		} else {
			out = items;
		}
		
		return out;
	}
	
});
