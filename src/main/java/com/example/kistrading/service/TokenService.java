package com.example.kistrading.service;

import com.example.kistrading.entity.Token;
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
    private String appkey;
    @Value("${kis.train.appsecret}")
    private String appsecert;

    @Transactional
    public Token createToken() {
        Map<String, String> reqBody = new HashMap<>();

        reqBody.put("grant_type", "client_credentials");
        reqBody.put("appkey", appkey);
        reqBody.put("appsecret", appsecert);

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
                .token("Bearer " + jsonNode.get("access_token").asText())
                .expiredDate(LocalDateTime.parse(
                        jsonNode.get("access_token_token_expired").asText(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss")))
                .build();

        return tokenRepository.save(token);
    }

    @Transactional
    public String getAndDeleteToken(){
        List<Token> tokenList = tokenRepository.findAll();

        Token result = null;

        for (Token token : tokenList) {
            if (token.getExpiredDate().minusHours(12).isBefore(LocalDateTime.now())){
                tokenRepository.delete(token);
            } else {
                result = token;
            }
        }

        return result != null ? result.getToken() : this.createToken().getToken();
    }

}
