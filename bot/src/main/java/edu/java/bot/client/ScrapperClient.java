package edu.java.bot.client;

import edu.java.models.dto.request.AddLinkRequest;
import edu.java.models.dto.request.RemoveLinkRequest;
import edu.java.models.dto.response.LinkResponse;
import edu.java.models.dto.response.ListLinksResponse;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
public class ScrapperClient {
    private final WebClient webClient;
    private final static String TG_CHAT_ID = "/tg-chat/{id}";
    private final static String LINKS = "/links";
    private final static String TG_CHAT_ID_HEADER = "Tg-Chat-Id";

    public ScrapperClient(WebClient.Builder builder, String baseUrl) {
        webClient = builder.baseUrl(baseUrl).build();
    }

    public void registerChat(Long id) {
        webClient.post()
            .uri(builder -> builder.path(TG_CHAT_ID).build(id))
            .retrieve()
            .bodyToMono(String.class)
            .blockOptional();
    }

    public void deleteChat(Long id) {
        webClient.delete()
            .uri(builder -> builder.path(TG_CHAT_ID).build(id))
            .retrieve()
            .bodyToMono(String.class)
            .blockOptional();
    }

    public Optional<ListLinksResponse> getLinks(Long id) {
        return webClient.get()
            .uri(LINKS)
            .header(TG_CHAT_ID_HEADER, id.toString())
            .retrieve()
            .bodyToMono(ListLinksResponse.class)
            .blockOptional();
    }

    public Optional<LinkResponse> addLink(Long id, String link) {
        return webClient.post()
            .uri(LINKS)
            .header(TG_CHAT_ID_HEADER, id.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(new AddLinkRequest(link)))
            .retrieve()
            .bodyToMono(LinkResponse.class)
            .blockOptional();
    }

    public Optional<LinkResponse> deleteLink(Long id, String link) {
        return webClient.method(HttpMethod.DELETE)
            .uri(LINKS)
            .header(TG_CHAT_ID_HEADER, id.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(new RemoveLinkRequest(link)))
            .retrieve()
            .bodyToMono(LinkResponse.class)
            .blockOptional();
    }
}
