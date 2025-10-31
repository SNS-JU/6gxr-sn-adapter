package com.capgemini.south_node_adapter.infrastructure.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.capgemini.south_node_adapter.domain.model.Application;
import com.capgemini.south_node_adapter.domain.model.ApplicationsAppDeploymentTcSlices;
import com.capgemini.south_node_adapter.domain.service.ApplicationsService;
import com.capgemini.south_node_adapter.infrastructure.auth.SessionUtil;
import com.capgemini.south_node_adapter.infrastructure.configuration.XRProperties;
import com.capgemini.south_node_adapter.infrastructure.feign.IEAPClientNBI;
import com.capgemini.south_node_adapter.infrastructure.feign.model.EnterpriseDetails;
import com.capgemini.south_node_adapter.infrastructure.feign.model.Flavour;
import com.capgemini.south_node_adapter.infrastructure.feign.model.IEAPZoneDetails;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.Session;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.SessionRepository;

import feign.FeignException;
import lombok.Builder;

@Builder
public class ApplicationsServiceImpl implements ApplicationsService {

	IEAPClientNBI ieapClientNBI;

	SessionRepository sessionRepository;

	XRProperties xrProperties;

	@Override
	public ResponseEntity<List<Application>> viewAllApplication(String sessionId) {

		Optional<Session> session = SessionUtil.getSession(sessionId, sessionRepository,
				xrProperties.getSessionValidTimeInSeconds());
		if (session.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		try {
			return this.feignGetApplicationList(session.get());
		} catch (FeignException e) {
			return new ResponseEntity<>(HttpStatus.valueOf(e.status()));
		}
	}

	private ResponseEntity<List<Application>> feignGetApplicationList(Session session) {

		SessionUtil.checkAndRefreshSessionIeapTokenIfExpired(session, ieapClientNBI, sessionRepository, xrProperties);
		Map<String, IEAPZoneDetails> zoneDetails = this.getUserIEAPZones(session, ieapClientNBI);

		List<Application> applications = this.ieapClientNBI
				.getApplications(session.getAppProviderId(), session.getIeapToken().getAccessToken()).stream()
				.map(app -> {

					Application response = new Application();
					response.setAppId(app.getAppId());
					response.setAppComponentSpecs(app.getAppComponentSpecs());
					response.setAppMetaData(app.getAppMetaData());
					response.setAppQoSProfile(app.getAppQoSProfile());

					List<ApplicationsAppDeploymentTcSlices> tcSlices = new ArrayList<>();
					app.getAppDeploymentZones().stream().forEach(ieapZoneDetails -> {

						List<String> mappedTcSlices = this.xrProperties.getTcSlices().entrySet().stream()
								.filter(tcSlice -> tcSlice.getValue().getIeapZone().equals(ieapZoneDetails.getZoneId()))
								.map(Entry::getKey).toList();

						mappedTcSlices.forEach(mappedTcSlice -> {

							ApplicationsAppDeploymentTcSlices slice = new ApplicationsAppDeploymentTcSlices();

							slice.setTcSlice(mappedTcSlice);

							List<String> allowedFlavours = xrProperties.getTcSlices().get(mappedTcSlice)
									.getAllowedFlavours().entrySet().stream().map(Entry::getKey).toList();

							List<String> existingFlavours = zoneDetails.get(ieapZoneDetails.getZoneId())
									.getSupportedFlavours().stream().map(Flavour::getFlavourId).toList();

							slice.setSupportedFlavours(
									allowedFlavours.stream().filter(existingFlavours::contains).toList());

							tcSlices.add(slice);
						});
					});
					response.setAppDeploymentTcSlices(tcSlices);
					return response;
				}).toList();

		return new ResponseEntity<>(applications, HttpStatus.OK);
	}

	private Map<String, IEAPZoneDetails> getUserIEAPZones(Session session, IEAPClientNBI ieapClientNBI) {

		EnterpriseDetails user = ieapClientNBI.getUser(session.getAppProviderId(),
				session.getIeapToken().getAccessToken());
		String domain = StringUtils.isEmpty(user.getDomainName()) ? "OPDefault" : user.getDomainName();

		List<IEAPZoneDetails> zones = ieapClientNBI.getZones(domain, session.getIeapToken().getAccessToken());
		return zones.stream().collect(Collectors.toMap(zone -> zone.getZoneId(), zone -> zone));
	}
}
