package com.capgemini.south_node_adapter.infrastructure.feign.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IEAPApplicationAppComponentSpecs   {
  @JsonProperty("serviceNameNB")
  private String serviceNameNB;

  @JsonProperty("serviceNameEW")
  private String serviceNameEW;

  @JsonProperty("componentName")
  private String componentName;

  @JsonProperty("artifactId")
  private String artifactId;
}
