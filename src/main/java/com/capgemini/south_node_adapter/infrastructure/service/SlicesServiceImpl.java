package com.capgemini.south_node_adapter.infrastructure.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.capgemini.south_node_adapter.domain.service.SlicesService;
import com.capgemini.south_node_adapter.infrastructure.configuration.XRProperties;

import lombok.Builder;

@Builder
public class SlicesServiceImpl implements SlicesService {

	XRProperties xrProperties;

	@Override
	public ResponseEntity<List<String>> slicesGet(String sessionId) {
		return new ResponseEntity<>(xrProperties.getTcSlices().keySet().stream().toList(), HttpStatus.OK);
	}

}
