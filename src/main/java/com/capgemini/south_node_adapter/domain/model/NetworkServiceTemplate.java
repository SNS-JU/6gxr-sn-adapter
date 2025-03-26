package com.capgemini.south_node_adapter.domain.model;

import java.time.OffsetDateTime;
import java.util.List;

import com.capgemini.south_node_adapter.domain.model.constant.TargetNodeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NetworkServiceTemplate {
	@JsonProperty("trialId")
	private Integer trialId = null;

	@JsonProperty("experimentName")
	private String experimentName = null;

	@JsonProperty("startTime")
	private OffsetDateTime startTime = null;

	@JsonProperty("stopTime")
	private OffsetDateTime stopTime = null;

	@JsonProperty("targetNode")
	private TargetNodeEnum targetNode = null;

	@JsonProperty("targetFacility")
	private String targetFacility = null;

	@JsonProperty("kpis")
	@Valid
	private List<String> kpis = null;

	@JsonProperty("southNodeAdapterNetworkServiceTemplate")
	private SouthNodeAdapterNetworkServiceTemplate southNodeAdapterNetworkServiceTemplate = null;
}
