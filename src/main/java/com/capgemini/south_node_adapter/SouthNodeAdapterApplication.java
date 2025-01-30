package com.capgemini.south_node_adapter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.capgemini.south_node_adapter.infrastructure.configuration.XRProperties;

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(XRProperties.class)
public class SouthNodeAdapterApplication {

	public static void main(String[] args) {
		SpringApplication.run(SouthNodeAdapterApplication.class, args);
	}
}
