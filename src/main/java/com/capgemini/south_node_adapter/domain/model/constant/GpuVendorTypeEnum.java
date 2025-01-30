package com.capgemini.south_node_adapter.domain.model.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GpuVendorTypeEnum {
	
	@JsonProperty("GPU_PROVIDER_NVIDIA")
	NVIDIA,

	@JsonProperty("GPU_PROVIDER_AMD")
	AMD;
}
