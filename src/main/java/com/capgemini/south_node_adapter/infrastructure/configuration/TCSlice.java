package com.capgemini.south_node_adapter.infrastructure.configuration;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TCSlice {
	String ieapZone;
	String nefSlice;
	Map<String, String> allowedFlavours;
}