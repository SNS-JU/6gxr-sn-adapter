package com.capgemini.south_node_adapter.infrastructure.feign.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Flavour {

	@JsonProperty("flavourId")
	private String flavourId = null;
}
