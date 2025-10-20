package com.capgemini.south_node_adapter.infrastructure.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.capgemini.south_node_adapter.domain.model.LoginDetails;
import com.capgemini.south_node_adapter.domain.service.LoginService;
import com.capgemini.south_node_adapter.infrastructure.auth.Token;
import com.capgemini.south_node_adapter.infrastructure.configuration.XRProperties;
import com.capgemini.south_node_adapter.infrastructure.feign.IEAPClientNBI;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.Credential;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.Session;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.CredentialRepository;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.SessionRepository;

import feign.FeignException;
import lombok.Builder;

@Builder
public class LoginServiceImpl implements LoginService {

	IEAPClientNBI ieapClientNBI;

	CredentialRepository credentialRepository;

	SessionRepository sessionRepository;
	
	XRProperties xrProperties;

	@Override
	public ResponseEntity<Void> loginDelete(String sessionId) {
		this.sessionRepository.deleteById(sessionId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<LoginDetails> login(String user, String password) {

		Optional<Credential> credential = this.credentialRepository.findItemByUser(user);
		if (credential.isEmpty() || !credential.get().getPassword().equals(password)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		try {

			Token newIeapToken = ieapClientNBI.createAccessToken(xrProperties.getIeapAccess().getCredentials());
			newIeapToken.setAccessToken(xrProperties.getIeapAccess().getBearerPrefix() + StringUtils.SPACE + newIeapToken.getAccessToken());

			Session session = new Session(user, credential.get().getAppProviderId(), newIeapToken, LocalDateTime.now(),
					LocalDateTime.now());
			Session savedSession = this.sessionRepository.save(session);

			LoginDetails loginDetails = new LoginDetails();
			loginDetails.setSessionId(savedSession.getSessionId());

			return new ResponseEntity<>(loginDetails, HttpStatus.CREATED);
		} catch (FeignException e) {
			return new ResponseEntity<>(HttpStatus.valueOf(e.status()));
		}
	}
}