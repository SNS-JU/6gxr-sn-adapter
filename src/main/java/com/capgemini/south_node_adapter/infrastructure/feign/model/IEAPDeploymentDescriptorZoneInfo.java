package com.capgemini.south_node_adapter.infrastructure.feign.model;

import com.capgemini.south_node_adapter.domain.model.constant.DeploymentSitePreference;
import com.capgemini.south_node_adapter.domain.model.constant.ResourceConsumptionEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IEAPDeploymentDescriptorZoneInfo   {
	
  @JsonProperty("zoneId")
  private String zoneId = null;

  @JsonProperty("flavourId")
  private String flavourId = null;


  @JsonProperty("resourceConsumption")
  private ResourceConsumptionEnum resourceConsumption = ResourceConsumptionEnum.RESERVED_RES_AVOID;

  @JsonProperty("resPool")
  private String resPool = null;

  @JsonProperty("operator")
  private String operator = null;

  @JsonProperty("opCountry")
  private String opCountry = null;

  @JsonProperty("deploymentSitePreference")
  private DeploymentSitePreference deploymentSitePreference = null;
}
