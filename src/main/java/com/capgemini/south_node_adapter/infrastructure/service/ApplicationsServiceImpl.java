package com.capgemini.south_node_adapter.infrastructure.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.capgemini.south_node_adapter.domain.model.ApplicationsAppDeploymentTcSlices;
import com.capgemini.south_node_adapter.domain.model.InlineResponse200;
import com.capgemini.south_node_adapter.domain.service.ApplicationsService;
import com.capgemini.south_node_adapter.infrastructure.auth.SessionUtil;
import com.capgemini.south_node_adapter.infrastructure.configuration.XRProperties;
import com.capgemini.south_node_adapter.infrastructure.feign.IEAPClientNBI;
import com.capgemini.south_node_adapter.infrastructure.feign.model.EnterpriseDetails;
import com.capgemini.south_node_adapter.infrastructure.feign.model.Flavour;
import com.capgemini.south_node_adapter.infrastructure.feign.model.ZoneDetails;
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
	public ResponseEntity<List<InlineResponse200>> viewAllApplication(String sessionId) {

		Optional<Session> session = SessionUtil.getSession(sessionId, sessionRepository);
		if (session.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		try {
			return this.feignGetApplicationList(session.get());
		} catch (FeignException e) {
			return new ResponseEntity<>(HttpStatus.valueOf(e.status()));
		}
	}

	private ResponseEntity<List<InlineResponse200>> feignGetApplicationList(Session session) {
		
		SessionUtil.checkAndRefreshSessionIeapTokenIfExpired(session, ieapClientNBI, sessionRepository);
        Map<String, ZoneDetails> zoneDetails = this.getUserIEAPZones(session, ieapClientNBI);

		List<InlineResponse200> applications = this.ieapClientNBI
				.getApplications(session.getAppProviderId(), session.getIeapToken().getAccessToken()).stream()
				.map(app -> {

					InlineResponse200 response = new InlineResponse200();
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
							slice.setSupportedFlavours(zoneDetails.get(ieapZoneDetails.getZoneId())
									.getSupportedFlavours().stream().map(Flavour::getFlavourId).toList());

							tcSlices.add(slice);
						});
					});
					response.setAppDeploymentTcSlices(tcSlices);

					return response;
				}).toList();

		return new ResponseEntity<>(applications, HttpStatus.OK);
	}
	
	private Map<String, ZoneDetails> getUserIEAPZones(Session session, IEAPClientNBI ieapClientNBI) {

		EnterpriseDetails user = ieapClientNBI.getUser(session.getAppProviderId(),
				session.getIeapToken().getAccessToken());
		String domain = StringUtils.isEmpty(user.getDomainName()) ? "OPDefault" : user.getDomainName();

		List<ZoneDetails> zones = ieapClientNBI.getZones(domain, session.getIeapToken().getAccessToken());
		return zones.stream().collect(Collectors.toMap(zone -> zone.getZoneId(), zone -> zone));
	}
}
