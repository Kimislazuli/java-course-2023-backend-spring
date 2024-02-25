package edu.java.dto.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record GithubResponse(
    @JsonProperty("updated_at")
    OffsetDateTime lastModified
) {
}
