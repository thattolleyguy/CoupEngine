app.controller("coupCtrl", function($scope, $http, CoupSvc) {
	$scope.refreshPlayerTypes = function() {
		$http.get('/coup/playertypes').success(function (data) {
			$scope.playerTypes = data;
		}).error(function () {
			alert("an unexpected error ocurred!");
		});
	}
	
	$scope.loadSims = function() {
		$http.get('/coup/game').success(function (data) {
			$scope.sims = data;
		}).error(function () {
			$scope.sims = [];
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
		var url = "/coup/game";
		var types = $scope.players.map(function(player) {
			return player['type'];
		});
		var body = { runCount: $scope.gamecount, playerTypes: types };
		$http.post(url, body).success(function (data) {
			var results = data;
			results['winners'] = results['games'].map(function(x) { 
				var winnerIndex = x['playerInfo'].findIndex(function(p) {
					return !p['dead'];
				});
				return {
						index: winnerIndex,
						info: x['playerInfo'][winnerIndex]
				};
			});
			$scope.results = results;
		}).error(function() {
			$scope.results = { error: "Simulation failed" };
		});
		
		$scope.resultsURL = "gui/result"
	}
	
	$scope.refreshPlayerTypes();
	$scope.players = [];
	$scope.loadSims();
});