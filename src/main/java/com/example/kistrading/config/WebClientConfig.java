package com.example.kistrading.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${kis.domain.train}")
    private String domain;

    @Bean
    public WebClient webClient(){

        return WebClient.builder()
                .baseUrl(domain)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
