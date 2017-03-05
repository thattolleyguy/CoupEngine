package com.ttolley.coup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ttolley.coup.Game;
import com.ttolley.coup.model.GameConfig;
import com.ttolley.coup.model.GameResult;
import com.ttolley.coup.player.PlayerCreator;

@Component
public class GameService {

	private final PlayerCreator playerCreator;

	@Autowired
	public GameService(PlayerCreator playerCreator) {
		this.playerCreator = playerCreator;
	}

	public GameResult playGame(GameConfig config) {
		Game game = new Game(config, playerCreator);

		boolean gameOver = game.nextTurn();
		while (!gameOver) {
			gameOver = game.nextTurn();
		}
		
		return game.getResults();
	}
}
