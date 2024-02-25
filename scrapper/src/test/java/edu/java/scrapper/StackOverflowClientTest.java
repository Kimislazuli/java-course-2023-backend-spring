package edu.java.scrapper;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.client.StackOverflowClient;
import edu.java.dto.stackoverflow.StackOverflowResponse;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

public class StackOverflowClientTest {
    private static WireMockServer server = new WireMockServer();

    @BeforeAll
    public static void setUp() {
        server.start();
        server.stubFor(get(urlEqualTo("/questions/7?site=stackoverflow")).willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("{\"items\": [{\"last_activity_date\": \"1704247028\"}]}")));
    }

    @AfterAll
    public static void tearDown() {
        server.stop();
    }

    @Test
    public void correctlyReceiveLastModificationTime() {
        StackOverflowClient stackOverflowClient = new StackOverflowClient("http://localhost:8080");

        StackOverflowResponse
            actualResult = Objects.requireNonNull(stackOverflowClient.fetchLastModificationTime(7).block()).getBody();

        assertThat(actualResult).isEqualTo(new StackOverflowResponse(OffsetDateTime.parse("2024-01-03T01:57:08Z")));
    }
}
