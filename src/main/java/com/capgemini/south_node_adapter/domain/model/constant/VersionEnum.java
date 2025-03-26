package com.capgemini.south_node_adapter.domain.model.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum VersionEnum {

	@JsonProperty("OS_VERSION_UBUNTU_2204_LTS")
	OS_VERSION_UBUNTU_2204_LTS,

	@JsonProperty("OS_VERSION_RHEL_8")
	OS_VERSION_RHEL_8,

	@JsonProperty("OS_VERSION_RHEL_7")
	OS_VERSION_RHEL_7,

	@JsonProperty("OS_VERSION_DEBIAN_11")
	OS_VERSION_DEBIAN_11,

	@JsonProperty("OS_VERSION_COREOS_STABLE")
	OS_VERSION_COREOS_STABLE,

	@JsonProperty("OS_MS_WINDOWS_2012_R2")
	OS_MS_WINDOWS_2012_R2,

	@JsonProperty("OTHER")
	OTHER;
}
