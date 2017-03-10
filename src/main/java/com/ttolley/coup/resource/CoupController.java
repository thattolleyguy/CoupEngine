package com.ttolley.coup.resource;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.ttolley.coup.model.GameConfig;
import com.ttolley.coup.model.GameResult;
import com.ttolley.coup.model.PlayerTypeInfo;
import com.ttolley.coup.model.TestResult;
import com.ttolley.coup.model.TestRun;
import com.ttolley.coup.service.GameService;

@RestController
public class CoupController {

	private final GameService gameService;
	
	@Autowired
	public CoupController(GameService gameService) {
		this.gameService = gameService;
	}

	@RequestMapping(value = "/coup/game", method = RequestMethod.POST)
	public TestResult runGame(@RequestBody GameConfig config) {
		
		int testId = gameService.initializeTestRun(config);
		List<GameResult> results = Lists.newArrayList();
		for (int i = 0; i < config.runCount; i++) {
		    results.add( gameService.playGame(config) );
		}
		return new TestResult(testId, config.testLabel, results);
	}
	
	@RequestMapping(value = "/coup/game", method = RequestMethod.GET)
	public List<TestRun> getTestRuns(@RequestParam(value = "limit", defaultValue = "50") int limit,
			                         @RequestParam(value = "offset", defaultValue = "0") int offset) {
		return gameService.getTestRuns(limit, offset);
	}
	
	@RequestMapping(value = "/coup/game/{testId}", method = RequestMethod.GET)
	public TestResult getTestResult(@PathVariable("testId") int testId) {
		return gameService.getTestResult(testId);
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
