package edu.java.scrapper.client.bot;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.client.BotClient;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(properties = {"app.retry-config.backoff-type=exponential",
    "app.retry-config.status-codes=500", "app.retry-config.attempts=2"})
public class BotRetryTest extends IntegrationTest {
    static WireMockServer server = new WireMockServer(8094);
    private BotClient client;

    @Autowired
    public BotRetryTest(Retry retry) {
        this.client = new BotClient(WebClient.builder(), server.baseUrl(), retry);
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
    public void retryTest() {
        server.stubFor(post(urlEqualTo("/updates"))
            .inScenario("RETRY")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(aResponse().withStatus(500))
            .willSetStateTo("good")
        );

        server.stubFor(post(urlEqualTo("/updates"))
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
        server.verify(2, postRequestedFor(urlEqualTo("/updates")));
    }
}
