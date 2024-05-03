package edu.java.scrapper.domain.dao.abstract_dao;

import edu.java.scrapper.domain.model.link.Link;
import edu.java.scrapper.exception.NotExistException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface LinkDao {
    Optional<Long> createIfNotExist(String url, OffsetDateTime lastUpdate, OffsetDateTime lastCheck);

    void remove(long linkId) throws NotExistException;

    List<Link> findAll();

    Optional<Link> getLinkByUrl(String url);

    Optional<Link> getLinkById(long id);

    List<Link> findOldLinksToCheck(OffsetDateTime timestamp);

    void updateCheckTime(long linkId, OffsetDateTime timestamp);

    void updateUpdateTime(long linkId, OffsetDateTime timestamp);

    List<Link> findAllLinksByChatId(long chatId);
}
