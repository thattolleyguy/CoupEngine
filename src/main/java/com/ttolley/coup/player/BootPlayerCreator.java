package com.ttolley.coup.player;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ttolley.coup.model.PlayerInfo;

@Component
public class BootPlayerCreator implements PlayerCreator {

	Map<String, PlayerHandlerSupplier> handlers = Maps.newHashMap();

	@Autowired
	public BootPlayerCreator(List<PlayerHandlerSupplier> handlerSuppliers) {
		handlerSuppliers.forEach(s -> {
			if (handlers.containsKey(s.id)) {
				throw new RuntimeException("Duplicat PlayerHandlerSuppliers for: " + s.id);
			}
			handlers.put(s.id, s);
		});
	}

	@Override
	public PlayerHandler create(String id, PlayerInfo playerInfo, List<Integer> otherPlayerIds) {
		return handlers.get(id).createMethod.apply(playerInfo, otherPlayerIds);
	}

	public List<String> getPlayerTypes() {
		return Lists.newArrayList(handlers.keySet());
	}
	
	public static class PlayerHandlerSupplier {
		String id;
		BiFunction<PlayerInfo, List<Integer>, PlayerHandler> createMethod;

		public static PlayerHandlerSupplier supplierFor(Class<? extends PlayerHandler> pClass) {
			PlayerHandlerSupplier supplier = new PlayerHandlerSupplier();
			supplier.id = pClass.getSimpleName();
			try {
				Constructor<? extends PlayerHandler> c = pClass.getConstructor(PlayerInfo.class, List.class);
				supplier.createMethod = (playerInfo, otherPlayerIds) -> {
					try {
						return c.newInstance(playerInfo, otherPlayerIds);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				};
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(
						"Illegal PlayerHandler - does not implement constructor (PlayerInfo p, List<Integer> otherPlayerIds)", e);
			}
			return supplier;
		}
	}
}
