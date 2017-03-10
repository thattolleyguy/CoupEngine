package com.ttolley.coup.model;

import java.util.List;

public class GameResult {

	private List<PlayerInfo> playerInfo;

	public GameResult(List<PlayerInfo> playerInfo) {
		this.playerInfo = playerInfo;
	}

	public List<PlayerInfo> getPlayerInfo() {
		return playerInfo;
	}

	public void setPlayerInfo(List<PlayerInfo> playerInfo) {
		this.playerInfo = playerInfo;
	}

}
