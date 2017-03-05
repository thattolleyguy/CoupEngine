package com.ttolley.coup.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.ttolley.coup.player.PlayerCreator;

import java.util.List;

/**
 * Created by tylertolley on 2/9/17.
 */
public class GameConfig {
    public final List<String> playerTypes;

    @JsonCreator
    public GameConfig(@JsonProperty("playerTypes") List<String> playerTypes) {
        this.playerTypes = Lists.newArrayList(playerTypes);
    }
}
