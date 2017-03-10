package com.ttolley.coup.model;

import java.util.List;

public class TestResult {

	private int testId;
	private String testLabel;
	private List<GameResult> games;
	
	public TestResult() {}
	
	public TestResult(int testId, String testLabel, List<GameResult> games) {
		this.testId = testId;
		this.testLabel = testLabel;
		this.games = games;
	}

	public int getTestId() {
		return testId;
	}

	public void setTestId(int testId) {
		this.testId = testId;
	}

	public String getTestLabel() {
		return testLabel;
	}

	public void setTestLabel(String testLabel) {
		this.testLabel = testLabel;
	}

	public List<GameResult> getGames() {
		return games;
	}

	public void setGames(List<GameResult> games) {
		this.games = games;
	}
}
