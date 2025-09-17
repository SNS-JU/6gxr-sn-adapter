package com.capgemini.south_node_adapter.infrastructure.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "qosiumClient", url = "172.27.28.162:8001")
public interface QosiumClient {
	
    @PostMapping("/start_experiment")
    void startExperiment(
        @RequestParam("experiment_type") String experimentType,
        @RequestParam("experiment_description") String experimentDescription
    );

    @PostMapping("/stop_experiment")
    void stopExperiment(
        @RequestParam("experiment_id") String experimentId
    );
}