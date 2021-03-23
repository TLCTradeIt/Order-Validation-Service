package com.example.ordervalidationservice;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WebClientService {

    @Bean
    public WebClient.Builder getWebClientBuilder() {
        return WebClient.builder();
    }

}