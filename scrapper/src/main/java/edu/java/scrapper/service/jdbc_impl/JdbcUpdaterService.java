package edu.java.scrapper.service.jdbc_impl;

import edu.java.scrapper.domain.dao.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.model.link.Link;
import edu.java.scrapper.service.UpdaterService;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

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
