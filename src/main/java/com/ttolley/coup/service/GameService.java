package com.ttolley.coup.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ttolley.coup.Game;
import com.ttolley.coup.model.GameConfig;
import com.ttolley.coup.model.GameResult;
import com.ttolley.coup.model.PlayerInfo;
import com.ttolley.coup.model.PlayerTypeInfo;
import com.ttolley.coup.player.PlayerCreator;
import com.ttolley.coup.repository.GameHistoryManager;

@Component
public class GameService {

	private final GameHistoryManager gameHistoryManager;
	private final PlayerCreator playerCreator;

	@Autowired
	public GameService(GameHistoryManager gameHistoryManager, PlayerCreator playerCreator) {
		this.playerCreator = playerCreator;
		this.gameHistoryManager = gameHistoryManager;
	}

	public GameResult playGame(GameConfig config) {
		Game game = new Game(config, gameHistoryManager, playerCreator);

		boolean gameOver = game.nextTurn();
		while (!gameOver) {
			gameOver = game.nextTurn();
		}
		
		PlayerInfo winner = game.getWinner();
		if (winner != null) {
		    gameHistoryManager.updateWinner(game.getId(), game.getWinner());
		}
		
		return game.getResults();
	}
	
	public Map<String, Integer> getStatistics() {
		return gameHistoryManager.getGameStats();
	}

	public List<PlayerTypeInfo> getPlayerTypes() {
		return playerCreator.getPlayerTypes().stream().map(PlayerTypeInfo::new).collect(Collectors.toList());
	}
}
