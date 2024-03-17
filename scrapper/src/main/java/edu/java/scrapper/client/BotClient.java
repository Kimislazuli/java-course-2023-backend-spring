package edu.java.scrapper.client;

import edu.java.models.dto.LinkUpdate;
import edu.java.models.dto.response.ApiErrorResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j

public class BotClient {
    private final WebClient webClient;
    private final static String UPDATES = "/updates";

    public BotClient(WebClient.Builder builder, String baseUrl) {
        webClient = builder.baseUrl(baseUrl).build();
    }

    public void sendUpdates(Long id, String url, String description, List<Long> tgChatIds) {
        webClient.post()
            .uri(UPDATES)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new LinkUpdate(id, url, description, tgChatIds))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(ApiErrorResponse.class)
                .flatMap(error -> {
                    log.error("Error: " + error.description() + " " + error.exceptionMessage());
                    return Mono.error(new RuntimeException(error.code() + " " + error.exceptionMessage()));
                }))
            .bodyToMono(String.class)
            .blockOptional();
    }

}
