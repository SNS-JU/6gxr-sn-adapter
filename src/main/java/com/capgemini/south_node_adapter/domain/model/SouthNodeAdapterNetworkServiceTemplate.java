package com.capgemini.south_node_adapter.domain.model;

import java.util.List;

import com.capgemini.south_node_adapter.domain.model.constant.StatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SouthNodeAdapterNetworkServiceTemplate {
	
	@JsonProperty("applications")
	@Valid
	private List<SouthNodeAdapterNetworkServiceTemplateApplications> applications = null;
	@JsonProperty("subscriptions")
	@Valid
	private List<SouthNodeAdapterNetworkServiceTemplateSubscriptions> subscriptions = null;

	@JsonProperty("status")

	@JsonInclude(JsonInclude.Include.NON_ABSENT) // Exclude from JSON if absent
	@JsonSetter(nulls = Nulls.FAIL) // FAIL setting if the value is null
	private StatusEnum status = null;
}
