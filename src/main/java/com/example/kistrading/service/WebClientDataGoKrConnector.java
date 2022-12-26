package com.example.kistrading.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class WebClientDataGoKrConnector<T> {
    private final ObjectMapper objectMapper;
    @Qualifier("WebClientDataGoKr")
    private final WebClient webClientDataGoKr;
    
    /**
     * status code 가 200이 아닐 시 webclient 상에서 에러처리를 별도로 할 수 있지만 현재는 그냥 return 하게 해둠.
     *
     * @param methodType HTTP method
     * @param uri        base url 을 제외 한 uri
     * @param reqHeader  header 정보, null 을 넣어도 됨
     * @param reqParam   query parameters 정보, null 을 넣어도 됨
     * @param reqBody    request body 정보, null 을 넣어도 됨
     * @param classType  Generic class type (ex. String.class)
     * @return response 를 JsonNode 로 리턴
     */
    public synchronized T connect(HttpMethod methodType, String uri, Map<String, String> reqHeader,
                                  MultiValueMap<String, String> reqParam, Map<String, String> reqBody, Class<T> classType) {
        Map<String, String> body = reqBody != null ? reqBody : new HashMap<>();
        Map<String, String> headers = reqHeader != null ? reqHeader : new HashMap<>();
        MultiValueMap<String, String> params = reqParam != null ? reqParam : new LinkedMultiValueMap<>();

        try {
            String requestBodyJson = !body.isEmpty() ? objectMapper.writeValueAsString(body) : "";

            ResponseEntity<T> block = webClientDataGoKr.method(methodType)
                    .uri(uriBuilder -> uriBuilder
                            .path(uri)
                            .queryParams(params)
                            .build())
                    .headers(httpHeaders -> httpHeaders.setAll(headers))
                    .bodyValue(requestBodyJson)
                    .exchangeToMono(clientResponse -> clientResponse.toEntity(classType))
                    .block();


            return Objects.requireNonNull(block).getBody();

        } catch (JsonProcessingException e) {
            throw new RuntimeException("json error");
        }
    }

    public synchronized ResponseEntity<T> connectIncludeHeader(HttpMethod methodType, String uri, Map<String, String> reqHeader,
                                                               MultiValueMap<String, String> reqParam, Map<String, String> reqBody, Class<T> classType) {

        Map<String, String> body = reqBody != null ? reqBody : new HashMap<>();
        Map<String, String> headers = reqHeader != null ? reqHeader : new HashMap<>();
        MultiValueMap<String, String> params = reqParam != null ? reqParam : new LinkedMultiValueMap<>();

        try {
            String requestBodyJson = !body.isEmpty() ? objectMapper.writeValueAsString(body) : "";

            return webClientDataGoKr.method(methodType)
                    .uri(uriBuilder -> uriBuilder
                            .path(uri)
                            .queryParams(params)
                            .build())
                    .headers(httpHeaders -> httpHeaders.setAll(headers))
                    .bodyValue(requestBodyJson)
                    .exchangeToMono(clientResponse -> clientResponse.toEntity(classType))
                    .block();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}
