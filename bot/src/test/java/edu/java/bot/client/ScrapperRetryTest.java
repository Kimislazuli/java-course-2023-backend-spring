package edu.java.bot.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

@WireMockTest(httpPort = 8095)
@SpringBootTest(properties = {"app.retry-config.backoff-type=exponential",
    "app.retry-config.status-codes=500", "app.retry-config.attempts=2"})
public class ScrapperRetryTest {
    static WireMockServer server = new WireMockServer();
    private ScrapperClient client;

    @Autowired
    public ScrapperRetryTest(Retry retry) {
        this.client = new ScrapperClient(WebClient.builder(), "http://localhost:8095", retry);
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
    public void registerChatTest() {
        stubFor(post(urlEqualTo("/tg-chat/1"))
            .inScenario("RETRY")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(aResponse().withStatus(500))
            .willSetStateTo("good")
        );

        stubFor(post(urlEqualTo("/tg-chat/1"))
            .inScenario("RETRY")
            .whenScenarioStateIs("good")
            .willReturn(aResponse().withStatus(200).withBody(""))
        );

        client.registerChat(1L);
        verify(2, postRequestedFor(urlEqualTo("/tg-chat/1")));
    }

    @Test
    public void updateStateTest() {
        stubFor(post(urlEqualTo("/tg-chat/1/change_state"))
            .inScenario("RETRY")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(aResponse().withStatus(500))
            .willSetStateTo("good")
        );

        stubFor(post(urlEqualTo("/tg-chat/1/change_state"))
            .inScenario("RETRY")
            .whenScenarioStateIs("good")
            .willReturn(aResponse().withStatus(200).withBody(""))
        );

        client.updateChatState(1L, 2);
        verify(2, postRequestedFor(urlEqualTo("/tg-chat/1/change_state")));
    }

    @Test
    public void deleteChatTest() {
        stubFor(delete(urlEqualTo("/tg-chat/1"))
            .inScenario("RETRY")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(aResponse().withStatus(500))
            .willSetStateTo("good")
        );

        stubFor(delete(urlEqualTo("/tg-chat/1"))
            .inScenario("RETRY")
            .whenScenarioStateIs("good")
            .willReturn(aResponse().withStatus(200).withBody(""))
        );

        client.deleteChat(1L);
        verify(2, deleteRequestedFor(urlEqualTo("/tg-chat/1")));
    }

    @Test
    public void addLinkTest() {
        stubFor(post(urlEqualTo("/links"))
            .inScenario("RETRY")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(aResponse().withStatus(500))
            .willSetStateTo("good")
        );

        stubFor(post(urlEqualTo("/links"))
            .inScenario("RETRY")
            .whenScenarioStateIs("good")
            .willReturn(aResponse().withStatus(200).withBody(""))
        );

        client.addLink(1L, "link");
        verify(2, postRequestedFor(urlEqualTo("/links")));
    }
}
