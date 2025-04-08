package com.capgemini.south_node_adapter.infrastructure.persistence.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.capgemini.south_node_adapter.infrastructure.persistence.entity.ExperimentLock;

public interface LockRepository extends MongoRepository<ExperimentLock, String> {

}
