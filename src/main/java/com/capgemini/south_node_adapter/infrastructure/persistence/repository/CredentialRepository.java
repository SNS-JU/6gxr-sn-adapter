package com.capgemini.south_node_adapter.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.capgemini.south_node_adapter.infrastructure.persistence.entity.Credential;

public interface CredentialRepository extends MongoRepository<Credential, String> {
	
	@Query("{user:'?0'}")
	Optional<Credential> findItemByUser(String user);
}
