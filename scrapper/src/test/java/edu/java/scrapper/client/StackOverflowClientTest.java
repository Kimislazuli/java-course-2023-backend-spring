package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.scrapper.client.StackOverflowClient;
import edu.java.scrapper.dto.stackoverflow.StackOverflowResponse;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

public class StackOverflowClientTest {
    private static final WireMockServer server = new WireMockServer();

    @BeforeAll
    public static void setUp() {
        server.start();
    }

    @AfterAll
    public static void tearDown() {
        server.stop();
    }

    @Test
    public void correctlyReceiveLastModificationTime() {
        server.stubFor(get(urlEqualTo("/questions/7?site=stackoverflow")).willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("{\"items\": [{\"last_activity_date\": \"1704247028\"}]}")));

        StackOverflowClient stackOverflowClient = new StackOverflowClient(WebClient.builder(), "http://localhost:8080");

        StackOverflowResponse
            actualResult = Objects.requireNonNull(stackOverflowClient.fetchLastModificationTime(7));

        assertThat(actualResult).isEqualTo(new StackOverflowResponse(OffsetDateTime.parse("2024-01-03T01:57:08Z")));
    }
}
