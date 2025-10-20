package com.capgemini.south_node_adapter.infrastructure.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.capgemini.south_node_adapter.domain.model.ExperimentError;
import com.capgemini.south_node_adapter.domain.model.NetworkServiceTemplate;
import com.capgemini.south_node_adapter.domain.model.SouthNodeAdapterNetworkServiceTemplate;
import com.capgemini.south_node_adapter.domain.model.SouthNodeAdapterNetworkServiceTemplateApplications;
import com.capgemini.south_node_adapter.domain.model.SouthNodeAdapterNetworkServiceTemplateSubscriptions;
import com.capgemini.south_node_adapter.domain.model.constant.StatusEnum;
import com.capgemini.south_node_adapter.domain.service.ExperimentService;
import com.capgemini.south_node_adapter.infrastructure.auth.Token;
import com.capgemini.south_node_adapter.infrastructure.configuration.IEAPAccess;
import com.capgemini.south_node_adapter.infrastructure.configuration.TCSlice;
import com.capgemini.south_node_adapter.infrastructure.configuration.XRProperties;
import com.capgemini.south_node_adapter.infrastructure.feign.IEAPClientNBI;
import com.capgemini.south_node_adapter.infrastructure.feign.NEFClient;
import com.capgemini.south_node_adapter.infrastructure.feign.model.EnterpriseDetails;
import com.capgemini.south_node_adapter.infrastructure.feign.model.Oauth2TokenBody;
import com.capgemini.south_node_adapter.infrastructure.feign.model.IEAPZoneDetails;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.ExperimentLock;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.Session;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.SouthNodeExperiment;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.SouthNodeExperimentIEAPInstance;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.LockRepository;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.SessionRepository;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.SouthNodeNSTRepository;

class ExperimentServiceImplTest {

	@Nested
	class ExperimentServiceImplTestGet {

		@Test
		void whenSessionExists() {

			XRProperties xrProperties = new XRProperties();
			xrProperties.setSessionValidTimeInSeconds(300l);

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Session session = new Session("user", "appProviderId", new Token("accessToken", "tokenType", 1, "scope"),
					LocalDateTime.now(), LocalDateTime.now());
			Mockito.doReturn(Optional.of(session)).when(sessionRepository).findById(Mockito.anyString());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			Mockito.doReturn(List.of(new SouthNodeExperiment())).when(southNodeNSTRepository)
					.findByUser(Mockito.anyString());

			ExperimentService experimentService = ExperimentServiceImpl.builder().sessionRepository(sessionRepository)
					.xrproperties(xrProperties).southNodeNSTRepository(southNodeNSTRepository).build();

			ResponseEntity<List<NetworkServiceTemplate>> response = experimentService.experimentGet(StringUtils.EMPTY);
			assertEquals(HttpStatus.OK, response.getStatusCode());
		}

		@Test
		void whenInexistantSession() {

			XRProperties xrProperties = new XRProperties();
			xrProperties.setSessionValidTimeInSeconds(300l);

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Mockito.doReturn(Optional.empty()).when(sessionRepository).findById(Mockito.anyString());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			Mockito.doReturn(List.of(new SouthNodeExperiment())).when(southNodeNSTRepository)
					.findByUser(Mockito.anyString());

			ExperimentService experimentService = ExperimentServiceImpl.builder().sessionRepository(sessionRepository)
					.xrproperties(xrProperties).southNodeNSTRepository(southNodeNSTRepository).build();

			ResponseEntity<List<NetworkServiceTemplate>> response = experimentService.experimentGet(StringUtils.EMPTY);
			assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		}

		@Test
		void whenSessionExpired() {

			XRProperties xrProperties = new XRProperties();
			xrProperties.setSessionValidTimeInSeconds(300l);

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Session session = new Session("user", "appProviderId", new Token("accessToken", "tokenType", 1, "scope"),
					LocalDateTime.now().plusSeconds(1), LocalDateTime.now().minusSeconds(300l));
			Mockito.doReturn(Optional.of(session)).when(sessionRepository).findById(Mockito.anyString());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			Mockito.doReturn(List.of(new SouthNodeExperiment())).when(southNodeNSTRepository)
					.findByUser(Mockito.anyString());

			ExperimentService experimentService = ExperimentServiceImpl.builder().sessionRepository(sessionRepository)
					.xrproperties(xrProperties).southNodeNSTRepository(southNodeNSTRepository).build();

			ResponseEntity<List<NetworkServiceTemplate>> response = experimentService.experimentGet(StringUtils.EMPTY);
			assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		}
	}

	@Nested
	class ExperimentServiceImplTestPost {

		@Test
		void whenSessionExists() {

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Session session = new Session("user", "appProviderId", new Token("accessToken", "tokenType", 1, "scope"),
					LocalDateTime.now(), LocalDateTime.now());
			Mockito.doReturn(Optional.of(session)).when(sessionRepository).findById(Mockito.anyString());

			IEAPClientNBI ieapClientNBI = Mockito.mock(IEAPClientNBI.class);
			Mockito.doReturn(new Token("accessToken", "tokenType", 1, "scope")).when(ieapClientNBI)
					.createAccessToken(Mockito.any(Oauth2TokenBody.class));
			EnterpriseDetails user = new EnterpriseDetails();
			Mockito.doReturn(user).when(ieapClientNBI).getUser(Mockito.anyString(), Mockito.anyString());
			IEAPZoneDetails zoneDetails = new IEAPZoneDetails();
			zoneDetails.setZoneId("zoneId");
			Mockito.doReturn(List.of(zoneDetails)).when(ieapClientNBI).getZones(Mockito.anyString(),
					Mockito.anyString());
			Mockito.doReturn(List.of(StringUtils.EMPTY, StringUtils.EMPTY)).when(ieapClientNBI).launchApplication(
					Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.any(), Mockito.any());

			NEFClient nefClient = Mockito.mock(NEFClient.class);

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			Mockito.doReturn(null).when(southNodeNSTRepository).save(Mockito.any(SouthNodeExperiment.class));

			XRProperties properties = new XRProperties();
			Map<String, TCSlice> tcSliceMap = new HashMap<String, TCSlice>();
			TCSlice sampleTcSlice = new TCSlice();
			sampleTcSlice.setIeapZone("zoneId");
			tcSliceMap.put("tcSlice", sampleTcSlice);
			properties.setTcSlices(tcSliceMap);
			properties.setSessionValidTimeInSeconds(300l);

			SouthNodeAdapterNetworkServiceTemplateApplications sample = new SouthNodeAdapterNetworkServiceTemplateApplications();
			sample.setTcSlice("tcSlice");
			SouthNodeAdapterNetworkServiceTemplateSubscriptions sampleSub = new SouthNodeAdapterNetworkServiceTemplateSubscriptions();
			sampleSub.setTcSlice("tcSlice");
			SouthNodeAdapterNetworkServiceTemplate southNodeAdapterNetworkServiceTemplate = new SouthNodeAdapterNetworkServiceTemplate();
			southNodeAdapterNetworkServiceTemplate.setApplications(List.of(sample, sample, sample));
			southNodeAdapterNetworkServiceTemplate.setSubscriptions(List.of(sampleSub, sampleSub, sampleSub));
			NetworkServiceTemplate networkServiceTemplate = new NetworkServiceTemplate();
			networkServiceTemplate.setExperimentName("TEST");
			networkServiceTemplate.setSouthNodeAdapterNetworkServiceTemplate(southNodeAdapterNetworkServiceTemplate);

			ExperimentService experimentService = ExperimentServiceImpl.builder().ieapClientNBI(ieapClientNBI)
					.sessionRepository(sessionRepository).southNodeNSTRepository(southNodeNSTRepository)
					.nefClient(nefClient).xrproperties(properties).build();

			ResponseEntity<Void> response = experimentService.experimentPost(StringUtils.EMPTY, networkServiceTemplate);
			assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		}

		@Test
		void whenInexistantSession() {

			XRProperties xrProperties = new XRProperties();
			xrProperties.setSessionValidTimeInSeconds(300l);

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Mockito.doReturn(Optional.empty()).when(sessionRepository).findById(Mockito.anyString());

			IEAPClientNBI ieapClientNBI = Mockito.mock(IEAPClientNBI.class);
			Mockito.doReturn(List.of(StringUtils.EMPTY, StringUtils.SPACE)).when(ieapClientNBI).launchApplication(
					Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
					Mockito.anyString(), Mockito.anyString());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			Mockito.doReturn(null).when(southNodeNSTRepository).save(Mockito.any(SouthNodeExperiment.class));

			ExperimentService experimentService = ExperimentServiceImpl.builder().ieapClientNBI(ieapClientNBI)
					.xrproperties(xrProperties).sessionRepository(sessionRepository)
					.southNodeNSTRepository(southNodeNSTRepository).build();

			ResponseEntity<Void> response = experimentService.experimentPost(StringUtils.EMPTY, null);
			assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		}

		@Test
		void whenSessionExpired() {

			XRProperties xrProperties = new XRProperties();
			xrProperties.setSessionValidTimeInSeconds(300l);

			Session session = new Session("user", "appProviderId", new Token("accessToken", "tokenType", 1, "scope"),
					LocalDateTime.now().plusSeconds(1), LocalDateTime.now().minusSeconds(300l));
			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Mockito.doReturn(Optional.of(session)).when(sessionRepository).findById(Mockito.anyString());

			IEAPClientNBI ieapClientNBI = Mockito.mock(IEAPClientNBI.class);
			Mockito.doReturn(List.of(StringUtils.EMPTY, StringUtils.SPACE)).when(ieapClientNBI).launchApplication(
					Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
					Mockito.anyString(), Mockito.anyString());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			Mockito.doReturn(null).when(southNodeNSTRepository).save(Mockito.any(SouthNodeExperiment.class));

			ExperimentService experimentService = ExperimentServiceImpl.builder().ieapClientNBI(ieapClientNBI)
					.xrproperties(xrProperties).sessionRepository(sessionRepository)
					.southNodeNSTRepository(southNodeNSTRepository).build();

			ResponseEntity<Void> response = experimentService.experimentPost(StringUtils.EMPTY, null);
			assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		}
	}

	@Nested
	class ExperimentServiceImplTestIdGet {

		@Test
		void whenSessionExists() {

			XRProperties xrProperties = new XRProperties();
			xrProperties.setSessionValidTimeInSeconds(300l);

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Session session = new Session("user", "appProviderId", new Token("accessToken", "tokenType", 1, "scope"),
					LocalDateTime.now(), LocalDateTime.now());
			Mockito.doReturn(Optional.of(session)).when(sessionRepository).findById(Mockito.anyString());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			Mockito.doReturn(new SouthNodeExperiment()).when(southNodeNSTRepository)
					.findByUserAndExperimentName(Mockito.anyString(), Mockito.anyString());

			ExperimentService experimentService = ExperimentServiceImpl.builder().sessionRepository(sessionRepository)
					.xrproperties(xrProperties).southNodeNSTRepository(southNodeNSTRepository).build();

			ResponseEntity<NetworkServiceTemplate> response = experimentService
					.experimentExperimentNameGet(StringUtils.EMPTY, StringUtils.EMPTY);
			assertEquals(HttpStatus.OK, response.getStatusCode());
		}

		@Test
		void whenInexistantSession() {

			XRProperties xrProperties = new XRProperties();
			xrProperties.setSessionValidTimeInSeconds(300l);

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Mockito.doReturn(Optional.empty()).when(sessionRepository).findById(Mockito.anyString());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			Mockito.doReturn(new SouthNodeExperiment()).when(southNodeNSTRepository)
					.findByUserAndExperimentName(Mockito.anyString(), Mockito.anyString());

			ExperimentService experimentService = ExperimentServiceImpl.builder().sessionRepository(sessionRepository)
					.xrproperties(xrProperties).southNodeNSTRepository(southNodeNSTRepository).build();

			ResponseEntity<NetworkServiceTemplate> response = experimentService
					.experimentExperimentNameGet(StringUtils.EMPTY, StringUtils.EMPTY);
			assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		}
	}

	@Nested
	class ExperimentServiceImplTestIdDelete {

		@Test
		void whenSessionExists() {

			XRProperties xrProperties = new XRProperties();
			xrProperties.setSessionValidTimeInSeconds(300l);
			xrProperties.setExperimentLockId(StringUtils.EMPTY);

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Session session = new Session("user", "appProviderId", new Token("accessToken", "tokenType", 1, "scope"),
					LocalDateTime.now(), LocalDateTime.now());
			Mockito.doReturn(Optional.of(session)).when(sessionRepository).findById(Mockito.anyString());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			SouthNodeExperimentIEAPInstance instance = new SouthNodeExperimentIEAPInstance(StringUtils.EMPTY,
					StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
			SouthNodeExperiment toBeDeleted = new SouthNodeExperiment();
			toBeDeleted.setIeapInstances(List.of(instance));
			Mockito.doReturn(toBeDeleted).when(southNodeNSTRepository).findByUserAndExperimentName(Mockito.anyString(),
					Mockito.anyString());

			IEAPClientNBI ieapClientNBI = Mockito.mock(IEAPClientNBI.class);
			Mockito.doReturn(new Token()).when(ieapClientNBI).createAccessToken(Mockito.any(Oauth2TokenBody.class));
			
			LockRepository lockRepository = Mockito.mock(LockRepository.class);
			ExperimentLock experimentLock = new ExperimentLock();
			experimentLock.setLock(StringUtils.SPACE);
			Mockito.doReturn(Optional.of(experimentLock)).when(lockRepository).findById(Mockito.anyString());

			ExperimentService experimentService = ExperimentServiceImpl.builder().ieapClientNBI(ieapClientNBI)
					.xrproperties(xrProperties).sessionRepository(sessionRepository).lockRepository(lockRepository)
					.southNodeNSTRepository(southNodeNSTRepository).build();

			ResponseEntity<Void> response = experimentService.experimentExperimentNameDelete(StringUtils.EMPTY,
					StringUtils.EMPTY);
			assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		}

		@Test
		void whenInexistantSession() {

			XRProperties xrProperties = new XRProperties();
			xrProperties.setSessionValidTimeInSeconds(300l);
			xrProperties.setExperimentLockId(StringUtils.EMPTY);

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Mockito.doReturn(Optional.empty()).when(sessionRepository).findById(Mockito.anyString());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			SouthNodeExperimentIEAPInstance instance = new SouthNodeExperimentIEAPInstance(StringUtils.EMPTY,
					StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
			SouthNodeExperiment toBeDeleted = new SouthNodeExperiment();
			toBeDeleted.setIeapInstances(List.of(instance));
			Mockito.doReturn(toBeDeleted).when(southNodeNSTRepository).findByUserAndExperimentName(Mockito.anyString(),
					Mockito.anyString());

			IEAPClientNBI ieapClientNBI = Mockito.mock(IEAPClientNBI.class);

			ExperimentService experimentService = ExperimentServiceImpl.builder().ieapClientNBI(ieapClientNBI)
					.xrproperties(xrProperties).sessionRepository(sessionRepository)
					.southNodeNSTRepository(southNodeNSTRepository).build();

			ResponseEntity<Void> response = experimentService.experimentExperimentNameDelete(StringUtils.EMPTY,
					StringUtils.EMPTY);
			assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		}
	}

	@Nested
	class ExperimentServiceImplTestEndTrialIdDelete {

		@Test
		void withoutSessionTrialIdExists() {

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Session session = new Session("user", "appProviderId", new Token("accessToken", "tokenType", 1, "scope"),
					LocalDateTime.now(), LocalDateTime.now());
			Mockito.doReturn(Optional.of(session)).when(sessionRepository).findById(Mockito.anyString());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);

			SouthNodeExperimentIEAPInstance instance = new SouthNodeExperimentIEAPInstance(StringUtils.EMPTY,
					StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);

			SouthNodeAdapterNetworkServiceTemplate southNodeAdapterNetworkServiceTemplate = new SouthNodeAdapterNetworkServiceTemplate();
			southNodeAdapterNetworkServiceTemplate.setStatus(StatusEnum.LAUNCH_SUCCESS);
			NetworkServiceTemplate networkServiceTemplate = new NetworkServiceTemplate();
			networkServiceTemplate.setSouthNodeAdapterNetworkServiceTemplate(southNodeAdapterNetworkServiceTemplate);
			networkServiceTemplate.setQosiumMonitoring(false);
			SouthNodeExperiment toBeDeleted = new SouthNodeExperiment();
			toBeDeleted.setNetworkServiceTemplate(networkServiceTemplate);
			toBeDeleted.setIeapInstances(List.of(instance));

			IEAPAccess ieapAccess = new IEAPAccess();
			ieapAccess.setCredentials(new Oauth2TokenBody());
			
			XRProperties xrProperties = new XRProperties();
			xrProperties.setIeapAccess(ieapAccess);
			xrProperties.setExperimentLockId(StringUtils.EMPTY);
			LockRepository lockRepository = Mockito.mock(LockRepository.class);
			ExperimentLock experimentLock = new ExperimentLock();
			experimentLock.setLock(StringUtils.SPACE);
			Mockito.doReturn(Optional.of(experimentLock)).when(lockRepository).findById(Mockito.anyString());

			Mockito.doReturn(List.of(toBeDeleted)).when(southNodeNSTRepository)
					.findByNetworkServiceTemplate_TrialId(Mockito.anyInt());

			IEAPClientNBI ieapClientNBI = Mockito.mock(IEAPClientNBI.class);
			Mockito.doReturn(new Token()).when(ieapClientNBI).createAccessToken(Mockito.any(Oauth2TokenBody.class));

			ExperimentService experimentService = ExperimentServiceImpl.builder().ieapClientNBI(ieapClientNBI)
					.xrproperties(xrProperties).lockRepository(lockRepository).sessionRepository(sessionRepository)
					.southNodeNSTRepository(southNodeNSTRepository).build();

			ResponseEntity<Void> response = experimentService.experimentEndTrialTrialIdDelete(0);
			assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		}
	}

	@Nested
	class ExperimentServiceImplTestRunExperimentNamePost {

		@Test
		void whenSessionExistsExperimentAlreadyInCourse() {

			XRProperties xrProperties = new XRProperties();
			xrProperties.setSessionValidTimeInSeconds(300l);
			xrProperties.setExperimentLockId(StringUtils.EMPTY);

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Session session = new Session("user", "appProviderId", new Token("accessToken", "tokenType", 1, "scope"),
					LocalDateTime.now(), LocalDateTime.now());
			Mockito.doReturn(Optional.of(session)).when(sessionRepository).findById(Mockito.anyString());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			SouthNodeExperimentIEAPInstance instance = new SouthNodeExperimentIEAPInstance(StringUtils.EMPTY,
					StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
			SouthNodeExperiment toBeDeleted = new SouthNodeExperiment();
			NetworkServiceTemplate networkServiceTemplate = new NetworkServiceTemplate();
			networkServiceTemplate.setQosiumMonitoring(true);
			toBeDeleted.setNetworkServiceTemplate(networkServiceTemplate);
			toBeDeleted.setIeapInstances(List.of(instance));
			Mockito.doReturn(toBeDeleted).when(southNodeNSTRepository).findByUserAndExperimentName(Mockito.anyString(),
					Mockito.anyString());

			LockRepository lockRepository = Mockito.mock(LockRepository.class);
			ExperimentLock experimentLock = new ExperimentLock();
			experimentLock.setLock(StringUtils.SPACE);
			Mockito.doReturn(Optional.of(experimentLock)).when(lockRepository).findById(Mockito.anyString());

			IEAPClientNBI ieapClientNBI = Mockito.mock(IEAPClientNBI.class);
			Mockito.doReturn(new Token()).when(ieapClientNBI).createAccessToken(Mockito.any(Oauth2TokenBody.class));

			ExperimentService experimentService = ExperimentServiceImpl.builder().ieapClientNBI(ieapClientNBI)
					.lockRepository(lockRepository).sessionRepository(sessionRepository).xrproperties(xrProperties)
					.southNodeNSTRepository(southNodeNSTRepository).build();

			ResponseEntity<List<ExperimentError>> response = experimentService
					.experimentRunExperimentNamePost(StringUtils.EMPTY, StringUtils.EMPTY);
			assertEquals(HttpStatus.LOCKED, response.getStatusCode());
		}

		@Test
		void whenSessionExistsExperimentNotInCourse() {

			XRProperties xrProperties = new XRProperties();
			xrProperties.setSessionValidTimeInSeconds(300l);
			xrProperties.setExperimentLockId(StringUtils.EMPTY);

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Session session = new Session("user", "appProviderId", new Token("accessToken", "tokenType", 1, "scope"),
					LocalDateTime.now(), LocalDateTime.now());
			Mockito.doReturn(Optional.of(session)).when(sessionRepository).findById(Mockito.anyString());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			SouthNodeExperimentIEAPInstance instance = new SouthNodeExperimentIEAPInstance(StringUtils.EMPTY,
					StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
			SouthNodeExperiment toBeDeleted = new SouthNodeExperiment();
			toBeDeleted.setIeapInstances(List.of(instance));
			NetworkServiceTemplate networkServiceTemplate = new NetworkServiceTemplate();
			networkServiceTemplate.setQosiumMonitoring(true);
			toBeDeleted.setNetworkServiceTemplate(networkServiceTemplate);
			Mockito.doReturn(toBeDeleted).when(southNodeNSTRepository).findByUserAndExperimentName(Mockito.anyString(),
					Mockito.anyString());

			LockRepository lockRepository = Mockito.mock(LockRepository.class);
			ExperimentLock experimentLock = new ExperimentLock();
			experimentLock.setLock(StringUtils.SPACE);
			Mockito.doReturn(Optional.of(experimentLock)).when(lockRepository).findById(Mockito.anyString());

			IEAPClientNBI ieapClientNBI = Mockito.mock(IEAPClientNBI.class);
			Mockito.doReturn(new Token()).when(ieapClientNBI).createAccessToken(Mockito.any(Oauth2TokenBody.class));

			ExperimentService experimentService = ExperimentServiceImpl.builder().ieapClientNBI(ieapClientNBI)
					.lockRepository(lockRepository).sessionRepository(sessionRepository).xrproperties(xrProperties)
					.southNodeNSTRepository(southNodeNSTRepository).build();

			ResponseEntity<List<ExperimentError>> response = experimentService
					.experimentRunExperimentNamePost(StringUtils.EMPTY, StringUtils.EMPTY);
			assertEquals(HttpStatus.LOCKED, response.getStatusCode());
		}

		@Test
		void whenInexistantSession() {

			XRProperties xrProperties = new XRProperties();
			xrProperties.setSessionValidTimeInSeconds(300l);
			xrProperties.setExperimentLockId(StringUtils.EMPTY);

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Mockito.doReturn(Optional.empty()).when(sessionRepository).findById(Mockito.anyString());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			SouthNodeExperimentIEAPInstance instance = new SouthNodeExperimentIEAPInstance(StringUtils.EMPTY,
					StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
			SouthNodeExperiment toBeDeleted = new SouthNodeExperiment();
			toBeDeleted.setIeapInstances(List.of(instance));
			Mockito.doReturn(toBeDeleted).when(southNodeNSTRepository).findByUserAndExperimentName(Mockito.anyString(),
					Mockito.anyString());

			IEAPClientNBI ieapClientNBI = Mockito.mock(IEAPClientNBI.class);

			ExperimentService experimentService = ExperimentServiceImpl.builder().ieapClientNBI(ieapClientNBI)
					.xrproperties(xrProperties).sessionRepository(sessionRepository)
					.southNodeNSTRepository(southNodeNSTRepository).build();

			ResponseEntity<List<ExperimentError>> response = experimentService
					.experimentRunExperimentNamePost(StringUtils.EMPTY, StringUtils.EMPTY);
			assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		}
	}
}
