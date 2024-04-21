package edu.java.scrapper.service.processing_services.jdbc_impl;

import edu.java.scrapper.domain.dao.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.model.link.Link;
import edu.java.scrapper.service.processing_services.UpdaterService;
import java.time.OffsetDateTime;
import java.util.List;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
public class JdbcUpdaterService implements UpdaterService {
    private final JdbcLinkDao linkDao;

    @Override
    public void update(long id, OffsetDateTime timestamp) {
        linkDao.updateUpdateTime(id, timestamp);
    }

    @Override
    public void check(long id, OffsetDateTime timestamp) {
        linkDao.updateCheckTime(id, timestamp);
    }

    @Override
    public List<Link> findOldLinksToUpdate(OffsetDateTime timestamp) {
        return linkDao.findOldLinksToCheck(timestamp);
    }
}
