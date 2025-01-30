package com.capgemini.south_node_adapter.application.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.south_node_adapter.application.api.ApplicationsApi;
import com.capgemini.south_node_adapter.domain.model.InlineResponse200;
import com.capgemini.south_node_adapter.domain.service.ApplicationsService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
public class ApplicationsApiController implements ApplicationsApi {

	@Autowired
	ApplicationsService applicationsService;

	@Override
	public ResponseEntity<List<InlineResponse200>> viewAllApplication(@NotNull @Valid String sessionId) {
		return this.applicationsService.viewAllApplication(sessionId);
	}
}
