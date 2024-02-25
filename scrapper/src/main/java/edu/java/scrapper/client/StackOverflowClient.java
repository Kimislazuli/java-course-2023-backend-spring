package edu.java.scrapper.client;

import edu.java.scrapper.dto.stackoverflow.StackOverflowResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class StackOverflowClient {
    private final WebClient webClient;

    public StackOverflowClient() {
        this("https://api.stackexchange.com/2.3/");
    }

    public StackOverflowClient(String baseUrl) {
        webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public Mono<ResponseEntity<StackOverflowResponse>> fetchLastModificationTime(int id) {
        String link = String.format("/questions/%d", id);
        return webClient
            .get()
            .uri(url -> url.path(link)
                .queryParam("site", "stackoverflow")
                .build())
            .retrieve()
            .toEntity(StackOverflowResponse.class);
    }
}
