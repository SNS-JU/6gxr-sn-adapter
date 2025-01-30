package com.capgemini.south_node_adapter.infrastructure.persistence.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.capgemini.south_node_adapter.infrastructure.persistence.entity.Session;

public interface SessionRepository extends MongoRepository<Session, String> {
	Session findByIeapTokenAccessToken(String accessToken);
}
