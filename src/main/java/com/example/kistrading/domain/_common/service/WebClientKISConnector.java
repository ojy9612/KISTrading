package com.example.kistrading.domain._common.service;

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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class WebClientKISConnector<T> {
    private final ObjectMapper objectMapper;
    @Qualifier("webClientKIS")
    private final WebClient webClientKIS;
    private final Queue<LocalDateTime> timeQueue = new LinkedList<>();


    /**
     * status code 가 200이 아닐 시 webclient 상에서 에러처리를 별도로 할 수 있지만 현재는 그냥 return 하게 해둠.
     * 1초당 20개의 요청만 보내게끔 설계했다.
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

            if (timeQueue.size() >= 19) {
                while (true) {
                    if (timeQueue.isEmpty()) {
                        break;
                    }
                    if (timeQueue.peek().plusSeconds(1).isAfter(LocalDateTime.now())) {
                        this.wait(Duration.between(Objects.requireNonNull(timeQueue.poll()), LocalDateTime.now()).toMillis());
                        break;
                    } else {
                        timeQueue.poll();
                    }
                }
            }

            ResponseEntity<T> block = webClientKIS.method(methodType)
                    .uri(uriBuilder -> uriBuilder
                            .path(uri)
                            .queryParams(params)
                            .build())
                    .headers(httpHeaders -> httpHeaders.setAll(headers))
                    .bodyValue(requestBodyJson)
                    .exchangeToMono(clientResponse -> clientResponse.toEntity(classType))
                    .block();

            timeQueue.add(LocalDateTime.now());

            return Objects.requireNonNull(block).getBody();

        } catch (JsonProcessingException e) {
            throw new RuntimeException("json error");
        } catch (InterruptedException e) {
            throw new RuntimeException("time error");
        }

    }

    /**
     * 위 함수와 동일하다. Header 정보를 포함하고 싶을 때 사용
     */
    public synchronized ResponseEntity<T> connectIncludeHeader(HttpMethod methodType, String uri, Map<String, String> reqHeader,
                                                               MultiValueMap<String, String> reqParam, Map<String, String> reqBody, Class<T> classType) {

        Map<String, String> body = reqBody != null ? reqBody : new HashMap<>();
        Map<String, String> headers = reqHeader != null ? reqHeader : new HashMap<>();
        MultiValueMap<String, String> params = reqParam != null ? reqParam : new LinkedMultiValueMap<>();

        try {
            String requestBodyJson = !body.isEmpty() ? objectMapper.writeValueAsString(body) : "";

            if (timeQueue.size() >= 19) {
                while (true) {
                    if (timeQueue.isEmpty()) {
                        break;
                    }
                    if (timeQueue.peek().plusSeconds(1).isAfter(LocalDateTime.now())) {
                        this.wait(Duration.between(Objects.requireNonNull(timeQueue.poll()), LocalDateTime.now()).toMillis());
                        break;
                    } else {
                        timeQueue.poll();
                    }
                }
            }

            ResponseEntity<T> block = webClientKIS.method(methodType)
                    .uri(uriBuilder -> uriBuilder
                            .path(uri)
                            .queryParams(params)
                            .build())
                    .headers(httpHeaders -> httpHeaders.setAll(headers))
                    .bodyValue(requestBodyJson)
                    .exchangeToMono(clientResponse -> clientResponse.toEntity(classType))
                    .block();

            timeQueue.add(LocalDateTime.now());

            return block;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
