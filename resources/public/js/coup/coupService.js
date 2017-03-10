app.service('CoupSvc', function($http) {
    return {
        getStats: function() {
            return $http.get('/coup/stats').then(function(response) {
                return response.data;
            });
        },
        
        getPlayerTypes: function() {
            return $http.get('/coup/playertypes').success(function(data) {
                return data;
            }).error(function() {
            	alert("an unexpected error occurred!");
            });
        }
    }
})