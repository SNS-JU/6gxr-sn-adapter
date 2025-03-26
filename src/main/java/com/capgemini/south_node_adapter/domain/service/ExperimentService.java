package com.capgemini.south_node_adapter.domain.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.capgemini.south_node_adapter.domain.model.ExperimentError;
import com.capgemini.south_node_adapter.domain.model.NetworkServiceTemplate;

public interface ExperimentService {

	public ResponseEntity<Void> experimentExperimentNameDelete(String sessionId, String experimentName);
	public ResponseEntity<NetworkServiceTemplate> experimentExperimentNameGet(String sessionId, String experimentName);
	public ResponseEntity<List<NetworkServiceTemplate>> experimentGet(String sessionId);
	public ResponseEntity<Void> experimentPost(String sessionId, NetworkServiceTemplate body);
	public ResponseEntity<List<ExperimentError>> experimentRunExperimentNamePost(String sessionId, String experimentName);
	public ResponseEntity<List<ExperimentError>> experimentTerminateExperimentNameDelete(String sessionId, String experimentName);
	public ResponseEntity<Void> experimentEndTrialTrialIdDelete(Integer trialId);
}
