package edu.java.scrapper.dto.github;

import java.time.OffsetDateTime;

public record GithubResponse(
    OffsetDateTime lastModified
) {
}
