package edu.java.scrapper.service.processing_services.jooq;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.dao.jdbc.JdbcChatToLinkConnectionDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.dao.jooq.JooqChatToLinkConnectionDao;
import edu.java.scrapper.domain.dao.jooq.JooqLinkDao;
import edu.java.scrapper.domain.jooq.Tables;
import edu.java.scrapper.domain.model.link.Link;
import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.service.processing_services.UpdaterService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"app.database-access-type=jooq"})
public class JooqUpdaterServiceTest extends IntegrationTest {
    @Autowired
    UpdaterService updaterService;
    @Autowired
    JooqLinkDao linkDao;
    @Autowired
    JooqChatToLinkConnectionDao connectionDao;
    @Autowired
    private DSLContext dsl;

    @BeforeEach
    void setUp() {
        var connection = dsl.delete(Tables.CHAT_TO_LINK_CONNECTION).execute();
        var link = dsl.delete(Tables.LINK).execute();
        var chat = dsl.delete(Tables.CHAT).execute();
    }

    @Test
    void checkTest() throws NotExistException {
        linkDao.createIfNotExist(
                "www.url.com",
                OffsetDateTime.parse("2020-05-20T05:40:08.721Z"),
                OffsetDateTime.parse("2020-05-20T05:40:08.721Z")
        );

        Optional<Link> link = linkDao.getLinkByUrl("www.url.com");
        assertThat(link).isPresent();
        OffsetDateTime previous = link.get().getLastCheck();

        updaterService.check(link.get().getId(), OffsetDateTime.parse("2020-06-20T05:40:08.721Z"));

        Optional<Link> newLink = linkDao.getLinkByUrl("www.url.com");
        assertThat(newLink).isPresent();
        OffsetDateTime current = newLink.get().getLastCheck();

        assertThat(current).isAfter(previous);
    }

    @Test
    void updateTest() throws NotExistException {
        linkDao.createIfNotExist(
                "www.url.com",
                OffsetDateTime.parse("2020-05-20T05:40:08.721Z"),
                OffsetDateTime.parse("2020-05-20T05:40:08.721Z")
        );

        Optional<Link> link = linkDao.getLinkByUrl("www.url.com");
        assertThat(link).isPresent();
        OffsetDateTime previous = link.get().getLastUpdate();

        updaterService.update(link.get().getId(), OffsetDateTime.parse("2020-06-20T05:40:08.721Z"));

        Optional<Link> newLink = linkDao.getLinkByUrl("www.url.com");
        assertThat(newLink).isPresent();
        OffsetDateTime current = newLink.get().getLastUpdate();

        assertThat(current).isAfter(previous);
    }
}
