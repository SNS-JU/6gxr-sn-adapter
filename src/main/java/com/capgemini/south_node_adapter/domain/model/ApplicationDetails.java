package com.capgemini.south_node_adapter.domain.model;

import com.capgemini.south_node_adapter.domain.model.constant.OnboardStatusInfoEnum;
import com.capgemini.south_node_adapter.domain.model.constant.ResourceConsumptionEnum;
import com.capgemini.south_node_adapter.domain.model.constant.ZoneLockedEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationDetails {
	
	@JsonProperty("zoneId")
	private String zoneId = null;

	@JsonProperty("operator")
	private String operator = null;

	@JsonProperty("opCountry")
	private String opCountry = null;

	@JsonProperty("flavourId")
	private String flavourId = null;

	@JsonProperty("resourceConsumption")
	private ResourceConsumptionEnum resourceConsumption = ResourceConsumptionEnum.RESERVED_RES_AVOID;

	@JsonProperty("resPool")
	private String resPool = null;

	@JsonProperty("onboardStatusInfo")
	private OnboardStatusInfoEnum onboardStatusInfo = null;

	@JsonProperty("zoneLocked")
	private ZoneLockedEnum zoneLocked = null;
}
