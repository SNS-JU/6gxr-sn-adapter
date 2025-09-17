package com.capgemini.south_node_adapter.infrastructure.feign.model;

import com.capgemini.south_node_adapter.domain.model.constant.AppCategory;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IEAPApplicationAppMetaData   {
  
  @JsonProperty("appName")
  private String appName;

  @JsonProperty("version")
  private String version;

  @JsonProperty("appDescription")
  private String appDescription;

  @JsonProperty("mobilitySupport")
  private Boolean mobilitySupport;

  @JsonProperty("AccessToken")
  private String accessToken;

  @JsonProperty("category")
  private AppCategory category;
}
