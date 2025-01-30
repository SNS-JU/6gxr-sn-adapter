package com.capgemini.south_node_adapter.domain.model;

import com.capgemini.south_node_adapter.domain.model.constant.PageSizeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HugePage {

	@JsonProperty("pageSize")
	private PageSizeEnum pageSize = null;

	@JsonProperty("number")
	private Integer number = null;
}
