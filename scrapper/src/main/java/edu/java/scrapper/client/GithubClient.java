package edu.java.scrapper.client;

import edu.java.scrapper.dto.github.GithubResponse;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
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

    public Optional<GithubResponse> fetchLastModificationTime(String user, String repository) {
        String link = String.format("/repos/%s/%s/events", user, repository);
        try {
            return webClient
                .get()
                .uri(url -> url.path(link).queryParam("per_page", 1).build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<GithubResponse>>() {
                })
                .retryWhen(retryBackoff)
                .blockOptional()
                .map(List::getFirst);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
