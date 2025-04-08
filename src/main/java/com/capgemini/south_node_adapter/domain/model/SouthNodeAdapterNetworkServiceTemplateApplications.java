package com.capgemini.south_node_adapter.domain.model;

import com.capgemini.south_node_adapter.domain.model.constant.ResourceConsumptionEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SouthNodeAdapterNetworkServiceTemplateApplications {
	@JsonProperty("appId")

	private String appId = null;

	@JsonProperty("appVersion")

	private String appVersion = null;

	@JsonProperty("tcSlice")

	@JsonInclude(JsonInclude.Include.NON_ABSENT) // Exclude from JSON if absent
	@JsonSetter(nulls = Nulls.FAIL) // FAIL setting if the value is null
	private String tcSlice = null;

	@JsonProperty("flavourId")

	@JsonInclude(JsonInclude.Include.NON_ABSENT) // Exclude from JSON if absent
	@JsonSetter(nulls = Nulls.FAIL) // FAIL setting if the value is null
	private String flavourId = null;



	@JsonProperty("resourceConsumption")

	@JsonInclude(JsonInclude.Include.NON_ABSENT) // Exclude from JSON if absent
	@JsonSetter(nulls = Nulls.FAIL) // FAIL setting if the value is null
	private ResourceConsumptionEnum resourceConsumption = ResourceConsumptionEnum.RESERVED_RES_AVOID;
}
