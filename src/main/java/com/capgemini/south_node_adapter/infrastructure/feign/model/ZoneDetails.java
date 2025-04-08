package com.capgemini.south_node_adapter.infrastructure.feign.model;

import java.util.List;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Validated
@Getter
@Setter
public class ZoneDetails {
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
	@Valid
	private List<Flavour> supportedFlavours;
}
