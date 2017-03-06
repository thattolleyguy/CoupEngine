package com.ttolley.coup.resource;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.ttolley.coup.model.GameConfig;
import com.ttolley.coup.model.GameResult;
import com.ttolley.coup.model.PlayerTypeInfo;
import com.ttolley.coup.service.GameService;

@RestController
public class CoupController {

	private final GameService gameService;
	
	@Autowired
	public CoupController(GameService gameService) {
		this.gameService = gameService;
	}

	@RequestMapping(value = "/coup/game", method = RequestMethod.POST)
	public List<GameResult> runGame(@RequestParam(value = "gameCount", defaultValue = "1") int gameCount,
			                        @RequestBody GameConfig config) {
		List<GameResult> results = Lists.newArrayList();
		for (int i = 0; i < gameCount; i++) {
		    results.add( gameService.playGame(config) );
		}
		return results;
	}
	
	@RequestMapping(value = "/coup/playertypes", method = RequestMethod.GET)
	public List<PlayerTypeInfo> getPlayerTypes() {
		return gameService.getPlayerTypes();
	}
	
	@RequestMapping(value = "/coup/stats", method = RequestMethod.GET)
	public Map<String, Integer> getGameStats() {
		return gameService.getStatistics();
	}
}
