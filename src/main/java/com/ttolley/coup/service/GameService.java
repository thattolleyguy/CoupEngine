package com.ttolley.coup.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import com.ttolley.coup.Game;
import com.ttolley.coup.model.GameConfig;
import com.ttolley.coup.model.GameResult;
import com.ttolley.coup.model.PlayerInfo;
import com.ttolley.coup.model.PlayerTypeInfo;
import com.ttolley.coup.model.TestResult;
import com.ttolley.coup.model.TestRun;
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

	public int initializeTestRun(GameConfig config) {
		return gameHistoryManager.initializeTest(config);
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

	public TestResult getTestResult(int testId) {
		return gameHistoryManager.getTestResults(testId);
	}
	
	public List<TestRun> getTestRuns(int limit, int offset) {
		Preconditions.checkArgument(limit >= 0 && limit < 100);
		Preconditions.checkArgument(offset >= 0);
		
		return gameHistoryManager.getTestRuns(limit, offset);
	}
}
