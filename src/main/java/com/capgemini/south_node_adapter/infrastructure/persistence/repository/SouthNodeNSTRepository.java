package com.capgemini.south_node_adapter.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.capgemini.south_node_adapter.infrastructure.persistence.entity.SouthNodeExperiment;

public interface SouthNodeNSTRepository extends MongoRepository<SouthNodeExperiment, String> {

	List<SouthNodeExperiment> findByUser(String user);
	List<SouthNodeExperiment> findByNetworkServiceTemplate_TrialId(Integer trialId);
	
	SouthNodeExperiment findByUserAndExperimentName(String user, String experimentName);
	void deleteByUserAndExperimentName(String user, String experimentName);
	void deleteByNetworkServiceTemplate_TrialId(Integer trialId);
}