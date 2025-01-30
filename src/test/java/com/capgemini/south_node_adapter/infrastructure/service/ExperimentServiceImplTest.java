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
import com.capgemini.south_node_adapter.domain.service.ExperimentService;
import com.capgemini.south_node_adapter.infrastructure.auth.Token;
import com.capgemini.south_node_adapter.infrastructure.configuration.TCSlice;
import com.capgemini.south_node_adapter.infrastructure.configuration.XRProperties;
import com.capgemini.south_node_adapter.infrastructure.feign.IEAPClientNBI;
import com.capgemini.south_node_adapter.infrastructure.feign.NEFClient;
import com.capgemini.south_node_adapter.infrastructure.feign.model.EnterpriseDetails;
import com.capgemini.south_node_adapter.infrastructure.feign.model.Oauth2TokenBody;
import com.capgemini.south_node_adapter.infrastructure.feign.model.ZoneDetails;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.Session;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.SouthNodeExperiment;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.SouthNodeExperimentIEAPInstance;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.SessionRepository;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.SouthNodeNSTRepository;

import feign.FeignException;
import feign.Request;
import feign.Request.HttpMethod;

class ExperimentServiceImplTest {

	@Nested
	class ExperimentServiceImplTestGet {

		@Test
		void whenSessionExists() {

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Session session = new Session("user", "appProviderId", new Token("accessToken", "tokenType", 1, "scope"), LocalDateTime.now());
			Mockito.doReturn(Optional.of(session)).when(sessionRepository).findById(Mockito.anyString());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			Mockito.doReturn(List.of(new SouthNodeExperiment())).when(southNodeNSTRepository)
					.findByUser(Mockito.anyString());

			ExperimentService experimentService = ExperimentServiceImpl.builder().sessionRepository(sessionRepository)
					.southNodeNSTRepository(southNodeNSTRepository).build();

			ResponseEntity<List<NetworkServiceTemplate>> response = experimentService.experimentGet(StringUtils.EMPTY);
			assertEquals(HttpStatus.OK, response.getStatusCode());
		}

		@Test
		void whenInexistantSession() {

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Mockito.doReturn(Optional.empty()).when(sessionRepository).findById(Mockito.anyString());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			Mockito.doReturn(List.of(new SouthNodeExperiment())).when(southNodeNSTRepository)
					.findByUser(Mockito.anyString());

			ExperimentService experimentService = ExperimentServiceImpl.builder().sessionRepository(sessionRepository)
					.southNodeNSTRepository(southNodeNSTRepository).build();

			ResponseEntity<List<NetworkServiceTemplate>> response = experimentService.experimentGet(StringUtils.EMPTY);
			assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		}
	}

	@Nested
	class ExperimentServiceImplTestPost {

		@Test
		void whenSessionExists() {

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Session session = new Session("user", "appProviderId", new Token("accessToken", "tokenType", 1, "scope"), LocalDateTime.now());
			Mockito.doReturn(Optional.of(session)).when(sessionRepository).findById(Mockito.anyString());

			IEAPClientNBI ieapClientNBI = Mockito.mock(IEAPClientNBI.class);
			Mockito.doReturn(new Token("accessToken", "tokenType", 1, "scope")).when(ieapClientNBI).createAccessToken(Mockito.any(Oauth2TokenBody.class));
			EnterpriseDetails user = new EnterpriseDetails();
			Mockito.doReturn(user).when(ieapClientNBI).getUser(Mockito.anyString(), Mockito.anyString());
			ZoneDetails zoneDetails = new ZoneDetails();
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

			SouthNodeAdapterNetworkServiceTemplateApplications sample = new SouthNodeAdapterNetworkServiceTemplateApplications();
			sample.setTcSlice("tcSlice");
			SouthNodeAdapterNetworkServiceTemplateSubscriptions sampleSub = new SouthNodeAdapterNetworkServiceTemplateSubscriptions();
			sampleSub.setTcSlice("tcSlice");
			SouthNodeAdapterNetworkServiceTemplate southNodeAdapterNetworkServiceTemplate = new SouthNodeAdapterNetworkServiceTemplate();
			southNodeAdapterNetworkServiceTemplate.setApplications(List.of(sample, sample, sample));
			southNodeAdapterNetworkServiceTemplate.setSubscriptions(List.of(sampleSub, sampleSub, sampleSub));
			NetworkServiceTemplate networkServiceTemplate = new NetworkServiceTemplate();
			networkServiceTemplate.setSouthNodeAdapterNetworkServiceTemplate(southNodeAdapterNetworkServiceTemplate);

			ExperimentService experimentService = ExperimentServiceImpl.builder().ieapClientNBI(ieapClientNBI)
					.sessionRepository(sessionRepository).southNodeNSTRepository(southNodeNSTRepository)
					.nefClient(nefClient).xrproperties(properties).build();

			ResponseEntity<List<ExperimentError>> response = experimentService.experimentPost(StringUtils.EMPTY,
					networkServiceTemplate);
			assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		}

		@Test
		void whenInexistantSession() {

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Mockito.doReturn(Optional.empty()).when(sessionRepository).findById(Mockito.anyString());

			IEAPClientNBI ieapClientNBI = Mockito.mock(IEAPClientNBI.class);
			Mockito.doReturn(List.of(StringUtils.EMPTY, StringUtils.SPACE)).when(ieapClientNBI).launchApplication(
					Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
					Mockito.anyString(), Mockito.anyString());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			Mockito.doReturn(null).when(southNodeNSTRepository).save(Mockito.any(SouthNodeExperiment.class));

			ExperimentService experimentService = ExperimentServiceImpl.builder().ieapClientNBI(ieapClientNBI)
					.sessionRepository(sessionRepository).southNodeNSTRepository(southNodeNSTRepository).build();

			SouthNodeAdapterNetworkServiceTemplateApplications sample = new SouthNodeAdapterNetworkServiceTemplateApplications();

			SouthNodeAdapterNetworkServiceTemplate southNodeAdapterNetworkServiceTemplate = new SouthNodeAdapterNetworkServiceTemplate();
			southNodeAdapterNetworkServiceTemplate.setApplications(List.of(sample, sample, sample));

			NetworkServiceTemplate networkServiceTemplate = new NetworkServiceTemplate();
			networkServiceTemplate.setSouthNodeAdapterNetworkServiceTemplate(southNodeAdapterNetworkServiceTemplate);

			ResponseEntity<List<ExperimentError>> response = experimentService.experimentPost(StringUtils.EMPTY,
					networkServiceTemplate);
			assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		}

		@Test
		void whenIEAPClientException() {

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Session session = new Session("user", "appProviderId", new Token("accessToken", "tokenType", 1, "scope"), LocalDateTime.now());
			Mockito.doReturn(Optional.of(session)).when(sessionRepository).findById(Mockito.anyString());

			IEAPClientNBI ieapClientNBI = Mockito.mock(IEAPClientNBI.class);
			Mockito.doReturn(new Token("accessToken", "tokenType", 1, "scope")).when(ieapClientNBI).createAccessToken(Mockito.any(Oauth2TokenBody.class));
			EnterpriseDetails user = new EnterpriseDetails();
			Mockito.doReturn(user).when(ieapClientNBI).getUser(Mockito.anyString(), Mockito.anyString());
			ZoneDetails zoneDetails = new ZoneDetails();
			zoneDetails.setZoneId("zoneId");
			Mockito.doReturn(List.of(zoneDetails)).when(ieapClientNBI).getZones(Mockito.anyString(),
					Mockito.anyString());
			Mockito.doThrow(new FeignException.InternalServerError(StringUtils.EMPTY,
					Request.create(HttpMethod.POST, StringUtils.EMPTY, Map.of(), null, null, null), null, null))
					.when(ieapClientNBI).launchApplication(Mockito.any(), Mockito.any(), Mockito.any(),
							Mockito.anyBoolean(), Mockito.any(), Mockito.any());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			Mockito.doReturn(null).when(southNodeNSTRepository).save(Mockito.any(SouthNodeExperiment.class));

			XRProperties properties = new XRProperties();
			Map<String, TCSlice> tcSliceMap = new HashMap<String, TCSlice>();
			TCSlice sampleTcSlice = new TCSlice();
			sampleTcSlice.setIeapZone("zoneId");
			tcSliceMap.put("tcSlice", sampleTcSlice);
			properties.setTcSlices(tcSliceMap);

			NEFClient nefClient = Mockito.mock(NEFClient.class);

			ExperimentService experimentService = ExperimentServiceImpl.builder().ieapClientNBI(ieapClientNBI)
					.nefClient(nefClient).sessionRepository(sessionRepository)
					.southNodeNSTRepository(southNodeNSTRepository).xrproperties(properties).build();

			SouthNodeAdapterNetworkServiceTemplateApplications sample = new SouthNodeAdapterNetworkServiceTemplateApplications();
			sample.setTcSlice("tcSlice");
			SouthNodeAdapterNetworkServiceTemplateSubscriptions sampleSub = new SouthNodeAdapterNetworkServiceTemplateSubscriptions();
			sampleSub.setTcSlice("tcSlice");
			SouthNodeAdapterNetworkServiceTemplate southNodeAdapterNetworkServiceTemplate = new SouthNodeAdapterNetworkServiceTemplate();
			southNodeAdapterNetworkServiceTemplate.setApplications(List.of(sample, sample, sample));
			southNodeAdapterNetworkServiceTemplate.setSubscriptions(List.of(sampleSub, sampleSub, sampleSub));

			NetworkServiceTemplate networkServiceTemplate = new NetworkServiceTemplate();
			networkServiceTemplate.setSouthNodeAdapterNetworkServiceTemplate(southNodeAdapterNetworkServiceTemplate);

			ResponseEntity<List<ExperimentError>> response = experimentService.experimentPost(StringUtils.EMPTY,
					networkServiceTemplate);
			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		}
		
		@Test
		void whenIEAPClientZoneException() {

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Session session = new Session("user", "appProviderId", new Token("accessToken", "tokenType", 1, "scope"), LocalDateTime.now());
			Mockito.doReturn(Optional.of(session)).when(sessionRepository).findById(Mockito.anyString());

			IEAPClientNBI ieapClientNBI = Mockito.mock(IEAPClientNBI.class);
			Mockito.doReturn(new Token()).when(ieapClientNBI).createAccessToken(Mockito.any(Oauth2TokenBody.class));
			EnterpriseDetails user = new EnterpriseDetails();
			Mockito.doReturn(user).when(ieapClientNBI).getUser(Mockito.anyString(), Mockito.anyString());
			ZoneDetails zoneDetails = new ZoneDetails();
			zoneDetails.setZoneId("zoneId");
			Mockito.doThrow(new FeignException.InternalServerError(StringUtils.EMPTY,
					Request.create(HttpMethod.POST, StringUtils.EMPTY, Map.of(), null, null, null), null, null)).when(ieapClientNBI).getZones(Mockito.anyString(),
					Mockito.anyString());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			Mockito.doReturn(null).when(southNodeNSTRepository).save(Mockito.any(SouthNodeExperiment.class));

			XRProperties properties = new XRProperties();
			Map<String, TCSlice> tcSliceMap = new HashMap<String, TCSlice>();
			TCSlice sampleTcSlice = new TCSlice();
			sampleTcSlice.setIeapZone("zoneId");
			tcSliceMap.put("tcSlice", sampleTcSlice);
			properties.setTcSlices(tcSliceMap);

			NEFClient nefClient = Mockito.mock(NEFClient.class);

			ExperimentService experimentService = ExperimentServiceImpl.builder().ieapClientNBI(ieapClientNBI)
					.nefClient(nefClient).sessionRepository(sessionRepository)
					.southNodeNSTRepository(southNodeNSTRepository).xrproperties(properties).build();

			SouthNodeAdapterNetworkServiceTemplateApplications sample = new SouthNodeAdapterNetworkServiceTemplateApplications();
			sample.setTcSlice("tcSlice");
			SouthNodeAdapterNetworkServiceTemplateSubscriptions sampleSub = new SouthNodeAdapterNetworkServiceTemplateSubscriptions();
			sampleSub.setTcSlice("tcSlice");
			SouthNodeAdapterNetworkServiceTemplate southNodeAdapterNetworkServiceTemplate = new SouthNodeAdapterNetworkServiceTemplate();
			southNodeAdapterNetworkServiceTemplate.setApplications(List.of(sample, sample, sample));
			southNodeAdapterNetworkServiceTemplate.setSubscriptions(List.of(sampleSub, sampleSub, sampleSub));

			NetworkServiceTemplate networkServiceTemplate = new NetworkServiceTemplate();
			networkServiceTemplate.setSouthNodeAdapterNetworkServiceTemplate(southNodeAdapterNetworkServiceTemplate);

			ResponseEntity<List<ExperimentError>> response = experimentService.experimentPost(StringUtils.EMPTY,
					networkServiceTemplate);
			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		}
	}

	@Nested
	class ExperimentServiceImplTestIdGet {

		@Test
		void whenSessionExists() {

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Session session = new Session("user", "appProviderId", new Token("accessToken", "tokenType", 1, "scope"), LocalDateTime.now());
			Mockito.doReturn(Optional.of(session)).when(sessionRepository).findById(Mockito.anyString());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			Mockito.doReturn(new SouthNodeExperiment()).when(southNodeNSTRepository)
					.findByUserAndTrialId(Mockito.anyString(), Mockito.anyInt());

			ExperimentService experimentService = ExperimentServiceImpl.builder().sessionRepository(sessionRepository)
					.southNodeNSTRepository(southNodeNSTRepository).build();

			ResponseEntity<NetworkServiceTemplate> response = experimentService.experimentTrialIdGet(StringUtils.EMPTY,
					String.valueOf(0));
			assertEquals(HttpStatus.OK, response.getStatusCode());
		}

		@Test
		void whenInexistantSession() {

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Mockito.doReturn(Optional.empty()).when(sessionRepository).findById(Mockito.anyString());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			Mockito.doReturn(new SouthNodeExperiment()).when(southNodeNSTRepository)
					.findByUserAndTrialId(Mockito.anyString(), Mockito.anyInt());

			ExperimentService experimentService = ExperimentServiceImpl.builder().sessionRepository(sessionRepository)
					.southNodeNSTRepository(southNodeNSTRepository).build();

			ResponseEntity<NetworkServiceTemplate> response = experimentService.experimentTrialIdGet(StringUtils.EMPTY,
					String.valueOf(0));
			assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		}
		
		@Test
		void whenBadTrialId() {

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Session session = new Session("user", "appProviderId", new Token("accessToken", "tokenType", 1, "scope"), LocalDateTime.now());
			Mockito.doReturn(Optional.of(session)).when(sessionRepository).findById(Mockito.anyString());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			Mockito.doReturn(new SouthNodeExperiment()).when(southNodeNSTRepository)
					.findByUserAndTrialId(Mockito.anyString(), Mockito.anyInt());

			ExperimentService experimentService = ExperimentServiceImpl.builder().sessionRepository(sessionRepository)
					.southNodeNSTRepository(southNodeNSTRepository).build();

			ResponseEntity<NetworkServiceTemplate> response = experimentService.experimentTrialIdGet(StringUtils.EMPTY,
					StringUtils.EMPTY);
			assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		}
	}

	@Nested
	class ExperimentServiceImplTestIdDelete {

		@Test
		void whenSessionExists() {

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Session session = new Session("user", "appProviderId", new Token("accessToken", "tokenType", 1, "scope"), LocalDateTime.now());
			Mockito.doReturn(Optional.of(session)).when(sessionRepository).findById(Mockito.anyString());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			SouthNodeExperimentIEAPInstance instance = new SouthNodeExperimentIEAPInstance(StringUtils.EMPTY,
					StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
			SouthNodeExperiment toBeDeleted = new SouthNodeExperiment();
			toBeDeleted.setIeapInstances(List.of(instance));
			Mockito.doReturn(toBeDeleted).when(southNodeNSTRepository).findByUserAndTrialId(Mockito.anyString(),
					Mockito.anyInt());

			IEAPClientNBI ieapClientNBI = Mockito.mock(IEAPClientNBI.class);
			Mockito.doReturn(new Token()).when(ieapClientNBI).createAccessToken(Mockito.any(Oauth2TokenBody.class));

			ExperimentService experimentService = ExperimentServiceImpl.builder().ieapClientNBI(ieapClientNBI)
					.sessionRepository(sessionRepository).southNodeNSTRepository(southNodeNSTRepository).build();

			ResponseEntity<String> response = experimentService.experimentTrialIdDelete(StringUtils.EMPTY,
					String.valueOf(0));
			assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		}

		@Test
		void whenInexistantSession() {

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Mockito.doReturn(Optional.empty()).when(sessionRepository).findById(Mockito.anyString());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			SouthNodeExperimentIEAPInstance instance = new SouthNodeExperimentIEAPInstance(StringUtils.EMPTY,
					StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
			SouthNodeExperiment toBeDeleted = new SouthNodeExperiment();
			toBeDeleted.setIeapInstances(List.of(instance));
			Mockito.doReturn(toBeDeleted).when(southNodeNSTRepository).findByUserAndTrialId(Mockito.anyString(),
					Mockito.anyInt());

			IEAPClientNBI ieapClientNBI = Mockito.mock(IEAPClientNBI.class);

			ExperimentService experimentService = ExperimentServiceImpl.builder().ieapClientNBI(ieapClientNBI)
					.sessionRepository(sessionRepository).southNodeNSTRepository(southNodeNSTRepository).build();

			ResponseEntity<String> response = experimentService.experimentTrialIdDelete(StringUtils.EMPTY,
					String.valueOf(0));
			assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		}

		@Test
		void whenIEAPClientException() {

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Session session = new Session("user", "appProviderId", new Token("accessToken", "tokenType", 1, "scope"), LocalDateTime.now());
			Mockito.doReturn(Optional.of(session)).when(sessionRepository).findById(Mockito.anyString());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			SouthNodeExperimentIEAPInstance instance = new SouthNodeExperimentIEAPInstance(StringUtils.EMPTY,
					StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
			SouthNodeExperiment toBeDeleted = new SouthNodeExperiment();
			toBeDeleted.setIeapInstances(List.of(instance));
			Mockito.doReturn(toBeDeleted).when(southNodeNSTRepository).findByUserAndTrialId(Mockito.anyString(),
					Mockito.anyInt());

			IEAPClientNBI ieapClientNBI = Mockito.mock(IEAPClientNBI.class);
			Mockito.doReturn(new Token()).when(ieapClientNBI).createAccessToken(Mockito.any(Oauth2TokenBody.class));
			Mockito.doThrow(new FeignException.InternalServerError(StringUtils.EMPTY,
					Request.create(HttpMethod.DELETE, StringUtils.EMPTY, Map.of(), null, null, null), null, null))
					.when(ieapClientNBI).terminateApplication(Mockito.anyString(), Mockito.anyString(),
							Mockito.anyString(), Mockito.anyString());

			ExperimentService experimentService = ExperimentServiceImpl.builder().ieapClientNBI(ieapClientNBI)
					.sessionRepository(sessionRepository).southNodeNSTRepository(southNodeNSTRepository).build();

			ResponseEntity<String> response = experimentService.experimentTrialIdDelete(StringUtils.EMPTY,
					String.valueOf(0));
			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		}
		
		@Test
		void whenIEAPClientBadTrialId() {

			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Session session = new Session("user", "appProviderId", new Token("accessToken", "tokenType", 1, "scope"), LocalDateTime.now());
			Mockito.doReturn(Optional.of(session)).when(sessionRepository).findById(Mockito.anyString());

			SouthNodeNSTRepository southNodeNSTRepository = Mockito.mock(SouthNodeNSTRepository.class);
			SouthNodeExperimentIEAPInstance instance = new SouthNodeExperimentIEAPInstance(StringUtils.EMPTY,
					StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
			SouthNodeExperiment toBeDeleted = new SouthNodeExperiment();
			toBeDeleted.setIeapInstances(List.of(instance));
			Mockito.doReturn(toBeDeleted).when(southNodeNSTRepository).findByUserAndTrialId(Mockito.anyString(),
					Mockito.anyInt());

			IEAPClientNBI ieapClientNBI = Mockito.mock(IEAPClientNBI.class);

			ExperimentService experimentService = ExperimentServiceImpl.builder().ieapClientNBI(ieapClientNBI)
					.sessionRepository(sessionRepository).southNodeNSTRepository(southNodeNSTRepository).build();

			ResponseEntity<String> response = experimentService.experimentTrialIdDelete(StringUtils.EMPTY,
					StringUtils.EMPTY);
			assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		}
	}
}
