package edu.java.scrapper.dto.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record GithubResponse(
    @JsonProperty("created_at")
    OffsetDateTime lastModified
) {
}
