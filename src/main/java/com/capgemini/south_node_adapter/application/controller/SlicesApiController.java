package com.capgemini.south_node_adapter.application.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.south_node_adapter.application.api.SlicesApi;
import com.capgemini.south_node_adapter.domain.service.SlicesService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
public class SlicesApiController implements SlicesApi {
	
	@Autowired
	SlicesService slicesService;

	public ResponseEntity<List<String>> slicesGet(
			@NotNull @Parameter(in = ParameterIn.QUERY, description = "", required = true, schema = @Schema()) @Valid @RequestParam(value = "sessionId", required = true) String sessionId) {
		return this.slicesService.slicesGet(sessionId);
	}

}
