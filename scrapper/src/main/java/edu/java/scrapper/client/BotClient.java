package edu.java.scrapper.client;

import edu.java.models.dto.LinkUpdate;
import edu.java.models.dto.response.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
public class BotClient {
    private final WebClient webClient;
    private final static String UPDATES = "/updates";
    private final Retry retryBackoff;

    public BotClient(WebClient.Builder builder, String baseUrl, Retry retry) {
        webClient = builder.baseUrl(baseUrl).build();
        this.retryBackoff = retry;
    }

    public void updates(LinkUpdate update) {
        webClient.post()
            .uri(UPDATES)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(update)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(ApiErrorResponse.class)
                .flatMap(error -> {
                    log.error("Error: " + error.description() + " " + error.exceptionMessage());
                    return Mono.error(new RuntimeException(error.code() + " " + error.exceptionMessage()));
                }))
            .bodyToMono(String.class)
            .retryWhen(retryBackoff)
            .blockOptional();
    }
}
