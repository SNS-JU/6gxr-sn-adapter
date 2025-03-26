package com.capgemini.south_node_adapter.infrastructure.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.capgemini.south_node_adapter.domain.service.RegisterService;
import com.capgemini.south_node_adapter.infrastructure.feign.IEAPClientNBI;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.Credential;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.CredentialRepository;

import lombok.Builder;

@Builder
public class RegisterServiceImpl implements RegisterService {

	IEAPClientNBI ieapClientNBI;

	CredentialRepository credentialRepository;

	@Override
	public ResponseEntity<Void> registerDelete(String user, String password, String appProviderId) {

		try {
			credentialRepository.delete(new Credential(user, password, appProviderId));
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<Void> registerPost(String user, String password, String appProviderId) {
		
		Credential newUser = new Credential(user, password, appProviderId);
		if(credentialRepository.existsById(user)) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		
		try {
			credentialRepository.save(newUser);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
