package com.capgemini.south_node_adapter.infrastructure.feign.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Oauth2TokenBody {

  @JsonProperty("grant_type")
  private String grantType;

  @JsonProperty("client_id")
  private String clientId;

  @JsonProperty("client_secret")
  private String clientSecret;

  private String scope;

}
