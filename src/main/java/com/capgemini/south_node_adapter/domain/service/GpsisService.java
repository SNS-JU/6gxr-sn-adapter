package com.capgemini.south_node_adapter.domain.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

public interface GpsisService {
	public ResponseEntity<List<String>> gpsisGet(String sessionId);
}
