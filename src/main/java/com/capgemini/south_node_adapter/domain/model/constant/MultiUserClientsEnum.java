package com.capgemini.south_node_adapter.domain.model.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MultiUserClientsEnum {

	@JsonProperty("APP_TYPE_SINGLE_USER")
	SINGLE_USER,

	@JsonProperty("APP_TYPE_MULTI_USER")
	MULTI_USER;
}