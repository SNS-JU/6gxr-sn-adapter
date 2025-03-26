package com.capgemini.south_node_adapter.infrastructure.feign.model;

import java.util.ArrayList;
import java.util.List;

import com.capgemini.south_node_adapter.domain.model.GpuInfo;
import com.capgemini.south_node_adapter.domain.model.HugePage;
import com.capgemini.south_node_adapter.domain.model.OSType;
import com.capgemini.south_node_adapter.infrastructure.feign.model.constant.CpuArchTypeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Flavour {

	@JsonProperty("flavourId")
	private String flavourId = null;

	@JsonProperty("cpuArchType")
	private CpuArchTypeEnum cpuArchType = null;

	@JsonProperty("supportedOSTypes")
	@Valid
	private List<OSType> supportedOSTypes = new ArrayList<OSType>();

	@JsonProperty("numCPU")
	private Integer numCPU = null;

	@JsonProperty("memorySize")
	private Integer memorySize = null;

	@JsonProperty("storageSize")
	private Integer storageSize = null;

	@JsonProperty("gpu")
	@Valid
	private List<GpuInfo> gpu = null;

	@JsonProperty("vpu")
	private Integer vpu = null;

	@JsonProperty("hugepages")
	@Valid
	private List<HugePage> hugepages = null;

	@JsonProperty("cpuExclusivity")
	private Boolean cpuExclusivity = null;
}
