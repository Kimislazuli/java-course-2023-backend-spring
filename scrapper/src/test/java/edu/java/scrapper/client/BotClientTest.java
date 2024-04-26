package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.getAllServeEvents;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@WireMockTest(httpPort = 8090)
@SpringBootTest(properties = {"app.database-access-type=jpa", "app.retry-config.backoff-type=exponential",
    "app.retry-config.status-codes=500",
    "app.retry-config.jitter=0.1", "app.retry-config.attempts=2", "app.retry-config.min-delay=200", "server.port=8082"})
public class BotClientTest {
    static WireMockServer server = new WireMockServer();
    private BotClient client;

    @Autowired
    public BotClientTest(WebClient.Builder builder, Retry retry) {
        this.client = new BotClient(builder, "http://localhost:8090", retry);
    }

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

        client.updates(1L, "url", "description", List.of(1L, 2L));
        String requestBody = getAllServeEvents().getFirst().getRequest().getBodyAsString();
        assertThat(requestBody).isEqualTo(
            "{\"id\":1,\"url\":\"url\",\"description\":\"description\",\"tgChatIds\":[1,2]}");
    }

    @Test
    public void retryTest() {
        stubFor(post(urlEqualTo("/updates"))
            .inScenario("RETRY")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(aResponse().withStatus(500))
            .willSetStateTo("good")
        );

        stubFor(post(urlEqualTo("/updates"))
            .inScenario("RETRY")
            .whenScenarioStateIs("good")
            .willReturn(aResponse().withStatus(200).withBody(""))
        );

        assertDoesNotThrow(() -> client.updates(
            1L,
            "www.link.com",
            "description",
            List.of(1L)
        ));
        verify(2, postRequestedFor(urlEqualTo("/updates")));
    }
}
