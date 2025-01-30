package com.capgemini.south_node_adapter.domain.model;

import com.capgemini.south_node_adapter.domain.model.constant.ArchitectureEnum;
import com.capgemini.south_node_adapter.domain.model.constant.DistributionEnum;
import com.capgemini.south_node_adapter.domain.model.constant.LicenseEnum;
import com.capgemini.south_node_adapter.domain.model.constant.VersionEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OSType {

	@JsonProperty("architecture")
	private ArchitectureEnum architecture = null;

	@JsonProperty("distribution")
	private DistributionEnum distribution = null;

	@JsonProperty("version")
	private VersionEnum version = null;

	@JsonProperty("license")
	private LicenseEnum license = null;
}
