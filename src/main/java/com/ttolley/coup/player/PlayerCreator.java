package com.ttolley.coup.player;

import com.ttolley.coup.PlayerInfo;

import java.util.List;

/**
 * Created by tylertolley on 2/9/17.
 */
public interface PlayerCreator {
    // Factory method for creating a player
    public PlayerHandler create(PlayerInfo playerInfo, List<Integer> otherPlayerIds);
}
