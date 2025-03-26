package com.capgemini.south_node_adapter.domain.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.capgemini.south_node_adapter.domain.model.InlineResponse200;

public interface ApplicationsService {
	public ResponseEntity<List<InlineResponse200>> viewAllApplication(String sessionId);
}
