package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

public class BotClientTest {
    static WireMockServer server = new WireMockServer();
    BotClient client = new BotClient(WebClient.builder(), "http://localhost:8080");

    @BeforeAll
    static void setUp() {
        server.start();
    }

    @AfterAll
    static void tearDown() {
        server.stop();
    }

    @Test
    void updatesTest() {
        stubFor(post(urlEqualTo("/updates")).willReturn(aResponse().withStatus(200)));

        client.updates(1L, "url","description", List.of(1L, 2L));
        String requestBody = server.getAllServeEvents().getFirst().getRequest().getBodyAsString();
        assertThat(requestBody).isEqualTo("{\"id\":1,\"url\":\"url\",\"description\":\"description\",\"tgChatIds\":[1,2]}");
    }
}
