package com.capgemini.south_node_adapter.domain.model;

import com.capgemini.south_node_adapter.domain.model.constant.GpuVendorTypeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GpuInfo {

	@JsonProperty("gpuVendorType")
	private GpuVendorTypeEnum gpuVendorType = null;

	@JsonProperty("gpuModeName")
	private String gpuModeName = null;

	@JsonProperty("gpuMemory")
	private Integer gpuMemory = null;

	@JsonProperty("numGPU")
	private Integer numGPU = null;
}
