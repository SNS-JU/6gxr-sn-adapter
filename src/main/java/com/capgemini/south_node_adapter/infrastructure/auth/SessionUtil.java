package com.capgemini.south_node_adapter.infrastructure.auth;

import java.time.LocalDateTime;
import java.util.Optional;

import com.capgemini.south_node_adapter.infrastructure.feign.IEAPClientNBI;
import com.capgemini.south_node_adapter.infrastructure.feign.model.Oauth2TokenBody;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.Session;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.SessionRepository;

public class SessionUtil {

	public static Oauth2TokenBody IEAP_CREDENTIALS = Oauth2TokenBody.builder().grantType("client_credentials")
			.clientId("ieap-client").clientSecret("S6uboXdE8tJHLwJ5mUP4ov1q9NjbPwci").scope("all").build();

	public static String BEARER_PREFIX = "Bearer ";
	public static String DEFAULT_DOMAIN = "OPDefault";

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
			SessionRepository sessionRepository) {

		boolean expiredIeapToken = session.getIeapTokenCreatedAt().plusSeconds(session.getIeapToken().getExpiresIn())
				.isEqual(LocalDateTime.now())
				|| session.getIeapTokenCreatedAt().plusSeconds(session.getIeapToken().getExpiresIn())
						.isBefore(LocalDateTime.now());

		if (expiredIeapToken) {

			Token newToken = ieapClientNBI.createAccessToken(IEAP_CREDENTIALS);
			newToken.setAccessToken(BEARER_PREFIX + newToken.getAccessToken());

			session.setIeapToken(newToken);
			session.setIeapTokenCreatedAt(LocalDateTime.now());
			sessionRepository.save(session);
		}
	}
}
