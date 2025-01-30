package com.capgemini.south_node_adapter.domain.model.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AppCategory {

	@JsonProperty("IOT")
	IOT,

	@JsonProperty("HEALTH_CARE")
	HEALTH_CARE,

	@JsonProperty("GAMING")
	GAMING,

	@JsonProperty("VIRTUAL_REALITY")
	VIRTUAL_REALITY,

	@JsonProperty("SOCIALIZING")
	SOCIALIZING,

	@JsonProperty("SURVELIANCE")
	SURVELIANCE,

	@JsonProperty("ENTERTAINMENT")
	ENTERTAINMENT,

	@JsonProperty("CONNECTIVITY")
	CONNECTIVITY,

	@JsonProperty("PRODUCTIVITY")
	PRODUCTIVITY,

	@JsonProperty("SECURITY")
	SECURITY,

	@JsonProperty("IDUSTRIAL")
	IDUSTRIAL,

	@JsonProperty("EDUCATION")
	EDUCATION,

	@JsonProperty("OTHERS")
	OTHERS;
}
