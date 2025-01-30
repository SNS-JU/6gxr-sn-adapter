package com.capgemini.south_node_adapter.domain.model.constant;

import com.fasterxml.jackson.annotation.JsonProperty;


public enum DeploymentSitePreference {

	@JsonProperty("One")
	ONE,

	@JsonProperty("All")
	ALL;
}
