package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.config.GithubConfig;
import com.both.testing_pilot_backend.exceptions.GithubApiException;
import com.both.testing_pilot_backend.exceptions.GithubOauthException;
import com.both.testing_pilot_backend.dto.response.GithubUserEmail;
import com.both.testing_pilot_backend.dto.response.GithubUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class GithubService {

    private final WebClient gitHubWebClient;
    private final WebClient.Builder webClientBuilder;

    private final GithubConfig githubConfig;


    public GithubService(
            @Qualifier("githubWebClient") WebClient gitHubWebClient,
            @Qualifier("webClientBuilder") WebClient.Builder webClientBuilder, GithubConfig githubConfig) {
        this.gitHubWebClient = gitHubWebClient;
        this.webClientBuilder = webClientBuilder;
        this.githubConfig = githubConfig;
    }


    private Mono<String> getAccessToken(String code) {
        Map<String, String> bodyValue = new HashMap<>();
        bodyValue.put("client_id", githubConfig.getClientId());
        bodyValue.put("client_secret", githubConfig.getClientSecret());
        bodyValue.put("code", code);

        System.out.println("Gittiitititititi hubL::::P: " + githubConfig.toString() + " github clickent " + gitHubWebClient.toString());
        return gitHubWebClient
                .post()
                .uri("/login/oauth/access_token")
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(bodyValue)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new GithubOauthException("Error getting access token: " + errorBody)))
                )
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                .flatMap(response -> {
                    String token = response.get("access_token");
                    if (token == null) {
                        return Mono.error(new GithubOauthException("Access token not found in response: " + response));
                    }
                    return Mono.just(token);
                });
    }

    private Mono<GithubUserResponse> getGithubUserDetail(String accessToken) {
        return webClientBuilder
                .build()
                .get()
                .uri(githubConfig.getUserApi())
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error( new GithubApiException("Client error while fetching GitHub user: " + errorBody)))
                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error( new GithubApiException("Server error while fetching GitHub user: " + errorBody)))
                )
                .bodyToMono(GithubUserResponse.class);
    }

    private Mono<GithubUserResponse> getUserInfo(String accessToken) {
        Mono<GithubUserResponse> githubUserResponseMono = getGithubUserDetail(accessToken);
        Flux<GithubUserEmail> githubUserEmail = getGithubUserEmail(accessToken);

        return Mono.zip(githubUserResponseMono, githubUserEmail.collectList())
                .map(tuple -> {
                    GithubUserResponse userDetail = tuple.getT1();
                    List<GithubUserEmail> userEmails = tuple.getT2();

                    userEmails.stream()
                            .filter(userEmail -> userEmail.isPrimary())
                            .findFirst()
                            .ifPresent(userEmail -> userDetail.setEmail(userEmail.getEmail()));

                    System.out.println("User detail of github + " + userDetail.toString());
                    return userDetail;
                });

    }

    private Flux<GithubUserEmail> getGithubUserEmail(String accessToken) {
        return webClientBuilder.build()
                .get()
                .uri(githubConfig.getUserApi() + "/emails")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .headers(httpHeaders ->
                        httpHeaders.setAccept(Collections.singletonList(MediaType.valueOf("application/vnd.github+json")))
                )
                .retrieve()
                .bodyToFlux(GithubUserEmail.class);
    }

    public Mono<GithubUserResponse>  getUserByAccessToken(String code) {
        return getAccessToken(code)
                .flatMap(accessToken -> getUserInfo(accessToken));
    }
}
