package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import edu.java.scrapper.dto.github.GithubActivityResponse;
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
        stubFor(get(urlEqualTo("/repos/johndoe/java/activity")).willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("""
                [
                  {
                    "timestamp": "2024-01-03T01:57:09Z",
                    "activity_type": "pr_merge",
                    "actor": {
                          "login": "Kimislazuli"
                    }
                  }
                ]""")));

        GithubClient githubClient =
            new GithubClient(WebClient.builder(), "http://localhost:8080", Retry.backoff(2, Duration.ofHours(2)));

        List<GithubActivityResponse> actualResult =
            githubClient.fetchActivity("johndoe", "java", OffsetDateTime.parse("2024-01-01T01:57:09Z"));

        assertThat(actualResult).containsExactlyInAnyOrder(new GithubActivityResponse(
            "pr_merge",
            new GithubActivityResponse.Actor("Kimislazuli"),
            OffsetDateTime.parse("2024-01-03T01:57:09Z")
        ));
    }
}
