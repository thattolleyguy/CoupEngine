package com.ttolley.coup.player;

import com.ttolley.coup.PlayerInfo;

import java.util.List;

/**
 * Created by tylertolley on 2/9/17.
 */
public enum PlayerTypes implements PlayerCreator {
    RANDOM {
        @Override
        public PlayerHandler create(PlayerInfo playerInfo, List<Integer> otherPlayerIds) {
            return new RandomPlayerHandler(playerInfo, otherPlayerIds);
        }
    },
    TRUTHFUL {
        @Override
        public PlayerHandler create(PlayerInfo playerInfo, List<Integer> otherPlayerIds) {
            return new TruthPlayerHandler(playerInfo, otherPlayerIds);
        }
    }
}
