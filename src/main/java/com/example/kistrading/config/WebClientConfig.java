package com.example.kistrading.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final PropertiesMapping pm;
    @Bean
    public WebClient webClient() {

        return WebClient.builder()
                .baseUrl(pm.getDomain())
                .defaultHeader("Content-Type", "application/json; charset=utf-8")
                .build();
    }
}
