package com.capgemini.south_node_adapter.domain.model;

import com.capgemini.south_node_adapter.domain.model.constant.LatencyConstraintsEnum;
import com.capgemini.south_node_adapter.domain.model.constant.MultiUserClientsEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppQoSProfile {

	@JsonProperty("latencyConstraints")
	private LatencyConstraintsEnum latencyConstraints = null;

	@JsonProperty("bandwidthRequired")
	private Integer bandwidthRequired = null;

	@JsonProperty("multiUserClients")
	private MultiUserClientsEnum multiUserClients = MultiUserClientsEnum.SINGLE_USER;

	@JsonProperty("noOfUsersPerAppInst")
	private Integer noOfUsersPerAppInst = 1;

	@JsonProperty("appProvisioning")
	private Boolean appProvisioning = true;
}
