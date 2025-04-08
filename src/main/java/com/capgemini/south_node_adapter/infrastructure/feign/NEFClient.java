package com.capgemini.south_node_adapter.infrastructure.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "nefClient", url = "http://10.3.3.41/5tonic-exposure/v1")
public interface NEFClient {
	
	@RequestMapping(method = RequestMethod.POST, value = "/subscriptions/{subscriptionId}/profile/{profileId}", consumes = "application/json", produces = "application/json")
	Object upsertSubcription(@PathVariable("subscriptionId") String gpsi, @PathVariable("profileId") String sliceProfileName);
}