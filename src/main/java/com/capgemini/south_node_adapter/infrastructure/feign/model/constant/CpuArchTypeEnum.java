package com.capgemini.south_node_adapter.infrastructure.feign.model.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CpuArchTypeEnum {

	@JsonProperty("ISA_X86")
	X86,

	@JsonProperty("ISA_X86_64")
	X86_64,

	@JsonProperty("ISA_ARM_64")
	ARM_64;
}
