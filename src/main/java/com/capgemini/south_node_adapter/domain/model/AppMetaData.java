package com.capgemini.south_node_adapter.domain.model;

import com.capgemini.south_node_adapter.domain.model.constant.AppCategory;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppMetaData   {
  @JsonProperty("appName")
  private String appName = null;

  @JsonProperty("version")
  private String version = null;

  @JsonProperty("appDescription")
  private String appDescription = null;

  @JsonProperty("mobilitySupport")
  private Boolean mobilitySupport = false;

  @JsonProperty("AccessToken")
  private String accessToken = null;

  @JsonProperty("category")
  private AppCategory category = null;
}
