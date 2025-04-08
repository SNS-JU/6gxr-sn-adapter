package com.capgemini.south_node_adapter.infrastructure.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import com.capgemini.south_node_adapter.domain.service.LoginService;
import com.capgemini.south_node_adapter.infrastructure.auth.Token;
import com.capgemini.south_node_adapter.infrastructure.feign.IEAPClientNBI;
import com.capgemini.south_node_adapter.infrastructure.feign.model.Oauth2TokenBody;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.Credential;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.Session;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.CredentialRepository;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.SessionRepository;

class LoginServiceImplTest {

	@Nested
	class LoginServiceImplTestPost {

		@Test
		void whenCorrectCredentials() {

			CredentialRepository credentialRepository = Mockito.mock(CredentialRepository.class);
			Mockito.doReturn(Optional.of(new Credential("user", "password", "appProviderId"))).when(credentialRepository)
					.findItemByUser(Mockito.anyString());

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Mockito.doReturn(new Session()).when(sessionRepository).save(Mockito.any(Session.class));

			IEAPClientNBI ieapClientNBI = Mockito.mock(IEAPClientNBI.class);
			Mockito.doReturn(new Token()).when(ieapClientNBI).createAccessToken(Mockito.any(Oauth2TokenBody.class));

			LoginService loginService = LoginServiceImpl.builder().credentialRepository(credentialRepository)
					.sessionRepository(sessionRepository).ieapClientNBI(ieapClientNBI).build();
			
			assertEquals(HttpStatus.CREATED, loginService.login("user", "password").getStatusCode());
		}
		
		@Test
		void whenInexistantCredentials() {

			CredentialRepository credentialRepository = Mockito.mock(CredentialRepository.class);
			Mockito.doReturn(Optional.empty()).when(credentialRepository)
					.findItemByUser(Mockito.anyString());

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Mockito.doReturn(new Session()).when(sessionRepository).save(Mockito.any(Session.class));

			IEAPClientNBI ieapClientNBI = Mockito.mock(IEAPClientNBI.class);
			Mockito.doReturn(new Token()).when(ieapClientNBI).createAccessToken(Mockito.any(Oauth2TokenBody.class));

			LoginService loginService = LoginServiceImpl.builder().credentialRepository(credentialRepository)
					.sessionRepository(sessionRepository).ieapClientNBI(ieapClientNBI).build();
			
			assertEquals(HttpStatus.UNAUTHORIZED, loginService.login("user", "password").getStatusCode());
		}
		
		@Test
		void whenInvalidCredentials() {

			CredentialRepository credentialRepository = Mockito.mock(CredentialRepository.class);
			Mockito.doReturn(Optional.of(new Credential("user", "pass", "appProviderId"))).when(credentialRepository)
					.findItemByUser(Mockito.anyString());

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Mockito.doReturn(new Session()).when(sessionRepository).save(Mockito.any(Session.class));

			IEAPClientNBI ieapClientNBI = Mockito.mock(IEAPClientNBI.class);
			Mockito.doReturn(new Token()).when(ieapClientNBI).createAccessToken(Mockito.any(Oauth2TokenBody.class));

			LoginService loginService = LoginServiceImpl.builder().credentialRepository(credentialRepository)
					.sessionRepository(sessionRepository).ieapClientNBI(ieapClientNBI).build();
			
			assertEquals(HttpStatus.UNAUTHORIZED, loginService.login("user", "password").getStatusCode());
		}
	}
}
