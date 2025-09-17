package com.capgemini.south_node_adapter.domain.model;

import java.util.List;

import com.capgemini.south_node_adapter.infrastructure.feign.model.IEAPApplication;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Application extends IEAPApplication {
  
  @JsonProperty("appDeploymentTcSlices")
  private List<ApplicationsAppDeploymentTcSlices> appDeploymentTcSlices;
}
