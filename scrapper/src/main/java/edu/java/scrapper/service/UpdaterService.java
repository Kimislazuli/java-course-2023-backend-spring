package edu.java.scrapper.service;

import edu.java.scrapper.domain.model.link.Link;
import java.time.OffsetDateTime;
import java.util.List;

public interface UpdaterService {
    void update(long id, OffsetDateTime timestamp);

    List<Link> findOldLinksToUpdate(OffsetDateTime timestamp);

    void check(long id, OffsetDateTime timestamp);
}
