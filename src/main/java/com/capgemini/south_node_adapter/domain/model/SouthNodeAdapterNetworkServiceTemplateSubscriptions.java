package com.capgemini.south_node_adapter.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SouthNodeAdapterNetworkServiceTemplateSubscriptions {
	@JsonProperty("tcSlice")

	private String tcSlice = null;

	@JsonProperty("gpsi")

	private String gpsi = null;
}
