package com.capgemini.south_node_adapter.infrastructure.persistence.entity;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.capgemini.south_node_adapter.domain.model.NetworkServiceTemplate;

import lombok.Getter;
import lombok.Setter;

@Document("south_node_nst")
@Getter
@Setter
public class SouthNodeExperiment {

	String user;
	Integer trialId;
	
	NetworkServiceTemplate networkServiceTemplate;
	List<SouthNodeExperimentIEAPInstance> ieapInstances;
}
