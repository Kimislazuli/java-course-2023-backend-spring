package edu.java.scrapper.client;

import edu.java.scrapper.dto.github.GithubResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GithubClient {
    private final WebClient webClient;

    public GithubClient() {
        this("https://api.github.com");
    }

    public GithubClient(String baseUrl) {
        webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public Mono<ResponseEntity<GithubResponse>> fetchLastModificationTime(String user, String repository) {
        String link = String.format("/repos/%s/%s", user, repository);
        return webClient
            .get()
            .uri(link)
            .retrieve()
            .toEntity(GithubResponse.class);
    }
}
