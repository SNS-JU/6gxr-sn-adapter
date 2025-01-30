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
	public ResponseEntity<List<NetworkServiceTemplate>> experimentGet(@NotNull @Valid String sessionId) {
		return this.experimentService.experimentGet(sessionId);
	}

	@Override
	public ResponseEntity<List<ExperimentError>> experimentPost(@NotNull @Valid String sessionId,
			@Valid NetworkServiceTemplate body) {
		
		if (!body.getTargetNode().equals(TargetNodeEnum.SOUTH)) {
			return new ResponseEntity<List<ExperimentError>>(HttpStatus.FORBIDDEN);
		}

		return this.experimentService.experimentPost(sessionId, body);
	}

	@Override
	public ResponseEntity<String> experimentTrialIdDelete(@NotNull @Valid String sessionId, String trialId) {
		return this.experimentService.experimentTrialIdDelete(sessionId, trialId);
	}

	@Override
	public ResponseEntity<NetworkServiceTemplate> experimentTrialIdGet(@NotNull @Valid String sessionId,
			String trialId) {
		return this.experimentService.experimentTrialIdGet(sessionId, trialId);
	}
}
