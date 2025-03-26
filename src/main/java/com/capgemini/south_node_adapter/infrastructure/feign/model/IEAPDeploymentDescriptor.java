package com.capgemini.south_node_adapter.infrastructure.feign.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IEAPDeploymentDescriptor   {
  @JsonProperty("appId")
  private String appId = null;

  @JsonProperty("appVersion")
  private String appVersion = null;

  @JsonProperty("appProviderId")
  private String appProviderId = null;

  @JsonProperty("zoneInfo")
  private IEAPDeploymentDescriptorZoneInfo zoneInfo = null;
}
