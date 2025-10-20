package com.capgemini.south_node_adapter.infrastructure.feign.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IEAPZoneDetails {
	@JsonProperty("zoneId")
	private String zoneId;

	@JsonProperty("geolocation")
	private String geolocation;

	@JsonProperty("geographyDetails")
	private String geographyDetails;

	@JsonProperty("operator")
	private String operator;

	@JsonProperty("opCountry")
	private String opCountry;

	@JsonProperty("PartnerOPZone")
	private Boolean partnerOPZone;

	@JsonProperty("supportedFlavours")
	private List<Flavour> supportedFlavours;
}
