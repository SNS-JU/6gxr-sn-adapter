package com.capgemini.south_node_adapter.infrastructure.persistence.entity;

public record SouthNodeExperimentIEAPInstance(String appId, String appInstanceId, String zoneId, String operator,
		String opCountry) {
}
