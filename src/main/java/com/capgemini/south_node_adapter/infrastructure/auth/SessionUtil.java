package com.capgemini.south_node_adapter.infrastructure.auth;

import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.capgemini.south_node_adapter.infrastructure.configuration.XRProperties;
import com.capgemini.south_node_adapter.infrastructure.feign.IEAPClientNBI;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.Session;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.SessionRepository;

public class SessionUtil {

	public static Optional<Session> getSession(String sessionId, SessionRepository sessionRepository,
			Long sessionValidTimeInSeconds) {

		Optional<Session> session = sessionRepository.findById(sessionId);
		if (session.isEmpty()) {
			return Optional.empty();
		}

		boolean expiredSession = session.get().getCreatedAt().plusSeconds(sessionValidTimeInSeconds)
				.isEqual(LocalDateTime.now())
				|| session.get().getCreatedAt().plusSeconds(sessionValidTimeInSeconds).isBefore(LocalDateTime.now());

		if (expiredSession) {
			sessionRepository.delete(session.get());
			return Optional.empty();
		} else {
			return sessionRepository.findById(sessionId);
		}
	}

	public static void checkAndRefreshSessionIeapTokenIfExpired(Session session, IEAPClientNBI ieapClientNBI,
			SessionRepository sessionRepository, XRProperties xrProperties) {

		boolean expiredIeapToken = session.getIeapTokenCreatedAt().plusSeconds(session.getIeapToken().getExpiresIn())
				.isEqual(LocalDateTime.now())
				|| session.getIeapTokenCreatedAt().plusSeconds(session.getIeapToken().getExpiresIn())
						.isBefore(LocalDateTime.now());

		if (expiredIeapToken) {

			Token newToken = ieapClientNBI.createAccessToken(xrProperties.getIeapAccess().getCredentials());
			newToken.setAccessToken(xrProperties.getIeapAccess().getBearerPrefix() + StringUtils.SPACE + newToken.getAccessToken());

			session.setIeapToken(newToken);
			session.setIeapTokenCreatedAt(LocalDateTime.now());
			sessionRepository.save(session);
		}
	}
}
