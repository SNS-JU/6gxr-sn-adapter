package com.capgemini.south_node_adapter.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.capgemini.south_node_adapter.infrastructure.auth.Token;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Document("sessions")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Session {
	
	@Id
	String sessionId;
	
	@NonNull
	String user;
	
	@NonNull
	String appProviderId;

	@NonNull
    Token ieapToken;
	
	@NonNull
	LocalDateTime ieapTokenCreatedAt;
}
