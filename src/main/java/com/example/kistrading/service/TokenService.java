package com.example.kistrading.service;

import com.example.kistrading.entity.Token;
import com.example.kistrading.entity.em.TradeMode;
import com.example.kistrading.repository.TokenRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;
    private final ObjectMapper objectMapper;
    private final WebClientConnector<String> webClientConnectorString;

    @Value("${kis.train.appkey}")
    private String trainAppkey;
    @Value("${kis.train.appsecret}")
    private String trainAppsecert;
    @Value("${kis.real.appkey}")
    private String realAppkey;
    @Value("${kis.real.appsecret}")
    private String realAppsecert;
    @Value("${kis.mode}")
    private String mode;

    @Transactional
    public Token createToken() {
        Map<String, String> reqBody = new HashMap<>();

        reqBody.put("grant_type", "client_credentials");
        reqBody.put("appkey", mode.equals("real") ? realAppkey : trainAppkey);
        reqBody.put("appsecret", mode.equals("real") ? realAppsecert : trainAppsecert);

        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(
                    webClientConnectorString.connect(HttpMethod.POST, "/oauth2/tokenP",
                            null, null, reqBody, String.class)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Token token = Token.builder()
                .tokenValue("Bearer " + jsonNode.get("access_token").asText())
                .expiredDate(LocalDateTime.parse(
                        jsonNode.get("access_token_token_expired").asText(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss")))
                .mode(TradeMode.getTradeMode(mode))
                .build();

        return tokenRepository.save(token);
    }

    @Transactional
    public String getAndDeleteToken(){
        List<Token> tokenList = tokenRepository.findAll();

        Token result = null;

        for (Token token : tokenList) {
            if (token.getMode().equals(TradeMode.getTradeMode(mode))) {
                if (token.getExpiredDate().minusHours(12).isBefore(LocalDateTime.now())) {
                    tokenRepository.delete(token);
                } else {
                    result = token;
                }
            }
        }

        return result != null ? result.getTokenValue() : this.createToken().getTokenValue();
    }

}
