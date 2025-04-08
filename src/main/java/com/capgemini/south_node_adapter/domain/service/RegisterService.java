package com.capgemini.south_node_adapter.domain.service;

import org.springframework.http.ResponseEntity;

public interface RegisterService {
	
    public ResponseEntity<Void> registerDelete(String user,String password, String appProviderId);
    public ResponseEntity<Void> registerPost(String user,String password, String appProviderId);
}
