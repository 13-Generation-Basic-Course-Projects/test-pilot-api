package com.both.testing_pilot_backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebclientConfig {

    private final GithubConfig githubConfig;
    @Value("${postmark.api.token}")
    private String postmarkApiToken;

    @Bean(name = "githubWebClient")
    public WebClient getGithubWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(githubConfig.getApi())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean(name = "postmarkWebClient")
    public WebClient postmarkWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.postmarkapp.com")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Postmark-Server-Token", postmarkApiToken)
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(2 * 1024 * 1024))
                .build();
    }

    @Bean(name = "webClientBuilder")
    public WebClient.Builder getWebClientBuilder() {
        return WebClient
                .builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(2 * 1024 * 1024));
    }

}
