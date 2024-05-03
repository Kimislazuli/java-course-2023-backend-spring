package edu.java.scrapper.service.processing_services.non_orm_impl;

import edu.java.scrapper.domain.dao.abstract_dao.LinkDao;
import edu.java.scrapper.domain.model.link.Link;
import edu.java.scrapper.service.processing_services.UpdaterService;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
public class NonOrmUpdaterService implements UpdaterService {
    private final LinkDao linkDao;

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
