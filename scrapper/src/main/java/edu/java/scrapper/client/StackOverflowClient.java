package edu.java.scrapper.client;

import edu.java.scrapper.dto.stackoverflow.StackOverflowResponse;
import org.springframework.web.reactive.function.client.WebClient;

public class StackOverflowClient {
    private final WebClient webClient;

    public StackOverflowClient(WebClient.Builder builder, String baseUrl) {
        webClient = builder.baseUrl(baseUrl).build();
    }

    public StackOverflowResponse fetchLastModificationTime(int id) {
        String link = String.format("/questions/%d", id);
        return webClient
            .get()
            .uri(url -> url.path(link)
                .queryParam("site", "stackoverflow")
                .build())
            .retrieve()
            .bodyToMono(StackOverflowResponse.class)
            .block();
    }
}
