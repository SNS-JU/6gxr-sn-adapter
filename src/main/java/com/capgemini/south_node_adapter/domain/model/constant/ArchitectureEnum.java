package com.capgemini.south_node_adapter.domain.model.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ArchitectureEnum {

	@JsonProperty("x86_64")
	X86_64,

	@JsonProperty("x86")
	X86;
}
