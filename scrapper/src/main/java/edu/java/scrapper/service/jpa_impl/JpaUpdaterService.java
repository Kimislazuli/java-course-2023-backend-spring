package edu.java.scrapper.service.jpa_impl;

import edu.java.scrapper.domain.dao.jpa.JpaLinkDao;
import edu.java.scrapper.domain.model.link.Link;
import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.service.UpdaterService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JpaUpdaterService implements UpdaterService {
    private final JpaLinkDao repository;
    private static final String LINK_NOT_EXIST = "This link doesn't exist.";

    @Override
    @Transactional
    public void update(long id, OffsetDateTime timestamp) throws NotExistException {
        Optional<Link> link = repository.findById(id);
        if (link.isPresent()) {
            link.get().setLastUpdate(timestamp);
        } else {
            throw new NotExistException(LINK_NOT_EXIST);
        }
    }

    @Override
    public List<Link> findOldLinksToUpdate(OffsetDateTime timestamp) {
        return repository.findByLastCheckTime(timestamp);
    }

    @Override
    @Transactional
    public void check(long id, OffsetDateTime timestamp) throws NotExistException {
        Optional<Link> link = repository.findById(id);
        if (link.isPresent()) {
            link.get().setLastCheck(timestamp);
        } else {
            throw new NotExistException(LINK_NOT_EXIST);
        }
    }
}
