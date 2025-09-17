package com.capgemini.south_node_adapter.infrastructure.feign.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IEAPApplication {

	@JsonProperty("appId")
	private String appId;

	@JsonProperty("appMetaData")
	private IEAPApplicationAppMetaData appMetaData;

	@JsonProperty("appQoSProfile")
	private IEAPApplicationAppQoSProfile appQoSProfile;

	@JsonProperty("appComponentSpecs")
	private List<IEAPApplicationAppComponentSpecs> appComponentSpecs;

	@JsonProperty(access = Access.WRITE_ONLY)
	List<IEAPZoneDetails> appDeploymentZones;
}
