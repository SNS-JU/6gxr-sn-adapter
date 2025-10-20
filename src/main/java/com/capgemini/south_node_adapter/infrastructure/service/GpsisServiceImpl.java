package com.capgemini.south_node_adapter.infrastructure.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.capgemini.south_node_adapter.domain.service.GpsisService;
import com.capgemini.south_node_adapter.infrastructure.auth.SessionUtil;
import com.capgemini.south_node_adapter.infrastructure.configuration.XRProperties;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.Gpsi;
import com.capgemini.south_node_adapter.infrastructure.persistence.entity.Session;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.GpsiRepository;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.SessionRepository;

import lombok.Builder;

@Builder
public class GpsisServiceImpl implements GpsisService {

	GpsiRepository gpsiRepository;
	SessionRepository sessionRepository;
	XRProperties xrproperties;

	@Override
	public ResponseEntity<List<String>> gpsisGet(String sessionId) {
		
		Optional<Session> session = SessionUtil.getSession(sessionId, sessionRepository,
				xrproperties.getSessionValidTimeInSeconds());
		if (session.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		return new ResponseEntity<>(gpsiRepository.findAll().stream().map(Gpsi::getUe).toList(), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Void> gpsisPut(String sessionId, List<String> body) {
		
		Optional<Session> session = SessionUtil.getSession(sessionId, sessionRepository,
				xrproperties.getSessionValidTimeInSeconds());
		if (session.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}		
		
		gpsiRepository.deleteAll();
		gpsiRepository.saveAll(body.stream().map(ue -> {
			Gpsi toSave = new Gpsi();
			toSave.setUe(ue);
			
			return toSave;
		}).toList());
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}
}
