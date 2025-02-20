package com.capgemini.south_node_adapter.infrastructure.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Document("lock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExperimentLock {

	@Id
	String id;
	
	@NonNull
	Boolean lock;
}
