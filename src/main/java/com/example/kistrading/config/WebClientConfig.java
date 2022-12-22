package com.example.kistrading.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${kis.train.domain}")
    private String trainDomain;
    @Value("${kis.real.domain}")
    private String realDomain;
    @Value("${kis.mode}")
    private String mode;

    @Bean
    public WebClient webClient() {

        return WebClient.builder()
                .baseUrl(mode.equals("real") ? realDomain : trainDomain)
                .defaultHeader("Content-Type", "application/json; charset=utf-8")
                .build();
    }
}
