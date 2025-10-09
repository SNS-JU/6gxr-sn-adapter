package com.capgemini.south_node_adapter.infrastructure.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import com.capgemini.south_node_adapter.infrastructure.auth.Token;
import com.capgemini.south_node_adapter.infrastructure.configuration.TCSlice;
import com.capgemini.south_node_adapter.infrastructure.configuration.XRProperties;
import com.capgemini.south_node_adapter.infrastructure.feign.IEAPClientNBI;
import com.capgemini.south_node_adapter.infrastructure.feign.NEFClient;
import com.capgemini.south_node_adapter.infrastructure.feign.QosiumClient;
import com.capgemini.south_node_adapter.infrastructure.feign.model.EnterpriseDetails;
import com.capgemini.south_node_adapter.infrastructure.feign.model.IEAPZoneDetails;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.ExperimentLock;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.Session;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.SouthNodeExperiment;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.SouthNodeExperimentIEAPInstance;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.LockRepository;
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

	LockRepository lockRepository;

	QosiumClient qosiumClient;

	// CREATE, READ AND DELETE EXPERIMENT //

	@Override
	public ResponseEntity<Void> experimentExperimentNameDelete(String sessionId, String experimentName) {

		Optional<Session> session = SessionUtil.getSession(sessionId, sessionRepository,
				xrproperties.getSessionValidTimeInSeconds());
		if (session.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		SouthNodeExperiment experiment = this.southNodeNSTRepository
				.findByUserAndExperimentName(session.get().getUser(), experimentName);
		Optional<ExperimentLock> experimentLock = this.lockRepository.findById(this.xrproperties.getExperimentLockId());

		if (this.isLockedByQosiumExperiment(experiment.getId(), experimentLock)) {
			this.experimentTerminateExperimentNameDelete(sessionId, experimentName);
		}

		this.southNodeNSTRepository.deleteByUserAndExperimentName(session.get().getUser(), experimentName);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

	@Override
	public ResponseEntity<NetworkServiceTemplate> experimentExperimentNameGet(String sessionId, String experimentName) {

		Optional<Session> session = SessionUtil.getSession(sessionId, sessionRepository,
				xrproperties.getSessionValidTimeInSeconds());
		if (session.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		try {
			NetworkServiceTemplate experiment = this.southNodeNSTRepository
					.findByUserAndExperimentName(sessionId, experimentName).getNetworkServiceTemplate();
			return new ResponseEntity<>(experiment, HttpStatus.OK);
		} catch (FeignException e) {
			return new ResponseEntity<>(HttpStatus.valueOf(e.status()));
		}
	}

	@Override
	public ResponseEntity<List<NetworkServiceTemplate>> experimentGet(String sessionId) {

		Optional<Session> session = SessionUtil.getSession(sessionId, sessionRepository,
				xrproperties.getSessionValidTimeInSeconds());
		if (session.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		List<NetworkServiceTemplate> experiments = this.southNodeNSTRepository.findByUser(session.get().getUser())
				.stream().map(SouthNodeExperiment::getNetworkServiceTemplate).toList();

		return new ResponseEntity<>(experiments, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Void> experimentPost(String sessionId, NetworkServiceTemplate body) {

		Optional<Session> session = SessionUtil.getSession(sessionId, sessionRepository,
				xrproperties.getSessionValidTimeInSeconds());
		if (session.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		if (StringUtils.isEmpty(body.getExperimentName())) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		if (!Objects.isNull(southNodeNSTRepository.findByUserAndExperimentName(session.get().getUser(),
				body.getExperimentName()))) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}

		SouthNodeExperiment experiment = new SouthNodeExperiment();
		experiment.setUser(session.get().getUser());
		experiment.setExperimentName(body.getExperimentName());
		experiment.setNetworkServiceTemplate(body);

		experiment.getNetworkServiceTemplate().getSouthNodeAdapterNetworkServiceTemplate()
				.setStatus(StatusEnum.CREATED);
		southNodeNSTRepository.save(experiment);

		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

	// RUN AND TERMINATE EXPERIMENT //

	@Override
	public ResponseEntity<List<ExperimentError>> experimentRunExperimentNamePost(String sessionId,
			String experimentName) {

		Optional<Session> session = SessionUtil.getSession(sessionId, sessionRepository,
				xrproperties.getSessionValidTimeInSeconds());
		if (session.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		SouthNodeExperiment experiment = this.southNodeNSTRepository
				.findByUserAndExperimentName(session.get().getUser(), experimentName);

		if (Objects.isNull(experiment)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		List<ExperimentError> errors = new ArrayList<>();
		
		//Only one Qosium experiment at a time in the plaform, otherwise ignore lock status
		if(Boolean.TRUE.equals(experiment.getNetworkServiceTemplate().getQosiumMonitoring())) {
			
			try {
				this.acquireQosiumLock(experiment.getId());
			} catch (IllegalStateException e) {
				ExperimentError lockError = new ExperimentError();
				lockError.setMessage(e.getMessage());
				return new ResponseEntity<>(List.of(lockError), HttpStatus.LOCKED);
			}
			
			this.qosiumPost(experiment, errors);
		}

		Map<String, IEAPZoneDetails> zones;

		try {
			zones = this.getUserIEAPZones(session.get(), ieapClientNBI);
			experiment.setIeapInstances(this.feignIEAPApplicationPost(session.get(),
					experiment.getNetworkServiceTemplate(), zones, errors));
			this.feignNEFApplicationPost(experiment.getNetworkServiceTemplate(), zones, errors);
		} catch (FeignException e) {
			ExperimentError experimentError = new ExperimentError();
			experimentError.setHttpStatus(HttpStatus.valueOf(e.status()).toString());
			experimentError.setMessage(e.getMessage());
			errors.add(experimentError);
		}

		if (errors.isEmpty()) {

			experiment.getNetworkServiceTemplate().getSouthNodeAdapterNetworkServiceTemplate()
					.setStatus(StatusEnum.LAUNCH_SUCCESS);
			southNodeNSTRepository.save(experiment);

			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		} else {

			experiment.getNetworkServiceTemplate().getSouthNodeAdapterNetworkServiceTemplate()
					.setStatus(StatusEnum.LAUNCH_FAILED);
			southNodeNSTRepository.save(experiment);
			this.feignTerminateAllInstances(experiment, session.get(), errors);

			if(experiment.getNetworkServiceTemplate().getQosiumMonitoring()) {
				this.releaseQosiumLock(experiment.getId());
			}
			
			return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private Map<String, IEAPZoneDetails> getUserIEAPZones(Session session, IEAPClientNBI ieapClientNBI) {

		SessionUtil.checkAndRefreshSessionIeapTokenIfExpired(session, ieapClientNBI, sessionRepository,
				this.xrproperties);
		EnterpriseDetails user = ieapClientNBI.getUser(session.getAppProviderId(),
				session.getIeapToken().getAccessToken());

		String domain = StringUtils.isEmpty(user.getDomainName()) ? "OPDefault" : user.getDomainName();
		List<IEAPZoneDetails> zones = ieapClientNBI.getZones(domain, session.getIeapToken().getAccessToken());

		return zones.stream().collect(Collectors.toMap(zone -> zone.getZoneId(), zone -> zone));
	}

	private List<SouthNodeExperimentIEAPInstance> feignIEAPApplicationPost(Session session, NetworkServiceTemplate body,
			Map<String, IEAPZoneDetails> zones, List<ExperimentError> errors) {

		SessionUtil.checkAndRefreshSessionIeapTokenIfExpired(session, ieapClientNBI, sessionRepository,
				this.xrproperties);
		List<SouthNodeExperimentIEAPInstance> ieapInstances = new ArrayList<>();

		body.getSouthNodeAdapterNetworkServiceTemplate().getApplications().stream().forEach(lcmDescriptorToLaunch -> {

			TCSlice tcSlice = this.xrproperties.getTcSlices().get(lcmDescriptorToLaunch.getTcSlice());
			IEAPZoneDetails zoneDetails = zones.get(tcSlice.getIeapZone());
			String infraId = StringUtils.isEmpty(lcmDescriptorToLaunch.getFlavourId()) ? null
					: tcSlice.getAllowedFlavours().get(lcmDescriptorToLaunch.getFlavourId());

			try {

				String zoneInfo = new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
						.append("opCountry", zoneDetails.getOpCountry()).append("operator", zoneDetails.getOperator())
						.append("resourceConsumption", lcmDescriptorToLaunch.getResourceConsumption())
						.append("zoneId", zoneDetails.getZoneId())
						.append("flavourId", lcmDescriptorToLaunch.getFlavourId()).append("infraId", infraId)
						.toString();

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
	
	private void qosiumPost(SouthNodeExperiment experiment, List<ExperimentError> errors) {
		
		try {
			String monitoringId = experiment.getId().concat(Timestamp.from(Instant.now()).toString())
					.replace(StringUtils.SPACE, StringUtils.EMPTY);

			experiment.setMonitoringId(monitoringId);
			this.qosiumClient.startExperiment(this.xrproperties.getMonitoringType(), experiment.getMonitoringId());
		} catch (FeignException e) {
			ExperimentError experimentError = new ExperimentError();
			experimentError.setMessage(e.getMessage());
			errors.add(experimentError);
		}
	}

	private void feignNEFApplicationPost(NetworkServiceTemplate body, Map<String, IEAPZoneDetails> zones,
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
	public ResponseEntity<List<ExperimentError>> experimentTerminateExperimentNameDelete(String sessionId,
			String experimentName) {

		Optional<Session> session = SessionUtil.getSession(sessionId, sessionRepository,
				xrproperties.getSessionValidTimeInSeconds());
		if (session.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		SouthNodeExperiment experiment = this.southNodeNSTRepository
				.findByUserAndExperimentName(session.get().getUser(), experimentName);

		List<ExperimentError> errors = new ArrayList<>();

		//Only one Qosium experiment at a time in the plaform, otherwise ignore lock status
		if(Boolean.TRUE.equals(experiment.getNetworkServiceTemplate().getQosiumMonitoring())) {
			this.releaseQosiumLock(experiment.getId());
			this.qosiumDelete(experiment, errors);
		}
		
		this.feignTerminateAllInstances(experiment, session.get(), errors);

		if (errors.isEmpty()) {

			experiment.getNetworkServiceTemplate().getSouthNodeAdapterNetworkServiceTemplate()
					.setStatus(StatusEnum.TERMINATE_SUCCESS);
			southNodeNSTRepository.save(experiment);

			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		} else {

			experiment.getNetworkServiceTemplate().getSouthNodeAdapterNetworkServiceTemplate()
					.setStatus(StatusEnum.TERMINATE_FAILED);
			southNodeNSTRepository.save(experiment);

			return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void feignTerminateAllInstances(SouthNodeExperiment experiment, Session session,
			List<ExperimentError> errors) {

		SessionUtil.checkAndRefreshSessionIeapTokenIfExpired(session, ieapClientNBI, sessionRepository,
				this.xrproperties);
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
		experiment.setIeapInstances(List.of());
		this.southNodeNSTRepository.save(experiment);
	}
	
	private void qosiumDelete(SouthNodeExperiment experiment, List<ExperimentError> errors) {
		
		try {
			this.qosiumClient.stopExperiment(experiment.getMonitoringId());
		} catch (FeignException e) {
			ExperimentError experimentError = new ExperimentError();
			experimentError.setMessage(e.getMessage());
			errors.add(experimentError);
		}
	}

	// CASCADE DELETION FOR UNIFIED WEB PORTAL //

	@Override
	public ResponseEntity<Void> experimentEndTrialTrialIdDelete(Integer trialId) {

		List<SouthNodeExperiment> trialExperiments = this.southNodeNSTRepository
				.findByNetworkServiceTemplate_TrialId(trialId);

		trialExperiments.stream().forEach(trialExperiment -> {
			if(trialExperiment.getNetworkServiceTemplate().getQosiumMonitoring()) {
				this.releaseQosiumLock(trialExperiment.getId());
			}
		});

		trialExperiments.stream().forEach(trialExperiment -> {

			if (trialExperiment.getNetworkServiceTemplate().getSouthNodeAdapterNetworkServiceTemplate().getStatus()
					.equals(StatusEnum.LAUNCH_SUCCESS)) {

				Token newIeapToken = ieapClientNBI.createAccessToken(xrproperties.getIeapAccess().getCredentials());
				trialExperiment.getIeapInstances().stream().forEach(ieapInstance -> {

					try {

						this.ieapClientNBI.terminateApplication(ieapInstance.appId(), ieapInstance.appInstanceId(),
								ieapInstance.zoneId(), newIeapToken.getAccessToken());

					} catch (Exception e) {
					}
				});
			}
		});
		this.southNodeNSTRepository.deleteAll(trialExperiments);

		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

	// DB LOCK ON RUNNING EXPERIMENTS //

	// Only one experiment can be running at the same time
	private void acquireQosiumLock(String experimentId) throws IllegalStateException {

		Optional<ExperimentLock> experimentLock = this.lockRepository.findById(this.xrproperties.getExperimentLockId());

		if (experimentLock.isEmpty()) {
			ExperimentLock newLock = new ExperimentLock(this.xrproperties.getExperimentLockId(), experimentId);
			this.lockRepository.save(newLock);
		} else {

			if (experimentLock.get().getLock().isEmpty()) {
				experimentLock.get().setLock(experimentId);
				this.lockRepository.save(experimentLock.get());
			} else {
				Optional<SouthNodeExperiment> usedBy = this.southNodeNSTRepository.findById(experimentLock.get().getLock());
				throw new IllegalStateException(usedBy.isEmpty() ? StringUtils.EMPTY : usedBy.get().getExperimentName());
			}
		}
	}

	private boolean isLockedByQosiumExperiment(String experimentId, Optional<ExperimentLock> experimentLock) {
		return !experimentLock.isEmpty() && experimentLock.get().getLock().equals(experimentId);
	}

	private void releaseQosiumLock(String experimentId) {

		Optional<ExperimentLock> experimentLock = this.lockRepository.findById(this.xrproperties.getExperimentLockId());

		if (this.isLockedByQosiumExperiment(experimentId, experimentLock)) {
			experimentLock.get().setLock(StringUtils.EMPTY);
			this.lockRepository.save(experimentLock.get());
		}
	}
}