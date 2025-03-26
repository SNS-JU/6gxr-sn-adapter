package com.capgemini.south_node_adapter.domain.model.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OnboardStatusInfoEnum {

	@JsonProperty("PENDING")
	PENDING,

	@JsonProperty("ONBOARDED")
	ONBOARDED,

	@JsonProperty("DEBOARDING")
	DEBOARDING,

	@JsonProperty("REMOVED")
	REMOVED,

	@JsonProperty("FAILED")
	FAILED;
}
