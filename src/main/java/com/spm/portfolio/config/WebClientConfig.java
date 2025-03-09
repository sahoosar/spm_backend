package com.spm.portfolio.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    private final String baseUrl;
    private final int maxConnections;
    private final int pendingAcquireMaxCount;
    private final int pendingAcquireTimeoutMs;
    private final int connectTimeoutMs;
    private final int responseTimeoutSeconds;
    private final int readTimeoutSeconds;
    private final int writeTimeoutSeconds;

    // Constructor Injection with @Value
    public WebClientConfig(
            @Value("${webclient.base-url}") String baseUrl,
            @Value("${webclient.max-connections}") int maxConnections,
            @Value("${webclient.pending-acquire-max-count}") int pendingAcquireMaxCount,
            @Value("${webclient.pending-acquire-timeout-ms}") int pendingAcquireTimeoutMs,
            @Value("${webclient.connect-timeout-ms}") int connectTimeoutMs,
            @Value("${webclient.response-timeout-seconds}") int responseTimeoutSeconds,
            @Value("${webclient.read-timeout-seconds}") int readTimeoutSeconds,
            @Value("${webclient.write-timeout-seconds}") int writeTimeoutSeconds
    ) {
        this.baseUrl = baseUrl;
        this.maxConnections = maxConnections;
        this.pendingAcquireMaxCount = pendingAcquireMaxCount;
        this.pendingAcquireTimeoutMs = pendingAcquireTimeoutMs;
        this.connectTimeoutMs = connectTimeoutMs;
        this.responseTimeoutSeconds = responseTimeoutSeconds;
        this.readTimeoutSeconds = readTimeoutSeconds;
        this.writeTimeoutSeconds = writeTimeoutSeconds;
    }
    @Bean
    public WebClient webClient() {
        ConnectionProvider provider = ConnectionProvider.builder("custom")
                .maxConnections(maxConnections)
                .pendingAcquireMaxCount(pendingAcquireMaxCount)
                .pendingAcquireTimeout(Duration.ofMillis(pendingAcquireTimeoutMs))
                .lifo()
                .build();

        HttpClient httpClient = HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
                .responseTimeout(Duration.ofSeconds(responseTimeoutSeconds))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(readTimeoutSeconds))
                                .addHandlerLast(new WriteTimeoutHandler(writeTimeoutSeconds))
                );

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
