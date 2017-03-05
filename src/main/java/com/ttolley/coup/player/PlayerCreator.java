package com.ttolley.coup.player;

import java.util.List;

import com.ttolley.coup.model.PlayerInfo;

/**
 * Created by tylertolley on 2/9/17.
 */
public interface PlayerCreator {

    // Factory method for creating a player
    public PlayerHandler create(String type, PlayerInfo playerInfo, List<Integer> otherPlayerIds);
}
