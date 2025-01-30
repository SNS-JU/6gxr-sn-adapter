package com.capgemini.south_node_adapter.infrastructure.configuration;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
public class MongoDBConfiguration {

	@Bean
	public MongoCustomConversions mongoCustomConversions() {
		
		return new MongoCustomConversions(Arrays.asList(new Converter<Date, OffsetDateTime>() {
			
			@Override
			public OffsetDateTime convert(Date date) {
				return date.toInstant().atOffset(ZoneOffset.UTC);
			}
		}, new Converter<OffsetDateTime, Date>() {
			
			@Override
			public Date convert(OffsetDateTime offsetDateTime) {
				return Date.from(offsetDateTime.toInstant());
			}
		}));
	}
}
