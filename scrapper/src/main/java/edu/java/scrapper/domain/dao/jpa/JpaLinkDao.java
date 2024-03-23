package edu.java.scrapper.domain.dao.jpa;

import edu.java.scrapper.domain.model.link.Link;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaLinkDao extends JpaRepository<Link, Long> {
    Optional<Link> findByUrl(String string);

    @Query("FROM Link link WHERE link.lastCheck <= :time")
    List<Link> findByLastCheckTime(OffsetDateTime time);
}
