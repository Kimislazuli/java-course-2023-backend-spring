package edu.java.scrapper.client;

import edu.java.models.dto.LinkUpdate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
public class BotClient {
    private final WebClient webClient;
    private final static String UPDATES = "/updates";

    public BotClient(WebClient.Builder builder, String baseUrl) {
        webClient = builder.baseUrl(baseUrl).build();
    }

    public void updates(Long linkId, String url, String description, List<Long> tgChatIds) {
        webClient.post()
            .uri(UPDATES)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(new LinkUpdate(linkId, url, description, tgChatIds)))
            .retrieve()
            .bodyToMono(String.class)
            .blockOptional();
    }
}
