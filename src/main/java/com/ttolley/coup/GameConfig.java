package com.ttolley.coup;

import com.google.common.collect.Lists;
import com.ttolley.coup.player.PlayerCreator;
import com.ttolley.coup.player.PlayerTypes;

import java.util.List;

/**
 * Created by tylertolley on 2/9/17.
 */
public class GameConfig {
    List<PlayerTypes> playerTypes = Lists.newArrayList();

    public GameConfig(PlayerTypes... playerTypes){
        for (PlayerTypes playerType : playerTypes) {
            this.playerTypes.add(playerType);
        }
    }
}
