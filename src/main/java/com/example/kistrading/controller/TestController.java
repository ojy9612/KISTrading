package com.example.kistrading.controller;

import com.example.kistrading.entity.Token;
import com.example.kistrading.repository.TokenRepository;
import com.example.kistrading.service.WebClientConnector;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
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


    private final WebClientConnector webClientConnector;
    private final TokenRepository tokenRepository;

    @GetMapping("/test1")
    @Transactional
    public JsonNode test1() {


        Map<String, String> reqBody = new HashMap<>();
        MultiValueMap<String, String> reqParam = new LinkedMultiValueMap<>();
        MultiValueMap<String, String> reqHeader = new LinkedMultiValueMap<>();

        reqBody.put("grant_type", "client_credentials");
        reqBody.put("appkey", appkey);
        reqBody.put("appsecret", appsecert);

        JsonNode jsonNode = webClientConnector.connectKIS(HttpMethod.POST, "/oauth2/tokenP",
                reqHeader, reqParam, reqBody);

        if (jsonNode.get("error_code") != null) {
            return jsonNode;
        }

        Token token = Token.builder()
                .token("Bearer " + jsonNode.get("access_token").asText())
                .expiredDate(LocalDateTime.parse(
                        jsonNode.get("access_token_token_expired").asText(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss")))
                .build();

        tokenRepository.save(token);

        return jsonNode;
    }


    @GetMapping("/test2/{token}")
    public String test2(@PathVariable String token) {

        Map<String, String> reqBody = new HashMap<>();
        MultiValueMap<String, String> reqParam = new LinkedMultiValueMap<>();
        MultiValueMap<String, String> reqHeader = new LinkedMultiValueMap<>();

        reqBody.put("token", token);
        reqBody.put("appkey", appkey);
        reqBody.put("appsecret", appsecert);


        JsonNode test = webClientConnector.connectKIS(HttpMethod.POST, "/oauth2/revokeP",
                reqHeader, reqParam, reqBody);


        return null;


    }

    @GetMapping("/test3")
    @Transactional
    public JsonNode test3() {


        Map<String, String> reqBody = new HashMap<>();
        MultiValueMap<String, String> reqParam = new LinkedMultiValueMap<>();
        MultiValueMap<String, String> reqHeader = new LinkedMultiValueMap<>();

        List<Token> all = tokenRepository.findAll();

        reqHeader.add("authorization", all.get(0).getToken());
        reqHeader.add("appkey", appkey);
        reqHeader.add("appsecret", appsecert);
        reqHeader.add("tr_id", "VTTC8434R");

        reqParam.add("CANO", "50076882");
        reqParam.add("ACNT_PRDT_CD", "01");
        reqParam.add("AFHR_FLPR_YN", "N");
        reqParam.add("OFL_YN", "");
        reqParam.add("INQR_DVSN", "01");
        reqParam.add("UNPR_DVSN", "01");
        reqParam.add("FUND_STTL_ICLD_YN", "N");
        reqParam.add("FNCG_AMT_AUTO_RDPT_YN", "N");
        reqParam.add("PRCS_DVSN", "01");
        reqParam.add("CTX_AREA_FK100", "");
        reqParam.add("CTX_AREA_NK100", "");


        JsonNode jsonNode = webClientConnector.connectKIS(HttpMethod.GET, "/uapi/domestic-stock/v1/trading/inquire-balance",
                reqHeader, reqParam, reqBody);

        if (jsonNode.get("error_code") != null) {
            return jsonNode;
        }

        Token token = Token.builder()
                .token(jsonNode.get("access_token").asText())
                .expiredDate(LocalDateTime.parse(
                        jsonNode.get("access_token_token_expired").asText(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss")))
                .build();

        tokenRepository.save(token);

        return jsonNode;
    }


}
