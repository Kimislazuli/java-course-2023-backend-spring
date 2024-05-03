package edu.java.scrapper.client.bot;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.scrapper.client.BotClient;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

public class BotClientTest {
    static WireMockServer server = new WireMockServer(8093);
    private BotClient client =
        new BotClient(WebClient.builder(), server.baseUrl(), Retry.backoff(1, Duration.ofMinutes(5)));
    ;

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
        server.stubFor(post(urlEqualTo("/updates")).willReturn(aResponse().withStatus(200)));

        client.updates(1L, "url", "description", List.of(1L, 2L));
        String requestBody = server.getAllServeEvents().getFirst().getRequest().getBodyAsString();
        assertThat(requestBody).isEqualTo(
            "{\"id\":1,\"url\":\"url\",\"description\":\"description\",\"tgChatIds\":[1,2]}");
    }

}
