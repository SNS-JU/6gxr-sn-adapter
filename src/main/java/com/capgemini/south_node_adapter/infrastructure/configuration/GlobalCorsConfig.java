package com.capgemini.south_node_adapter.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalCorsConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {

		registry.addMapping("/**").allowedOrigins("http://193.166.32.46:4040", "http://10.15.126.160:5173",
				"http://10.15.126.160:5174").allowedMethods("*").allowedHeaders("*")
				.allowCredentials(true);
	}
}
