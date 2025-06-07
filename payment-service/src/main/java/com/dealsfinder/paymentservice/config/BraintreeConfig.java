package com.dealsfinder.paymentservice.config;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BraintreeConfig {

    @Value("${braintree.merchant-id}")
    private String merchantId;

    @Value("${braintree.public-key}")
    private String publicKey;

    @Value("${braintree.private-key}")
    private String privateKey;

    @Value("${braintree.environment}")
    private String environment;

    @Bean
    public BraintreeGateway braintreeGateway() {
        return new BraintreeGateway(
                environment.equalsIgnoreCase("sandbox") ? Environment.SANDBOX : Environment.PRODUCTION,
                merchantId,
                publicKey,
                privateKey
        );
    }
}
