package edu.java.scrapper.service.processing_services.jpa;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.dao.jpa.JpaChatToLinkConnectionDao;
import edu.java.scrapper.domain.dao.jpa.JpaLinkDao;
import edu.java.scrapper.domain.model.link.Link;
import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.service.processing_services.UpdaterService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"app.database-access-type=jpa"})
public class JpaUpdaterServiceTest extends IntegrationTest {
    @Autowired
    UpdaterService updaterService;
    @Autowired
    JpaLinkDao linkDao;
    @Autowired
    JpaChatToLinkConnectionDao connectionDao;

    @BeforeEach
    void setUp() {
        connectionDao.deleteAll();
        linkDao.deleteAll();
    }

    @Test
    void checkTest() throws NotExistException {
        linkDao.saveAndFlush(new Link(
            5L,
            "link",
            OffsetDateTime.parse("2020-05-20T05:40:08.721Z"),
            OffsetDateTime.parse("2020-05-20T05:40:08.721Z")
        ));

        Optional<Link> link = linkDao.findByUrl("link");
        assertThat(link).isPresent();
        OffsetDateTime previous = link.get().getLastCheck();

        updaterService.check(link.get().getId(), OffsetDateTime.parse("2020-06-20T05:40:08.721Z"));

        Optional<Link> newLink = linkDao.findByUrl("link");
        assertThat(newLink).isPresent();
        OffsetDateTime current = newLink.get().getLastCheck();

        assertThat(current).isAfter(previous);
    }

    @Test
    void updateTest() throws NotExistException {
        linkDao.saveAndFlush(new Link(
            5L,
            "link",
            OffsetDateTime.parse("2020-05-20T05:40:08.721Z"),
            OffsetDateTime.parse("2020-05-20T05:40:08.721Z")
        ));

        Optional<Link> link = linkDao.findByUrl("link");
        assertThat(link).isPresent();
        OffsetDateTime previous = link.get().getLastUpdate();

        updaterService.update(link.get().getId(), OffsetDateTime.parse("2020-06-20T05:40:08.721Z"));

        Optional<Link> newLink = linkDao.findByUrl("link");
        assertThat(newLink).isPresent();
        OffsetDateTime current = newLink.get().getLastUpdate();

        assertThat(current).isAfter(previous);
    }

    @Test
    void findOldTest() {
        linkDao.saveAndFlush(new Link(
            5L,
            "link",
            OffsetDateTime.parse("2020-05-20T05:40:08.721Z"),
            OffsetDateTime.parse("2020-05-20T05:40:08.721Z")
        ));

        linkDao.saveAndFlush(new Link(
            6L,
            "string",
            OffsetDateTime.parse("2020-05-20T05:40:08.721Z"),
            OffsetDateTime.parse("2020-07-20T05:40:08.721Z")
        ));

        List<Link> oldLinks = updaterService.findOldLinksToUpdate(OffsetDateTime.parse("2020-06-20T05:40:08.721Z"));

        assertThat(oldLinks.stream().map(Link::getUrl)).containsExactlyInAnyOrder("link");
    }
}
