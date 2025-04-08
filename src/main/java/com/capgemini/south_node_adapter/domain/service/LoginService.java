package com.capgemini.south_node_adapter.domain.service;

import org.springframework.http.ResponseEntity;

import com.capgemini.south_node_adapter.domain.model.LoginDetails;

public interface LoginService {

	public ResponseEntity<LoginDetails> login(String user, String password);
	public ResponseEntity<Void> loginDelete(String sessionId);
}
