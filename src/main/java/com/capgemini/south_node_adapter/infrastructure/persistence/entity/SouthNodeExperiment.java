package com.capgemini.south_node_adapter.infrastructure.persistence.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.capgemini.south_node_adapter.domain.model.NetworkServiceTemplate;

import lombok.Getter;
import lombok.Setter;

@Document("south_node_nst")
@Getter
@Setter
@CompoundIndex(def = "{'experimentName': 1}", name = "experimentNameUnique", unique = true)
public class SouthNodeExperiment {

	@Id
	String id;
	
	String user;
	String experimentName;
	
	NetworkServiceTemplate networkServiceTemplate;
	List<SouthNodeExperimentIEAPInstance> ieapInstances;
}
