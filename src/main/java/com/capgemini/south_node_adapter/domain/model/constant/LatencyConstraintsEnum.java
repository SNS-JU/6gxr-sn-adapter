package com.capgemini.south_node_adapter.domain.model.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum LatencyConstraintsEnum {

	@JsonProperty("NONE")
	NONE,

	@JsonProperty("LOW")
	LOW,

	@JsonProperty("ULTRALOW")
	ULTRALOW;
}
