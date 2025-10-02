package com.capgemini.south_node_adapter.infrastructure.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.capgemini.south_node_adapter.domain.service.SlicesService;
import com.capgemini.south_node_adapter.infrastructure.auth.SessionUtil;
import com.capgemini.south_node_adapter.infrastructure.configuration.XRProperties;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.Session;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.SessionRepository;

import lombok.Builder;

@Builder
public class SlicesServiceImpl implements SlicesService {

	XRProperties xrProperties;
	SessionRepository sessionRepository;

	@Override
	public ResponseEntity<List<String>> slicesGet(String sessionId) {
		
		Optional<Session> session = SessionUtil.getSession(sessionId, sessionRepository,
				xrProperties.getSessionValidTimeInSeconds());
		if (session.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<>(xrProperties.getTcSlices().keySet().stream().toList(), HttpStatus.OK);
	}

}
