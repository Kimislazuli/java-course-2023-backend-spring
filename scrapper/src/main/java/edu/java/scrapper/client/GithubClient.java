package edu.java.scrapper.client;

import edu.java.scrapper.dto.github.GithubActivityResponse;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

@Slf4j
public class GithubClient {
    private final WebClient webClient;
    private final Retry retryBackoff;

    public GithubClient(WebClient.Builder builder, String baseUrl, Retry retryBackoff) {
        this.retryBackoff = retryBackoff;
        webClient = builder.baseUrl(baseUrl).build();
    }

    public List<GithubActivityResponse> fetchActivity(String user, String repository, OffsetDateTime afterTime) {
        String link = String.format("/repos/%s/%s/activity", user, repository);
        try {
            return webClient
                    .get()
                    .uri(url -> url.path(link).build())
                    .retrieve()
                    .bodyToFlux(GithubActivityResponse.class)
                    .collectList()
                    .retryWhen(retryBackoff)
                    .block()
                    .stream()
                    .filter(s -> s.timestamp().isAfter(afterTime))
                    .toList();
        } catch (NullPointerException e) {
            return Collections.emptyList();
        }
    }
}
