package com.example.kistrading.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import javax.net.ssl.SSLException;
import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final PropertiesMapping pm;

    /**
     * KIS 통신용 WebClient 설정
     * TLS1.3 버전과 maxIdleTime 설정을 했다.
     *
     * @return WebClient
     */
    @Bean
    @Qualifier("WebClientKIS")
    public WebClient webClientKIS() {

        // The connection observed an error  reactor.netty.http.client.PrematureCloseException: Connection prematurely closed BEFORE response
        // 위 에러 떄문에 추가 함.(패킷 단계에서 분석해야 함) 'io.micrometer:micrometer-core' 를 dependency 에 추가해야 함.
        ConnectionProvider provider = ConnectionProvider.builder("custom-provider")
                .maxConnections(100)
                .maxIdleTime(Duration.ofSeconds(4))
                .maxLifeTime(Duration.ofSeconds(4))
                .pendingAcquireTimeout(Duration.ofMillis(5000))
                .pendingAcquireMaxCount(-1)
                .evictInBackground(Duration.ofSeconds(30))
                .lifo()
                .metrics(true)
                .build();

        final SslContext sslContextForTls13;
        try {
            sslContextForTls13 = SslContextBuilder.forClient()
                    .protocols("TLSv1.3")
                    .build();
        } catch (SSLException e) {
            throw new RuntimeException(e);
        }

        final HttpClient httpClientForTls13 = HttpClient.create(provider)
                .secure(ssl -> ssl.sslContext(sslContextForTls13));

        return WebClient.builder()
                .baseUrl(pm.getDomain())
                .clientConnector(new ReactorClientHttpConnector(httpClientForTls13))
                .defaultHeader("Content-Type", "application/json; charset=utf-8")
                .build();
    }

    /**
     * 공공데이터포탈 통신용 WebClient 설정
     * ssl 접속을 위한 설정, RequestParam 을 Encoding 하지 않게 설정을 했다.
     *
     * @return WebClient
     */
    @Bean
    @Qualifier("WebClientDataGoKr")
    public WebClient webClientDataGoKr() {
        String url = "https://apis.data.go.kr?" +
                "serviceKey=" + pm.getEnKey();

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(url);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        return WebClient.builder()
                .clientConnector(   // ssl 접속 허용..
                        new ReactorClientHttpConnector(
                                HttpClient.create().secure(t -> {
                                    try {
                                        t.sslContext(SslContextBuilder
                                                .forClient()
                                                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                                                .build());
                                    } catch (SSLException e) {
                                        throw new RuntimeException(e);
                                    }
                                })
                        ))
                .uriBuilderFactory(factory)
                .baseUrl(url)
                .build();
    }
}