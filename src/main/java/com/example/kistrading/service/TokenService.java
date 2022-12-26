package com.example.kistrading.service;

import com.example.kistrading.config.PropertiesMapping;
import com.example.kistrading.entity.Token;
import com.example.kistrading.repository.TokenRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
    private final WebClientKISConnector<String> webClientKISConnectorString;

    private final PropertiesMapping pm;

    @Transactional
    public Token createToken() {
        Map<String, String> reqBody = new HashMap<>();

        reqBody.put("grant_type", "client_credentials");
        reqBody.put("appkey", pm.getAppKey());
        reqBody.put("appsecret", pm.getAppSecret());

        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(
                    webClientKISConnectorString.connect(HttpMethod.POST, "/oauth2/tokenP",
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
                .mode(pm.getMode())
                .build();

        return tokenRepository.save(token);
    }

    @Transactional
    public Token getAndDeleteToken() {
        List<Token> tokenList = tokenRepository.findAll();

        Token result = null;

        for (Token token : tokenList) {
            if (token.getMode().equals(pm.getMode())) {
                if (token.getExpiredDate().minusHours(6).isBefore(LocalDateTime.now())) {
                    tokenRepository.delete(token);
                } else {
                    result = token;
                }
            }
        }

        return result != null ? result : this.createToken();
    }

}
