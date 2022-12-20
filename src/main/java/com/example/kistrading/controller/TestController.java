package com.example.kistrading.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TestController {

    @Value("${kis.domain.train}")
    private String train;
    @Value("${kis.train.appkey}")
    private String appkey;
    @Value("${kis.train.appsecret}")
    private String appsecert;

    private final ObjectMapper objectMapper;


    @GetMapping("/test")
    public String test() {
        WebClient webClient = WebClient.builder()
                .baseUrl(train)
                .defaultHeader("Content-Type", "application/json")
                .build();


        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", "client_credentials");
        requestBody.put("appkey", appkey);
        requestBody.put("appsecret", appsecert);

        String s;
        try {
            s = objectMapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return webClient.post()
                .uri("/oauth2/tokenP")
                .bodyValue(s)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @GetMapping("/test2/{token}")
    public String test2(@PathVariable String token) {
        WebClient webClient = WebClient.builder()
                .baseUrl(train)
                .defaultHeader("Content-Type", "application/json")
                .build();

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("token", token);
        requestBody.put("appkey", appkey);
        requestBody.put("appsecret", appsecert);

        String s;
        try {
            s = objectMapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return webClient.post()
                .uri("/oauth2/revokeP")
                .bodyValue(s)
                .retrieve()
                .bodyToMono(String.class)
                .block();

    }


}
