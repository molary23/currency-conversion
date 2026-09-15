package com.xe.ratealerts.config;

import static com.xe.ratealerts.utils.Constants.RATE_CLIENT;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RequiredArgsConstructor
@Configuration
public class RestConfig {
    private final AppConfig appConfig;

    @Bean(RATE_CLIENT)
    public WebClient createWebClient() {
        String credentials = Base64.getEncoder()
            .encodeToString((appConfig.getAccountId() + ":" + appConfig.getApiKey()).getBytes(StandardCharsets.US_ASCII));
        return WebClient.builder()
            .baseUrl(appConfig.getUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
            .build();
    }
}
