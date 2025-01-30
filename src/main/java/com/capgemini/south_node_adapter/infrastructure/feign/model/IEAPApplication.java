package com.capgemini.south_node_adapter.infrastructure.feign.model;

import java.util.List;

import com.capgemini.south_node_adapter.domain.model.InlineResponse200;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IEAPApplication extends InlineResponse200 {
	List<ZoneDetails> appDeploymentZones;
}
