package com.capgemini.south_node_adapter.infrastructure.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.capgemini.south_node_adapter.domain.service.RegisterService;
import com.capgemini.south_node_adapter.infrastructure.auth.Token;
import com.capgemini.south_node_adapter.infrastructure.feign.IEAPClientNBI;
import com.capgemini.south_node_adapter.infrastructure.feign.model.EnterpriseDetails;
import com.capgemini.south_node_adapter.infrastructure.feign.model.Oauth2TokenBody;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.Credential;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.CredentialRepository;

class RegisterServiceImplTest {

	@Nested
	class RegisterServiceImplTestPost {

		@Test
		void whenCredentialSaveSuccess() {

			CredentialRepository credentialRepository = Mockito.mock(CredentialRepository.class);
			Mockito.doReturn(new Credential(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY))
					.when(credentialRepository).save(Mockito.any(Credential.class));

			IEAPClientNBI ieapClientNBINBI = Mockito.mock(IEAPClientNBI.class);
			EnterpriseDetails user = new EnterpriseDetails();
			Mockito.doReturn(user).when(ieapClientNBINBI).getUser(Mockito.anyString(), Mockito.anyString());
			Mockito.doReturn(new Token()).when(ieapClientNBINBI).createAccessToken(Mockito.any(Oauth2TokenBody.class));

			RegisterService registerService = RegisterServiceImpl.builder().ieapClientNBI(ieapClientNBINBI)
					.credentialRepository(credentialRepository).build();
			ResponseEntity<Void> response = registerService.registerPost(StringUtils.EMPTY, StringUtils.EMPTY,
					StringUtils.EMPTY);

			assertEquals(HttpStatus.CREATED, response.getStatusCode());
		}

		@Test
		void whenCredentialSaveFailure() {

			CredentialRepository credentialRepository = Mockito.mock(CredentialRepository.class);
			Mockito.doThrow(new RuntimeException()).when(credentialRepository).save(Mockito.any(Credential.class));

			IEAPClientNBI ieapClientNBINBI = Mockito.mock(IEAPClientNBI.class);
			EnterpriseDetails user = new EnterpriseDetails();
			Mockito.doReturn(user).when(ieapClientNBINBI).getUser(Mockito.anyString(), Mockito.anyString());
			Mockito.doReturn(new Token()).when(ieapClientNBINBI).createAccessToken(Mockito.any(Oauth2TokenBody.class));

			RegisterService registerService = RegisterServiceImpl.builder().ieapClientNBI(ieapClientNBINBI)
					.credentialRepository(credentialRepository).build();
			ResponseEntity<Void> response = registerService.registerPost(StringUtils.EMPTY, StringUtils.EMPTY,
					StringUtils.EMPTY);

			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		}
	}

	@Nested
	class RegisterServiceImplTestDelete {

		@Test
		void whenCredentialDeleteSuccess() {

			CredentialRepository credentialRepository = Mockito.mock(CredentialRepository.class);
			Mockito.doNothing().when(credentialRepository).delete(Mockito.any(Credential.class));

			RegisterService registerService = RegisterServiceImpl.builder().credentialRepository(credentialRepository)
					.build();
			ResponseEntity<Void> response = registerService.registerDelete(StringUtils.EMPTY, StringUtils.EMPTY,
					StringUtils.EMPTY);

			assertEquals(HttpStatus.OK, response.getStatusCode());
		}

		@Test
		void whenCredentialDeleteFailure() {

			CredentialRepository credentialRepository = Mockito.mock(CredentialRepository.class);
			Mockito.doThrow(new RuntimeException()).when(credentialRepository).delete(Mockito.any(Credential.class));

			RegisterService registerService = RegisterServiceImpl.builder().credentialRepository(credentialRepository)
					.build();
			ResponseEntity<Void> response = registerService.registerDelete(StringUtils.EMPTY, StringUtils.EMPTY,
					StringUtils.EMPTY);

			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		}
	}
}
