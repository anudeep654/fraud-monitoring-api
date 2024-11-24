package com.fraudguard.fraud.service;

import com.fraudguard.shared.model.Transaction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "model-service", url = "${model-service.url}")
public interface ModelServiceClient {
    @PostMapping("/api/v1/model/predict")
    Transaction predictRisk(@RequestBody Transaction transaction);
}
