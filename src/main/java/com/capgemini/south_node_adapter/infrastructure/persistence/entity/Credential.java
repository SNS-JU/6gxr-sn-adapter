package com.capgemini.south_node_adapter.infrastructure.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document("credentials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Credential {

	@Id
	@Indexed(unique = true)
	String user;
	
	String password;
	String appProviderId;
}
