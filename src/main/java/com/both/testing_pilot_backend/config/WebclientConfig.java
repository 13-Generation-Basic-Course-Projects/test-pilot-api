package com.both.testing_pilot_backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebclientConfig {

    private final GithubConfig githubConfig;

    @Bean(name = "githubWebClient")
    public WebClient getGithubWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(githubConfig.getApi())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
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
