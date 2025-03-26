package com.capgemini.south_node_adapter.infrastructure.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;

import com.capgemini.south_node_adapter.infrastructure.auth.Token;
import com.capgemini.south_node_adapter.infrastructure.feign.model.EnterpriseDetails;
import com.capgemini.south_node_adapter.infrastructure.feign.model.IEAPApplication;
import com.capgemini.south_node_adapter.infrastructure.feign.model.Oauth2TokenBody;
import com.capgemini.south_node_adapter.infrastructure.feign.model.ZoneDetails;

@FeignClient(value = "ieapClientNBI", url = "http://10.15.125.12:31000/operatorplatform/v1")
public interface IEAPClientNBI {
	
	@RequestMapping(method = RequestMethod.POST, value = "/oauth2/token", consumes = "application/json", produces = "application/json")
	Token createAccessToken(Oauth2TokenBody body);

	@RequestMapping(method = RequestMethod.GET, value = "/application/onboarding/{appProviderId}/all", consumes = "application/json", produces = "application/json")
	List<IEAPApplication> getApplications(@PathVariable("appProviderId") String appProviderId,
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader);

	@RequestMapping(method = RequestMethod.DELETE, value = "/application/lcm/{appId}/instance/{appInstanceId}/zone/{zoneId}", consumes = "application/json", produces = "application/json")
	Void terminateApplication(@PathVariable("appId") String appId, @PathVariable("appInstanceId") String appInstanceId,
			@PathVariable("zoneId") String zoneId, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader);

	@RequestMapping(method = RequestMethod.GET, value = "/enterprise/user/{appProviderId}", consumes = "application/json", produces = "application/json")
	EnterpriseDetails getUser(@PathVariable("appProviderId") String appProviderId,
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader);

	@RequestMapping(method = RequestMethod.GET, value = "/edgeCloud/tenant/{domainName}/zones", consumes = "application/json", produces = "application/json")
	List<ZoneDetails> getZones(@PathVariable("domainName") String domainName,
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader);

	@RequestMapping(method = RequestMethod.POST, value = "/application/lcm/{appId}", consumes = "multipart/form-data", produces = "application/json")
	List<String> launchApplication(@PathVariable("appId") String appId,
			@RequestPart("appProviderId") String appProviderId, @RequestPart("zoneInfo") String zoneInfo,
			@RequestPart("updateConfig") boolean updateConfig, @RequestPart("appVersion") String appVersion,
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader);
}