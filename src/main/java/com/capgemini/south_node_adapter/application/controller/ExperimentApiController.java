package com.capgemini.south_node_adapter.application.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.south_node_adapter.application.api.ExperimentApi;
import com.capgemini.south_node_adapter.domain.model.ExperimentError;
import com.capgemini.south_node_adapter.domain.model.NetworkServiceTemplate;
import com.capgemini.south_node_adapter.domain.model.constant.TargetNodeEnum;
import com.capgemini.south_node_adapter.domain.service.ExperimentService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
public class ExperimentApiController implements ExperimentApi {

	@Autowired
	ExperimentService experimentService;

	@Override
	public ResponseEntity<Void> experimentExperimentNameDelete(@NotNull @Valid String sessionId,
			String experimentName) {
		return this.experimentService.experimentExperimentNameDelete(sessionId, experimentName);	}

	@Override
	public ResponseEntity<NetworkServiceTemplate> experimentExperimentNameGet(@NotNull @Valid String sessionId,
			String experimentName) {
		return this.experimentService.experimentExperimentNameGet(sessionId, experimentName);
	}

	@Override
	public ResponseEntity<List<NetworkServiceTemplate>> experimentGet(@NotNull @Valid String sessionId) {
		return this.experimentService.experimentGet(sessionId);
	}

	@Override
	public ResponseEntity<Void> experimentPost(@NotNull @Valid String sessionId, @Valid NetworkServiceTemplate body) {
		
		if (!body.getTargetNode().equals(TargetNodeEnum.SOUTH)) {
			return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
		}

		return this.experimentService.experimentPost(sessionId, body);
	}

	@Override
	public ResponseEntity<List<ExperimentError>> experimentRunExperimentNamePost(@NotNull @Valid String sessionId,
			String experimentName) {
		return this.experimentService.experimentRunExperimentNamePost(sessionId, experimentName);
	}

	@Override
	public ResponseEntity<List<ExperimentError>> experimentTerminateExperimentNameDelete(
			@NotNull @Valid String sessionId, String experimentName) {
		return this.experimentService.experimentTerminateExperimentNameDelete(sessionId, experimentName);
	}
}
