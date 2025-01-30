package com.capgemini.south_node_adapter.infrastructure.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import com.capgemini.south_node_adapter.domain.model.InlineResponse200;
import com.capgemini.south_node_adapter.domain.service.ApplicationsService;
import com.capgemini.south_node_adapter.infrastructure.auth.Token;
import com.capgemini.south_node_adapter.infrastructure.configuration.TCSlice;
import com.capgemini.south_node_adapter.infrastructure.configuration.XRProperties;
import com.capgemini.south_node_adapter.infrastructure.feign.IEAPClientNBI;
import com.capgemini.south_node_adapter.infrastructure.feign.model.EnterpriseDetails;
import com.capgemini.south_node_adapter.infrastructure.feign.model.Flavour;
import com.capgemini.south_node_adapter.infrastructure.feign.model.IEAPApplication;
import com.capgemini.south_node_adapter.infrastructure.feign.model.ZoneDetails;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.Session;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.SessionRepository;

import feign.FeignException;
import feign.Request;
import feign.Request.HttpMethod;

class ApplicationsServiceImplTest {

	@Nested
	class ApplicationsServiceImplTestGet {
		
		@Test
		void whenSessionExists() {

			Token token = new Token("accessToken", "tokenType", 1, "scope");
			Session session = new Session("user", "appProviderId", token);
			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Mockito.doReturn(Optional.of(session)).when(sessionRepository).findById(Mockito.anyString());

			ZoneDetails deploymentZoneDetails = new ZoneDetails();
			deploymentZoneDetails.setZoneId("zoneId");
			IEAPApplication sample = new IEAPApplication();
			sample.setAppDeploymentZones(List.of(deploymentZoneDetails));
			List<IEAPApplication> applications = List.of(sample, sample, sample);
			
			IEAPClientNBI ieapClientNBI = Mockito.mock(IEAPClientNBI.class);
			Mockito.doReturn(applications).when(ieapClientNBI).getApplications(Mockito.anyString(), Mockito.anyString());
			EnterpriseDetails user = new EnterpriseDetails();
			user.setDomainName(StringUtils.EMPTY);
			Mockito.doReturn(user).when(ieapClientNBI).getUser(Mockito.anyString(), Mockito.anyString());
			
			Flavour flavour = new Flavour();
			flavour.setFlavourId("flavour");
			ZoneDetails ieapZoneDetails = new ZoneDetails();
			ieapZoneDetails.setZoneId("zoneId");
			ieapZoneDetails.setSupportedFlavours(List.of(flavour));
			Mockito.doReturn(List.of(ieapZoneDetails)).when(ieapClientNBI).getZones(Mockito.anyString(), Mockito.anyString());
			
			XRProperties properties = new XRProperties();
			Map<String, TCSlice> tcSliceMap = new HashMap<String, TCSlice>();
			TCSlice sampleTcSlice = new TCSlice();
			sampleTcSlice.setIeapZone("zoneId");
			tcSliceMap.put("tcSlice", sampleTcSlice);
			properties.setTcSlices(tcSliceMap);

			ApplicationsService applicationsService = ApplicationsServiceImpl.builder().ieapClientNBI(ieapClientNBI).xrProperties(properties)
					.sessionRepository(sessionRepository).build();

			ResponseEntity<List<InlineResponse200>> response = applicationsService
					.viewAllApplication(StringUtils.EMPTY);
			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertEquals(3, response.getBody().size());
		}

		@Test
		void whenInexistantSession() {
			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Mockito.doReturn(Optional.empty()).when(sessionRepository).findById(Mockito.anyString());

			IEAPApplication sample = new IEAPApplication();
			sample.setAppDeploymentZones(List.of(new ZoneDetails()));
			List<IEAPApplication> applications = List.of(sample, sample, sample);
			
			IEAPClientNBI ieapClientNBI = Mockito.mock(IEAPClientNBI.class);
			Mockito.doReturn(applications).when(ieapClientNBI).getApplications(Mockito.anyString(), Mockito.anyString());

			ApplicationsService applicationsService = ApplicationsServiceImpl.builder().ieapClientNBI(ieapClientNBI)
					.sessionRepository(sessionRepository).build();

			ResponseEntity<List<InlineResponse200>> response = applicationsService
					.viewAllApplication(StringUtils.EMPTY);
			assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		}

		@Test
		void whenIEAPClientException() {

			Token token = new Token("accessToken", "tokenType", 1, "scope");
			Session session = new Session("user", "appProviderId", token);
			SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
			Mockito.doReturn(Optional.of(session)).when(sessionRepository).findById(Mockito.anyString());

			IEAPClientNBI ieapClientNBI = Mockito.mock(IEAPClientNBI.class);
			Mockito.doThrow(new FeignException.InternalServerError(StringUtils.EMPTY,
					Request.create(HttpMethod.GET, StringUtils.EMPTY, Map.of(), null, null, null), null, null))
					.when(ieapClientNBI).getApplications(Mockito.anyString(), Mockito.anyString());
			EnterpriseDetails user = new EnterpriseDetails();
			Mockito.doReturn(user).when(ieapClientNBI).getUser(Mockito.anyString(), Mockito.anyString());

			XRProperties properties = new XRProperties();
			Map<String, TCSlice> tcSliceMap = new HashMap<String, TCSlice>();
			TCSlice sampleTcSlice = new TCSlice();
			sampleTcSlice.setIeapZone("zoneId");
			tcSliceMap.put("tcSlice", sampleTcSlice);
			properties.setTcSlices(tcSliceMap);
			
			ApplicationsService applicationsService = ApplicationsServiceImpl.builder().ieapClientNBI(ieapClientNBI)
					.xrProperties(properties)
					.sessionRepository(sessionRepository).build();

			ResponseEntity<List<InlineResponse200>> response = applicationsService
					.viewAllApplication(StringUtils.EMPTY);
			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		}
	}
}
