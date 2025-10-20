package com.capgemini.south_node_adapter.infrastructure.feign.model;

import com.capgemini.south_node_adapter.domain.model.constant.LatencyConstraintsEnum;
import com.capgemini.south_node_adapter.domain.model.constant.MultiUserClientsEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IEAPApplicationAppQoSProfile {

	@JsonProperty("latencyConstraints")
	private LatencyConstraintsEnum latencyConstraints;

	@JsonProperty("bandwidthRequired")
	private Integer bandwidthRequired;

	@JsonProperty("multiUserClients")
	private MultiUserClientsEnum multiUserClients;

	@JsonProperty("noOfUsersPerAppInst")
	private Integer noOfUsersPerAppInst;

	@JsonProperty("appProvisioning")
	private Boolean appProvisioning;
}
