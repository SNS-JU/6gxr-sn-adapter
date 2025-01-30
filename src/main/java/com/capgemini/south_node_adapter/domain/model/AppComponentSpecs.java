package com.capgemini.south_node_adapter.domain.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppComponentSpecs   {
  @JsonProperty("serviceNameNB")
  private String serviceNameNB = null;

  @JsonProperty("serviceNameEW")
  private String serviceNameEW = null;

  @JsonProperty("componentName")
  private String componentName = null;

  @JsonProperty("artifactId")
  private UUID artifactId = null;
}
