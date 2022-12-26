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

import javax.annotation.PostConstruct;
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

    private Token token;

    /**
     * 생성자를 통해 토큰을 가져온다.
     */
    @PostConstruct
    @Transactional
    public void init() {
        token = this.createDeleteToken();
    }

    /**
     * 토큰 생성 함수
     * 직접 사용 금지
     */
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

        Token t = Token.builder()
                .tokenValue("Bearer " + jsonNode.get("access_token").asText())
                .expiredDate(LocalDateTime.parse(
                        jsonNode.get("access_token_token_expired").asText(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss")))
                .mode(pm.getMode())
                .build();

        return tokenRepository.save(t);
    }

    /**
     * 토큰을 가져오거나 Expired 되었다면 삭제하고 생성하는 함수
     * 직접 사용 금지
     *
     * @return Token
     */
    @Transactional
    public Token createDeleteToken() {
        List<Token> tokenList = tokenRepository.findAll();

        Token result = null;

        for (Token t : tokenList) {
            if (t.getMode().equals(pm.getMode())) {
                if (t.getExpiredDate().minusHours(6).isBefore(LocalDateTime.now())) {
                    tokenRepository.delete(t);
                } else {
                    result = t;
                }
            }
        }

        return result != null ? result : this.createToken();
    }

    /**
     * 토큰이 expired 되었는지 DB에 들리지 않고 확인함.
     * 직접 사용 가능
     *
     * @return String Token value
     */
    @Transactional
    public String checkGetToken() {
        if (token.getExpiredDate().minusHours(6).isBefore(LocalDateTime.now())) {
            return this.createDeleteToken().getTokenValue();
        } else {
            return token.getTokenValue();
        }
    }

}
