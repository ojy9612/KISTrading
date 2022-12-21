package com.example.kistrading.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class WebClientConnector<T> {
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

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
    public T connect(HttpMethod methodType, String uri, MultiValueMap<String, String> reqHeader,
                     MultiValueMap<String, String> reqParam, Map<String, String> reqBody, Class<T> classType) {

        Map<String, String> body = reqBody != null ? reqBody : new HashMap<>();
        MultiValueMap<String, String> headers = reqHeader != null ? reqHeader : new LinkedMultiValueMap<>();
        MultiValueMap<String, String> params = reqParam != null ? reqParam : new LinkedMultiValueMap<>();

        try {
            String requestBodyJson = !body.isEmpty() ? objectMapper.writeValueAsString(body) : "";

            ResponseEntity<T> block = webClient.method(methodType)
                    .uri(uriBuilder -> uriBuilder
                            .path(uri)
                            .queryParams(params)
                            .build())
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .bodyValue(requestBodyJson)
                    .exchangeToMono(clientResponse -> clientResponse.toEntity(classType))
                    .block();

            return Objects.requireNonNull(block).getBody();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}
