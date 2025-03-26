package com.capgemini.south_node_adapter.domain.model.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum LicenseEnum {

	@JsonProperty("OS_LICENSE_TYPE_FREE")
	OS_LICENSE_TYPE_FREE,

	@JsonProperty("OS_LICENSE_TYPE_ON_DEMAND")
	OS_LICENSE_TYPE_ON_DEMAND,

	@JsonProperty("NOT_SPECIFIED")
	NOT_SPECIFIED;
}
