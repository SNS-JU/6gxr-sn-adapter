package com.capgemini.south_node_adapter.infrastructure.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.capgemini.south_node_adapter.domain.service.ApplicationsService;
import com.capgemini.south_node_adapter.domain.service.ExperimentService;
import com.capgemini.south_node_adapter.domain.service.GpsisService;
import com.capgemini.south_node_adapter.domain.service.LoginService;
import com.capgemini.south_node_adapter.domain.service.RegisterService;
import com.capgemini.south_node_adapter.domain.service.SlicesService;
import com.capgemini.south_node_adapter.infrastructure.feign.IEAPClientNBI;
import com.capgemini.south_node_adapter.infrastructure.feign.NEFClient;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.CredentialRepository;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.LockRepository;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.SessionRepository;
import com.capgemini.south_node_adapter.infrastructure.persistence.repository.SouthNodeNSTRepository;
import com.capgemini.south_node_adapter.infrastructure.service.ApplicationsServiceImpl;
import com.capgemini.south_node_adapter.infrastructure.service.ExperimentServiceImpl;
import com.capgemini.south_node_adapter.infrastructure.service.GpsisServiceImpl;
import com.capgemini.south_node_adapter.infrastructure.service.LoginServiceImpl;
import com.capgemini.south_node_adapter.infrastructure.service.RegisterServiceImpl;
import com.capgemini.south_node_adapter.infrastructure.service.SlicesServiceImpl;

@Configuration
@EnableConfigurationProperties(XRProperties.class)
public class SouthNodeAdapterConfiguration {

	@Bean
	RegisterService registerService(IEAPClientNBI ieapClientNBI, CredentialRepository credentialRepository) {
		return RegisterServiceImpl.builder().ieapClientNBI(ieapClientNBI).credentialRepository(credentialRepository)
				.build();
	}

	@Bean
	LoginService loginService(IEAPClientNBI ieapClientNBI, CredentialRepository credentialRepository,
			SessionRepository sessionRepository) {
		return LoginServiceImpl.builder().ieapClientNBI(ieapClientNBI).credentialRepository(credentialRepository)
				.sessionRepository(sessionRepository).build();
	}

	@Bean
	ApplicationsService applicationsService(IEAPClientNBI ieapClientNBI, SessionRepository sessionRepository,
			XRProperties xrProperties) {
		return ApplicationsServiceImpl.builder().ieapClientNBI(ieapClientNBI).sessionRepository(sessionRepository)
				.xrProperties(xrProperties).build();
	}

	@Bean
	GpsisService gpsisService(XRProperties xrProperties) {
		return GpsisServiceImpl.builder().xrProperties(xrProperties).build();
	}

	@Bean
	SlicesService slicesService(XRProperties xrProperties) {
		return SlicesServiceImpl.builder().xrProperties(xrProperties).build();
	}

	@Bean
	ExperimentService experimentService(IEAPClientNBI ieapClientNBI, SessionRepository sessionRepository,
			SouthNodeNSTRepository southNodeNSTRepository, XRProperties xrProperties, NEFClient nefClient,
			LockRepository lockRepository) {
		return ExperimentServiceImpl.builder().ieapClientNBI(ieapClientNBI).sessionRepository(sessionRepository)
				.xrproperties(xrProperties).southNodeNSTRepository(southNodeNSTRepository).nefClient(nefClient)
				.lockRepository(lockRepository).build();
	}
}
