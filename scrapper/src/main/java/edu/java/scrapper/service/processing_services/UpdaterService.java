package edu.java.scrapper.service.processing_services;

import edu.java.scrapper.domain.model.link.Link;
import edu.java.scrapper.exception.NotExistException;
import java.time.OffsetDateTime;
import java.util.List;

public interface UpdaterService {
    void update(long id, OffsetDateTime timestamp) throws NotExistException;

    List<Link> findOldLinksToUpdate(OffsetDateTime timestamp);

    void check(long id, OffsetDateTime timestamp) throws NotExistException;
}
