package com.capgemini.south_node_adapter.infrastructure.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.capgemini.south_node_adapter.domain.service.GpsisService;
import com.capgemini.south_node_adapter.infrastructure.configuration.XRProperties;

import lombok.Builder;

@Builder
public class GpsisServiceImpl implements GpsisService {
	
	XRProperties xrProperties;

	@Override
	public ResponseEntity<List<String>> gpsisGet(String sessionId) {
		return new ResponseEntity<>(xrProperties.getGpsis(), HttpStatus.OK);
	}
}
