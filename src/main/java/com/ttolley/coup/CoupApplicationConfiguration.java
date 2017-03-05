package com.ttolley.coup;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
