package com.capgemini.south_node_adapter.domain.model.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PageSizeEnum {

	@JsonProperty("2Mi")
	MI2,

	@JsonProperty("1Gi")
	GI1;
}
