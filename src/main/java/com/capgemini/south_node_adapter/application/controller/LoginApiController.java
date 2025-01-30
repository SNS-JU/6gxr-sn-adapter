package com.capgemini.south_node_adapter.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.south_node_adapter.application.api.LoginApi;
import com.capgemini.south_node_adapter.domain.model.LoginDetails;
import com.capgemini.south_node_adapter.domain.service.LoginService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
public class LoginApiController implements LoginApi {
	
	@Autowired
	LoginService loginService;

	@Override
	public ResponseEntity<Void> loginDelete(@NotNull @Valid String sessionId) {
		return this.loginService.loginDelete(sessionId);
	}

	@Override
	public ResponseEntity<LoginDetails> loginPost(@NotNull @Valid String user, @NotNull @Valid String password) {
		return this.loginService.login(user, password);
	}

}
