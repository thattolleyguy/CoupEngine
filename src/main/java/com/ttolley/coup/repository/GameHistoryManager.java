package com.ttolley.coup.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.ttolley.coup.model.Action;
import com.ttolley.coup.model.GameConfig;
import com.ttolley.coup.model.PlayerInfo;

// TODO
@Component
public class GameHistoryManager {

	private final JdbcTemplate template;

	@Autowired
	public GameHistoryManager(JdbcTemplate template) {
		this.template = template;
		initializeDb();
	}

	private void initializeDb() {
		template.update(
				"CREATE TABLE game(id INT AUTO_INCREMENT PRIMARY KEY, player_count INT, winner INT, winner_type VARCHAR(64))");
		template.update("CREATE TABLE events(id INT AUTO_INCREMENT PRIMARY KEY, " + "game_id INT NOT NULL, "
				+ "player_id INT NOT NULL, " + "target_id INT, " + "action VARCHAR(32) NOT NULL, "
				+ "role VARCHAR(16))");
	}

	public int initializeGame(GameConfig config) {
		String sql = "INSERT INTO game (player_count) VALUES (?)";
		KeyHolder holder = new GeneratedKeyHolder();
		template.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, config.playerTypes.size());
				return ps;
			}
		}, holder);

		return holder.getKey().intValue();
	}

	public void updateWinner(int gameId, PlayerInfo winner) {
		template.update("UPDATE game SET winner = ?, winner_type = ? WHERE id = ?", winner.playerId, winner.type, gameId);
	}

	public Map<String, Integer> getGameStats() {
		return template.query("SELECT winner_type, COUNT(1) FROM game GROUP BY winner_type", rs -> {
			Map<String, Integer> totals = Maps.newHashMap();
			while(rs.next()) {
				String type = rs.getString(1);
				if (type != null) {
				    totals.put(type, rs.getInt(2));
				}
			}
			return totals;
		});
	}

	public void registerEvent(int gameId, Action action) {
		template.update("INSERT INTO events (game_id, player_id, target_id, action, role) VALUES (?, ?, ?, ?, ?)",
				gameId, action.sourcePlayerId, action.targetPlayerId, action.type.name(),
				action.type.requiredRole == null ? null : action.type.requiredRole.name());
	}
}
