package com.dealsfinder.paymentservice.client;

import com.dealsfinder.dealservice.model.Deal;
import com.dealsfinder.paymentservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "deal-service", url = "http://localhost:8002", configuration = FeignConfig.class)
public interface DealClient {

    @GetMapping("/deals/{id}")
    Deal getDealById(@PathVariable("id") long id);
}