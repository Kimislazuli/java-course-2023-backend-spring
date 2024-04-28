package edu.java.bot.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.time.Duration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

public class ScrapperClientTest {
    static WireMockServer server = new WireMockServer();
    ScrapperClient client =
        new ScrapperClient(WebClient.builder(), "http://localhost:8080", Retry.backoff(2, Duration.ofMinutes(2)));

    @BeforeAll
    static void setUp() {
        server.start();
    }

    @AfterAll
    static void tearDown() {
        server.stop();
    }

    @Test
    void registerChatTest() {
        server.stubFor(post(urlEqualTo("/tg-chat/1")).willReturn(aResponse().withStatus(200)));

        client.registerChat(1L);
        int actualResult = server.getAllServeEvents().getFirst().getResponse().getStatus();
        assertThat(actualResult).isEqualTo(200);
    }

    @Test
    void deleteChatTest() {
        server.stubFor(delete(urlEqualTo("/tg-chat/1")).willReturn(aResponse().withStatus(200)));

        client.deleteChat(1L);
        int actualResult = server.getAllServeEvents().getFirst().getResponse().getStatus();
        assertThat(actualResult).isEqualTo(200);
    }

    @Test
    void getLinksTest() {
        server.stubFor(get(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", WireMock.equalTo("1"))
            .willReturn(aResponse().withStatus(200)));

        client.getLinks(1L);
        int actualStatusCode = server.getAllServeEvents().getFirst().getResponse().getStatus();
        boolean containsHeader =
            server.getAllServeEvents().getFirst().getRequest().getAllHeaderKeys().contains("Tg-Chat-Id");

        assertThat(actualStatusCode).isEqualTo(200);
        Assertions.assertTrue(containsHeader);
    }

    @Test
    void addLinkRequestTest() {
        server.stubFor(post(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", WireMock.equalTo("1"))
            .willReturn(aResponse().withStatus(200)));

        client.addLink(1L, "link");

        var request = server.getAllServeEvents().getFirst().getRequest();
        int actualStatusCode = server.getAllServeEvents().getFirst().getResponse().getStatus();
        String requestBody = request.getBodyAsString();
        boolean containsHeader = request.getAllHeaderKeys().contains("Tg-Chat-Id");

        assertThat(actualStatusCode).isEqualTo(200);
        Assertions.assertTrue(containsHeader);
        assertThat(requestBody).isEqualTo("{\"link\":\"link\"}");
    }

    @Test
    void deleteLinkRequestTest() {
        server.stubFor(delete(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", WireMock.equalTo("1"))
            .willReturn(aResponse().withStatus(200)));

        client.deleteLink(1L, "link");

        var request = server.getAllServeEvents().getFirst().getRequest();
        int actualStatusCode = server.getAllServeEvents().getFirst().getResponse().getStatus();
        String requestBody = request.getBodyAsString();
        boolean containsHeader = request.getAllHeaderKeys().contains("Tg-Chat-Id");

        assertThat(actualStatusCode).isEqualTo(200);
        Assertions.assertTrue(containsHeader);
        assertThat(requestBody).isEqualTo("{\"link\":\"link\"}");
    }
}
