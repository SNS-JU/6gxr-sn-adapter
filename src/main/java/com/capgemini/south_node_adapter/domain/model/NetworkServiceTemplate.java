package com.capgemini.south_node_adapter.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import com.capgemini.south_node_adapter.domain.model.constant.TargetNodeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
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
	@JsonFormat(pattern = "dd/MM/yyyy hh:mm a", shape = JsonFormat.Shape.STRING)
	private LocalDateTime startTime = null;

	@JsonProperty("stopTime")
	@JsonFormat(pattern = "dd/MM/yyyy hh:mm a", shape = JsonFormat.Shape.STRING)
	private LocalDateTime stopTime = null;

	@JsonProperty("targetNode")
	private TargetNodeEnum targetNode = null;

	@JsonProperty("targetFacility")
	private String targetFacility = null;

	@JsonProperty("kpis")
	@Valid
	private List<String> kpis = null;

	@JsonProperty("southNodeAdapterNetworkServiceTemplate")
	private SouthNodeAdapterNetworkServiceTemplate southNodeAdapterNetworkServiceTemplate = null;
	
	@JsonProperty("qosiumMonitoring")
	private Boolean qosiumMonitoring = null;
}
