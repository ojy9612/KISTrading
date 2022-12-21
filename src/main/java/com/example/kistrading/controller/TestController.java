package com.example.kistrading.controller;

import com.example.kistrading.repository.TokenRepository;
import com.example.kistrading.service.InformationService;
import com.example.kistrading.service.TokenService;
import com.example.kistrading.service.WebClientConnector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TestController {

    @Value("${kis.train.appkey}")
    private String appkey;
    @Value("${kis.train.appsecret}")
    private String appsecert;


    private final WebClientConnector<String> webClientConnectorString;
    private final TokenRepository tokenRepository;
    private final TokenService tokenService;
    private final InformationService informationService;
    private final ObjectMapper objectMapper;

    @GetMapping("/test1")
    @Transactional
    public String test1() {
        return tokenService.getAndDeleteToken();
    }


    @GetMapping("/test2/{token}")
    public JsonNode test2(@PathVariable String token) {

        Map<String, String> reqBody = new HashMap<>();

        reqBody.put("token", token);
        reqBody.put("appkey", appkey);
        reqBody.put("appsecret", appsecert);


        JsonNode test;
        try {
            test = objectMapper.readTree(webClientConnectorString.connect(HttpMethod.POST, "/oauth2/revokeP",
                    null, null, reqBody, String.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return test;


    }

    @GetMapping("/test3")
    @Transactional
    public void test3() {
        informationService.getAccountData();
    }


}
