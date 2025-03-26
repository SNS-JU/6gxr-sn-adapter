package com.capgemini.south_node_adapter.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.south_node_adapter.application.api.RegisterApi;
import com.capgemini.south_node_adapter.domain.service.RegisterService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
public class RegisterApiController implements RegisterApi {

	@Autowired
	RegisterService registerService;

	@Override
	public ResponseEntity<Void> registerDelete(@NotNull @Valid String user, @NotNull @Valid String password,
			@NotNull @Valid String appProviderId) {
		return this.registerService.registerDelete(user, password, appProviderId);
	}

	@Override
	public ResponseEntity<Void> registerPost(@NotNull @Valid String user, @NotNull @Valid String password,
			@NotNull @Valid String appProviderId) {
		return this.registerService.registerPost(user, password, appProviderId);
	}

}
