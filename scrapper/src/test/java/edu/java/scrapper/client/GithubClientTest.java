package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.scrapper.dto.github.GithubResponse;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

public class GithubClientTest {
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
        stubFor(get(urlEqualTo("/repos/johndoe/java/events?per_page=1")).willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("""
                [
                  {
                    "public": true,
                    "created_at": "2024-01-03T01:57:09Z"
                  }
                ]""")));

        GithubClient githubClient =
            new GithubClient(WebClient.builder(), "http://localhost:8080", Retry.backoff(2, Duration.ofSeconds(2)));

        Optional<GithubResponse> actualResult = githubClient.fetchLastModificationTime("johndoe", "java");

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(new GithubResponse(OffsetDateTime.parse("2024-01-03T01:57:09Z")));
    }
}
