package com.capgemini.south_node_adapter.domain.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InlineResponse200   {
  @JsonProperty("appId")

  private String appId = null;

  @JsonProperty("appDeploymentTcSlices")
  @Valid
  private List<ApplicationsAppDeploymentTcSlices> appDeploymentTcSlices = null;
  @JsonProperty("appMetaData")

  private AppMetaData appMetaData = null;

  @JsonProperty("appQoSProfile")

  private AppQoSProfile appQoSProfile = null;

  @JsonProperty("appComponentSpecs")
  @Valid
  private List<AppComponentSpecs> appComponentSpecs = new ArrayList<AppComponentSpecs>();
}
