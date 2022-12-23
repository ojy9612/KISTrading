package com.example.kistrading.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final PropertiesMapping pm;
    @Bean
    public WebClient webClient() {
        final SslContext sslContextForTls13;
        try {
            sslContextForTls13 = SslContextBuilder.forClient()
                    .protocols("TLSv1.3")
                    .build();
        } catch (SSLException e) {
            throw new RuntimeException(e);
        }

        final HttpClient httpClientForTls13 = HttpClient.create()
                .secure(ssl -> ssl.sslContext(sslContextForTls13));

        return WebClient.builder()
                .baseUrl(pm.getDomain())
                .clientConnector(new ReactorClientHttpConnector(httpClientForTls13))
                .defaultHeader("Content-Type", "application/json; charset=utf-8")
                .build();
    }
}
