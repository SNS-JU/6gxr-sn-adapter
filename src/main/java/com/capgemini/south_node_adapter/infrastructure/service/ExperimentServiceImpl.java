package com.capgemini.south_node_adapter.infrastructure.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.capgemini.south_node_adapter.domain.model.ExperimentError;
import com.capgemini.south_node_adapter.domain.model.NetworkServiceTemplate;
import com.capgemini.south_node_adapter.domain.model.constant.StatusEnum;
import com.capgemini.south_node_adapter.domain.service.ExperimentService;
import com.capgemini.south_node_adapter.infrastructure.auth.SessionUtil;
import com.capgemini.south_node_adapter.infrastructure.configuration.XRProperties;
import com.capgemini.south_node_adapter.infrastructure.feign.IEAPClientNBI;
import com.capgemini.south_node_adapter.infrastructure.feign.NEFClient;
import com.capgemini.south_node_adapter.infrastructure.feign.model.EnterpriseDetails;
import com.capgemini.south_node_adapter.infrastructure.feign.model.ZoneDetails;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.Session;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.SouthNodeExperiment;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.SouthNodeExperimentIEAPInstance;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.SessionRepository;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.SouthNodeNSTRepository;

import feign.FeignException;
import lombok.Builder;

@Builder
public class ExperimentServiceImpl implements ExperimentService {

	IEAPClientNBI ieapClientNBI;

	SessionRepository sessionRepository;

	SouthNodeNSTRepository southNodeNSTRepository;

	XRProperties xrproperties;

	NEFClient nefClient;

	@Override
	public ResponseEntity<List<NetworkServiceTemplate>> experimentGet(String sessionId) {

		Optional<Session> session = SessionUtil.getSession(sessionId, sessionRepository);
		if (session.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		List<NetworkServiceTemplate> experiments = this.southNodeNSTRepository.findByUser(session.get().getUser())
				.stream().map(SouthNodeExperiment::getNetworkServiceTemplate).toList();

		return new ResponseEntity<>(experiments, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<ExperimentError>> experimentPost(String sessionId, NetworkServiceTemplate body) {

		Optional<Session> session = SessionUtil.getSession(sessionId, sessionRepository);
		if (session.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		SouthNodeExperiment dbExperiment = new SouthNodeExperiment();
		dbExperiment.setUser(session.get().getUser());
		dbExperiment.setTrialId(body.getTrialId());
		dbExperiment.setNetworkServiceTemplate(body);

		List<ExperimentError> errors = new ArrayList<>();
		Map<String, ZoneDetails> zones;

		try {
			zones = this.getUserIEAPZones(session.get(), ieapClientNBI, errors);
		} catch (FeignException e) {
			ExperimentError experimentError = new ExperimentError();
			experimentError.setHttpStatus(HttpStatus.valueOf(e.status()).toString());
			experimentError.setMessage(e.getMessage());
			errors.add(experimentError);

			return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		dbExperiment.setIeapInstances(this.feignIEAPApplicationPost(session.get(), body, zones, errors));
		this.feignNEFApplicationPost(session.get(), body, zones, errors);

		if (errors.isEmpty()) {

			body.getSouthNodeAdapterNetworkServiceTemplate().setStatus(StatusEnum.OK);
			southNodeNSTRepository.save(dbExperiment);

			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		} else {

			body.getSouthNodeAdapterNetworkServiceTemplate().setStatus(StatusEnum.KO);
			southNodeNSTRepository.save(dbExperiment);
			this.feignTerminateAllInstances(dbExperiment, session.get(), errors);

			return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private List<SouthNodeExperimentIEAPInstance> feignIEAPApplicationPost(Session session, NetworkServiceTemplate body,
			Map<String, ZoneDetails> zones, List<ExperimentError> errors) {

		SessionUtil.checkAndRefreshSessionIeapTokenIfExpired(session, ieapClientNBI, sessionRepository);
		List<SouthNodeExperimentIEAPInstance> ieapInstances = new ArrayList<>();

		body.getSouthNodeAdapterNetworkServiceTemplate().getApplications().stream().forEach(lcmDescriptorToLaunch -> {

			try {

				ZoneDetails zoneDetails = zones
						.get(this.xrproperties.getTcSlices().get(lcmDescriptorToLaunch.getTcSlice()).getIeapZone());

				String zoneInfo = new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
						.append("opCountry", zoneDetails.getOpCountry()).append("operator", zoneDetails.getOperator())
						.append("resourceConsumption", lcmDescriptorToLaunch.getResourceConsumption())
						.append("zoneId", zoneDetails.getZoneId())
						.append("flavourId", lcmDescriptorToLaunch.getFlavourId()).toString();

				List<String> launchResult = this.ieapClientNBI.launchApplication(lcmDescriptorToLaunch.getAppId(),
						session.getAppProviderId(), zoneInfo, false, lcmDescriptorToLaunch.getAppVersion(),
						session.getIeapToken().getAccessToken());

				ieapInstances
						.add(new SouthNodeExperimentIEAPInstance(lcmDescriptorToLaunch.getAppId(), launchResult.get(0),
								zoneDetails.getZoneId(), zoneDetails.getOperator(), zoneDetails.getOpCountry()));
			} catch (FeignException e) {
				ExperimentError experimentError = new ExperimentError();
				experimentError.setHttpStatus(HttpStatus.valueOf(e.status()).toString());
				experimentError.setMessage(e.getMessage());

				errors.add(experimentError);
			}
		});

		return ieapInstances;
	}

	private void feignNEFApplicationPost(Session session, NetworkServiceTemplate body, Map<String, ZoneDetails> zones,
			List<ExperimentError> errors) {

		body.getSouthNodeAdapterNetworkServiceTemplate().getSubscriptions().stream().forEach(subscription -> {
			try {
				this.nefClient.upsertSubcription(subscription.getGpsi(),
						this.xrproperties.getTcSlices().get(subscription.getTcSlice()).getNefSlice());
			} catch (FeignException e) {

				ExperimentError experimentError = new ExperimentError();
				experimentError.setHttpStatus(HttpStatus.valueOf(e.status()).toString());
				experimentError.setMessage(e.getMessage());

				errors.add(experimentError);
			}
		});
	}

	@Override
	public ResponseEntity<String> experimentTrialIdDelete(String sessionId, String trialId) {

		Optional<Session> session = SessionUtil.getSession(sessionId, sessionRepository);
		if (session.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		Integer trialIdNumber;
		try {
			trialIdNumber = Integer.valueOf(trialId);
		} catch (NumberFormatException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		SouthNodeExperiment experiment = this.southNodeNSTRepository.findByUserAndTrialId(session.get().getUser(),
				trialIdNumber);
		List<ExperimentError> errors = new ArrayList<>();
		this.feignTerminateAllInstances(experiment, session.get(), errors);

		if (errors.isEmpty()) {

			this.southNodeNSTRepository.deleteByUserAndTrialId(session.get().getUser(), trialIdNumber);
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		} else {

			String errorsString = errors.stream().map(error -> error.getHttpStatus() + "|" + error.getMessage())
					.toList().toString();
			return new ResponseEntity<>(errorsString, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<NetworkServiceTemplate> experimentTrialIdGet(String sessionId, String trialId) {

		Optional<Session> session = SessionUtil.getSession(sessionId, sessionRepository);
		if (session.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		Integer trialIdNumber;
		try {
			trialIdNumber = Integer.valueOf(trialId);
		} catch (NumberFormatException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		try {
			NetworkServiceTemplate experiment = this.southNodeNSTRepository
					.findByUserAndTrialId(session.get().getUser(), trialIdNumber).getNetworkServiceTemplate();
			return new ResponseEntity<>(experiment, HttpStatus.OK);
		} catch (FeignException e) {
			return new ResponseEntity<>(HttpStatus.valueOf(e.status()));
		}
	}

	private void feignTerminateAllInstances(SouthNodeExperiment experiment, Session session,
			List<ExperimentError> errors) {

		SessionUtil.checkAndRefreshSessionIeapTokenIfExpired(session, ieapClientNBI, sessionRepository);
		experiment.getIeapInstances().forEach(ieapInstance -> {
			try {
				this.ieapClientNBI.terminateApplication(ieapInstance.appId(), ieapInstance.appInstanceId(),
						ieapInstance.zoneId(), session.getIeapToken().getAccessToken());
			} catch (FeignException e) {
				ExperimentError experimentError = new ExperimentError();
				experimentError.setHttpStatus(HttpStatus.valueOf(e.status()).toString());
				experimentError.setMessage(e.getMessage());
				errors.add(experimentError);
			}
		});
	}

	private Map<String, ZoneDetails> getUserIEAPZones(Session session, IEAPClientNBI ieapClientNBI,
			List<ExperimentError> errors) {
		
		SessionUtil.checkAndRefreshSessionIeapTokenIfExpired(session, ieapClientNBI, sessionRepository);
		EnterpriseDetails user = ieapClientNBI.getUser(session.getAppProviderId(),
				session.getIeapToken().getAccessToken());

		String domain = StringUtils.isEmpty(user.getDomainName()) ? "OPDefault" : user.getDomainName();
		List<ZoneDetails> zones = ieapClientNBI.getZones(domain, session.getIeapToken().getAccessToken());

		return zones.stream().collect(Collectors.toMap(zone -> zone.getZoneId(), zone -> zone));
	}
}