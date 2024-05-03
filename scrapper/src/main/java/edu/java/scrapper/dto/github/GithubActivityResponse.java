package edu.java.scrapper.dto.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record GithubActivityResponse(
    @JsonProperty("activity_type")
    String activityType,
    Actor actor,
    @JsonProperty("timestamp")
    OffsetDateTime timestamp
) {
    public record Actor(
        @JsonProperty("login")
        String login
    ) {}
}
