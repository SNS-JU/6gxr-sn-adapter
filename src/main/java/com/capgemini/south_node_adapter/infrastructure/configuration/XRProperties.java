package com.capgemini.south_node_adapter.infrastructure.configuration;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties
public class XRProperties {

	private List<String> gpsis;
	private Map<String, TCSlice> tcSlices;
}
