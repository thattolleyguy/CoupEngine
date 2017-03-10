package com.ttolley.coup.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TestRun {
	private final int id;
	private final String label;

	@JsonCreator
	public TestRun(@JsonProperty("id") int id, @JsonProperty("label") String label) {
		this.id = id;
		this.label = label;
	}

	public int getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

}
