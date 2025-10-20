package com.capgemini.south_node_adapter.domain.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.capgemini.south_node_adapter.domain.model.Application;

public interface ApplicationsService {
	public ResponseEntity<List<Application>> viewAllApplication(String sessionId);
}
