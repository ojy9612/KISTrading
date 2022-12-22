package com.example.kistrading.controller;

import com.example.kistrading.config.PropertiesMapping;
import com.example.kistrading.entity.em.OrderType;
import com.example.kistrading.service.InformationService;
import com.example.kistrading.service.TokenService;
import com.example.kistrading.service.TradeService;
import com.example.kistrading.service.WebClientConnector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final PropertiesMapping pm;
    
    private final WebClientConnector<String> webClientConnectorString;
    private final TokenService tokenService;
    private final TradeService tradeService;
    private final InformationService informationService;
    private final ObjectMapper objectMapper;


    @GetMapping("/test1")
    public String test1() {
        return tokenService.getAndDeleteToken();
    }


    @GetMapping("/test2/{token}")
    public JsonNode test2(@PathVariable String token) {

        Map<String, String> reqBody = new HashMap<>();

        reqBody.put("token", token);
        reqBody.put("appkey", pm.getAppKey());
        reqBody.put("appsecret", pm.getAppSecret());


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
