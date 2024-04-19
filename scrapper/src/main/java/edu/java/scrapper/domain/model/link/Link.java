package edu.java.scrapper.domain.model.link;

import java.time.OffsetDateTime;

public record Link(long id, String url, OffsetDateTime lastUpdate, OffsetDateTime lastCheck) {
}
