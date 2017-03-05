package com.ttolley.coup.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ttolley.coup.model.GameConfig;
import com.ttolley.coup.model.GameResult;
import com.ttolley.coup.service.GameService;

@RestController
public class CoupController {

	private final GameService gameService;
	
	@Autowired
	public CoupController(GameService gameService) {
		this.gameService = gameService;
	}

	@RequestMapping(value = "/coup/game", method = RequestMethod.POST)
	public GameResult runGame(@RequestBody GameConfig config) {
		return gameService.playGame(config);
	}
}
