package edu.java.scrapper.client;

import edu.java.scrapper.dto.github.GithubResponse;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
public class GithubClient {
    private final WebClient webClient;

    public GithubClient(WebClient.Builder builder, String baseUrl) {
        webClient = builder.baseUrl(baseUrl).build();
    }

    public GithubResponse fetchLastModificationTime(String user, String repository) {
        String link = String.format("/repos/%s/%s/events", user, repository);
        Pattern pattern = Pattern.compile("\"updated_at\":\"(.+?)\"");
        Optional<String> response = webClient
            .get()
            .uri(url -> url.path(link).queryParam("per_page", 1).build())
            .retrieve()
            .bodyToMono(String.class).blockOptional();
        if (response.isPresent()) {
            Matcher matcher = pattern.matcher(response.get());
            if (matcher.find()) {
                return new GithubResponse(OffsetDateTime.parse(matcher.group(1)));
            }
        }
        return null;
    }
}
