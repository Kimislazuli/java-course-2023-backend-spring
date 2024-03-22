package edu.java.bot.client;

import edu.java.bot.exception.ApiResponseException;
import edu.java.bot.model.Chat;
import edu.java.models.dto.request.AddLinkRequest;
import edu.java.models.dto.request.RemoveLinkRequest;
import edu.java.models.dto.response.ApiErrorResponse;
import edu.java.models.dto.response.ChatResponse;
import edu.java.models.dto.response.LinkResponse;
import edu.java.models.dto.response.ListLinksResponse;
import java.net.URI;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
public class ScrapperClient {
    private final WebClient webClient;
    private final static String TG_CHAT_ID = "/tg-chat/{id}";
    private final static String TG_CHAT_ID_WITH_STATE = "/tg-chat/{id}/change_state";
    private final static String LINKS = "/links";
    private final static String TG_CHAT_ID_HEADER = "Tg-Chat-Id";

    public ScrapperClient(WebClient.Builder builder, String baseUrl) {
        webClient = builder.baseUrl(baseUrl).build();
    }

    public void registerChat(Long id) {
        webClient.post()
            .uri(builder -> builder.path(TG_CHAT_ID).build(id))
            .retrieve()
            .onStatus(
                HttpStatusCode::is4xxClientError,
                r -> r.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new ApiResponseException(
                        apiErrorResponse.exceptionMessage())))
            ).bodyToMono(String.class).block();
    }

    public void updateChatState(Long id, int state) {
        webClient.post()
            .uri(builder -> builder.path(TG_CHAT_ID_WITH_STATE).build(id))
            .header("State", String.valueOf(state))
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

    public Optional<Chat> getChat(Long id) {
        ChatResponse response = webClient.get()
                .uri(builder -> builder.path(TG_CHAT_ID).build(id))
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .block();
        return response.id() != -1 ? Optional.of(new Chat(response.id(), response.state())) : Optional.empty();
    }

    public ListLinksResponse getLinks(Long id) {
        return webClient.get()
            .uri(LINKS)
            .header(TG_CHAT_ID_HEADER, id.toString())
            .retrieve()
            .bodyToMono(ListLinksResponse.class)
            .block();
    }

    public Optional<LinkResponse> addLink(Long id, String link) {
        return webClient.post()
            .uri(LINKS)
            .header(TG_CHAT_ID_HEADER, id.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(new AddLinkRequest(URI.create(link))))
            .retrieve()
            .onStatus(
                HttpStatusCode::is4xxClientError,
                r -> r.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new ApiResponseException(
                        apiErrorResponse.exceptionMessage())))
            )
            .bodyToMono(LinkResponse.class)
            .blockOptional();
    }

    public LinkResponse deleteLink(Long id, String link) {
        return webClient.method(HttpMethod.DELETE)
            .uri(LINKS)
            .header(TG_CHAT_ID_HEADER, id.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(new RemoveLinkRequest(link)))
            .retrieve()
            .onStatus(
                HttpStatusCode::is4xxClientError,
                r -> r.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new ApiResponseException(
                        apiErrorResponse.exceptionMessage())))
            )
            .bodyToMono(LinkResponse.class)
            .block();
    }
}
