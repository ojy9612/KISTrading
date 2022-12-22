package com.example.kistrading.controller;

import com.example.kistrading.entity.em.OrderType;
import com.example.kistrading.repository.TokenRepository;
import com.example.kistrading.service.InformationService;
import com.example.kistrading.service.TokenService;
import com.example.kistrading.service.TradeService;
import com.example.kistrading.service.WebClientConnector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TestController {

    @Value("${kis.train.appkey}")
    private String trainAppkey;
    @Value("${kis.train.appsecret}")
    private String trainAppsecert;
    @Value("${kis.train.account}")
    private String trainAccountNum;

    @Value("${kis.real.appkey}")
    private String realAppkey;
    @Value("${kis.real.appsecret}")
    private String realAppsecert;
    @Value("${kis.real.account}")
    private String realAccountNum;

    @Value("${kis.mode}")
    private String mode;


    private final WebClientConnector<String> webClientConnectorString;
    private final TokenRepository tokenRepository;
    private final TokenService tokenService;
    private final TradeService tradeService;
    private final InformationService informationService;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;


    @GetMapping("/test1")
    public String test1() {
        return tokenService.getAndDeleteToken();
    }


    @GetMapping("/test2/{token}")
    public JsonNode test2(@PathVariable String token) {

        Map<String, String> reqBody = new HashMap<>();

        reqBody.put("token", token);
        reqBody.put("appkey", trainAppkey);
        reqBody.put("appsecret", trainAppsecert);


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
    public void test3() {
        informationService.getAccountData();
    }

    @GetMapping("/test4")
    public void test4() {
        tradeService.OrderStock(OrderType.BUY, "007680", "0", "10");


    }


}
