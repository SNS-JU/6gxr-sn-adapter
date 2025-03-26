package com.capgemini.south_node_adapter.domain.model.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DistributionEnum {

	@JsonProperty("RHEL")
	RHEL,

	@JsonProperty("UBUNTU")
	UBUNTU,

	@JsonProperty("COREOS")
	COREOS,

	@JsonProperty("FEDORA")
	FEDORA,

	@JsonProperty("WINDOWS")
	WINDOWS,

	@JsonProperty("OTHER")
	OTHER;
}
