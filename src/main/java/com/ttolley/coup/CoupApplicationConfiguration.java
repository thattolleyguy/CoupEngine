package com.ttolley.coup;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.ttolley.coup.player.BootPlayerCreator.PlayerHandlerSupplier;
import com.ttolley.coup.player.RandomPlayerHandler;
import com.ttolley.coup.player.TruthPlayerHandler;

@Configuration
public class CoupApplicationConfiguration {

	@Bean
	public PlayerHandlerSupplier randomPlayerHandlerSupplier() {
		return PlayerHandlerSupplier.supplierFor(RandomPlayerHandler.class);
	}
	
	@Bean
	public PlayerHandlerSupplier truthPlayerHandlerSupplier() {
		return PlayerHandlerSupplier.supplierFor(TruthPlayerHandler.class);
	}
	
	// Repository configuration
	@Bean
	public JdbcTemplate gameHistoryTemplate(DataSource repo) {
		return new JdbcTemplate(repo);
	}
	
	@Bean
	public DataSource gameHistoryRepoDatasource() throws Exception {
		return new DriverManagerDataSource("jdbc:h2:mem:history;DB_CLOSE_DELAY=-1", "sa", "");
	}
	
}
