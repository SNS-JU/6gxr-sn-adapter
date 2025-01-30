package com.capgemini.south_node_adapter.domain.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.capgemini.south_node_adapter.domain.model.ExperimentError;
import com.capgemini.south_node_adapter.domain.model.NetworkServiceTemplate;

public interface ExperimentService {
	
	public ResponseEntity<List<NetworkServiceTemplate>> experimentGet(String sessionId);
	public ResponseEntity<List<ExperimentError>> experimentPost(String sessionId, NetworkServiceTemplate body);
	public ResponseEntity<String> experimentTrialIdDelete(String sessionId, String trialId);
	public ResponseEntity<NetworkServiceTemplate> experimentTrialIdGet(String sessionId, String trialId);
}
