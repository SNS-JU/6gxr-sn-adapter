package com.capgemini.south_node_adapter.infrastructure.configuration;

import com.capgemini.south_node_adapter.infrastructure.feign.model.Oauth2TokenBody;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IEAPAccess {
	
	private Oauth2TokenBody credentials;
	private String bearerPrefix;
	private String defaultDomain;
}
