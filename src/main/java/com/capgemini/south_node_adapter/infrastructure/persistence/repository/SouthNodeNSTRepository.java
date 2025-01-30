package com.capgemini.south_node_adapter.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.capgemini.south_node_adapter.infrastructure.persistence.entity.SouthNodeExperiment;

public interface SouthNodeNSTRepository extends MongoRepository<SouthNodeExperiment, String> {

	List<SouthNodeExperiment> findByUser(String user);
	
	SouthNodeExperiment findByUserAndTrialId(String user, Integer trialId);
	void deleteByUserAndTrialId(String user, Integer trialId);
}
