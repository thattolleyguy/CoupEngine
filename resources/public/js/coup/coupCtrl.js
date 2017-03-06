app.controller("coupCtrl", function($scope, $http, CoupSvc) {
	$scope.refreshPlayerTypes = function() {
		$http.get('/coup/playertypes').success(function (data) {
			$scope.playerTypes = data;
		}).error(function () {
			alert("an unexpected error ocurred!");
		});
	}
	
	$scope.addPlayer = function() {
		var newplayer = { 
			type: $scope.playerpick.type
		};
		
		$scope.players.push(newplayer);
		if ($scope.players.length > 7) {
		    $scope.maxplayers = true;
		}
	}
	
	$scope.removePlayer = function(index) {
		$scope.players.splice(index, 1);
		$scope.maxplayers = false;
	}
	
	$scope.rungame = function() {
		var url = "/coup/game?gameCount=" + ($scope.gamecount || 1);
		var types = $scope.players.map(function(player) {
			return player['type'];
		});
		var body = { playerTypes: types };
		$http.post(url, body).success(function (data) {
			$scope.results = data;
		}).error(function() {
			$scope.results = { error: "Simulation failed" };
		});
	}
	
	$scope.refreshPlayerTypes();
	$scope.players = [];
});