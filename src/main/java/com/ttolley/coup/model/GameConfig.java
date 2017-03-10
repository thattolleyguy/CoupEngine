package com.ttolley.coup.model;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

/**
 * Created by tylertolley on 2/9/17.
 */
public class GameConfig {
	public final int runCount;
	public final String testLabel;
    public final List<String> playerTypes;

    @JsonIgnore
    public int testId;
    
    @JsonCreator
    public GameConfig(@JsonProperty("runCount") int runCount, 
    		@JsonProperty("testLabel") String testLabel,
    		@JsonProperty("playerTypes") List<String> playerTypes) {
        this.playerTypes = Lists.newArrayList(playerTypes);
        this.testLabel = testLabel == null ? "test_" + UUID.randomUUID() : testLabel;
        this.runCount = runCount <= 0 ? 1 : runCount;
    }
}
