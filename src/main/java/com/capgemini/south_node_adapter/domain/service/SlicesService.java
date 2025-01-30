package com.capgemini.south_node_adapter.domain.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

public interface SlicesService {
	public ResponseEntity<List<String>> slicesGet(String sessionId);
}
