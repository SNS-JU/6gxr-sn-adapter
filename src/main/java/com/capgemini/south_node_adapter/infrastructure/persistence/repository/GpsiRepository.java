package com.capgemini.south_node_adapter.infrastructure.persistence.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.capgemini.south_node_adapter.infrastructure.persistence.entity.Gpsi;

public interface GpsiRepository extends MongoRepository<Gpsi, String> {

}
