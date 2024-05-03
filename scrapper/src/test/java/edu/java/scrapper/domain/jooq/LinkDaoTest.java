package edu.java.scrapper.domain.jooq;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.dao.jooq.JooqChatDao;
import edu.java.scrapper.domain.dao.jooq.JooqChatToLinkConnectionDao;
import edu.java.scrapper.domain.dao.jooq.JooqLinkDao;
import edu.java.scrapper.domain.model.link.Link;
import edu.java.scrapper.exception.NotExistException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {"app.database-access-type=jooq"})
public class LinkDaoTest extends IntegrationTest {
    @Autowired
    private DSLContext dsl;
    @Autowired
    private JooqLinkDao linkDao;
    @Autowired
    private JooqChatDao chatDao;
    @Autowired
    private JooqChatToLinkConnectionDao connectionDao;

    @BeforeEach
    void setUp() {
        var connection = dsl.delete(Tables.CHAT_TO_LINK_CONNECTION).execute();
        var link = dsl.delete(Tables.LINK).execute();
        var chat = dsl.delete(Tables.CHAT).execute();
    }

    @Test
    @Transactional
    @Rollback
    void addSuccessfullyTest() {
        long linkId = linkDao.createIfNotExist("www.url.com", OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            0,
            ZoneOffset.UTC
        ), OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            0,
            ZoneOffset.UTC
        )).get();

        List<Link> actualResult = linkDao.findAll();

        assertThat(actualResult.size()).isEqualTo(1);

        assertThat(actualResult.getFirst()).isEqualTo(new Link(linkId, "www.url.com", OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            0,
            ZoneOffset.UTC
        ), OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            0,
            ZoneOffset.UTC
        )));
    }

    @Test
    @Transactional
    @Rollback
    void addExistedChatTest() {
        linkDao.createIfNotExist("www.url.com", OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            0,
            ZoneOffset.UTC
        ), OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            0,
            ZoneOffset.UTC
        ));
        Optional<Long> actualResult = linkDao.createIfNotExist("www.url.com", OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            0,
            ZoneOffset.UTC
        ), OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            0,
            ZoneOffset.UTC
        ));

        assertThat(actualResult).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void removeSuccessfullyTest() throws NotExistException {
        long linkId = linkDao.createIfNotExist("www.url.com", OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            0,
            ZoneOffset.UTC
        ), OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            0,
            ZoneOffset.UTC
        )).get();
        linkDao.remove(linkId);

        List<Link> actualResult = linkDao.findAll();

        assertThat(actualResult).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void removeNotExistedChatTest() {
        assertThrows(NotExistException.class, () -> {
            linkDao.remove(22L);
        });
    }

    @Test
    @Transactional
    @Rollback
    void findAllTest() {
        long firstId = linkDao.createIfNotExist("www.url.com", OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            0,
            ZoneOffset.UTC
        ), OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            0,
            ZoneOffset.UTC
        )).get();
        long secondId = linkDao.createIfNotExist("www.link.com", OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            0,
            ZoneOffset.UTC
        ), OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            0,
            ZoneOffset.UTC
        )).get();

        List<Link> actualResult = linkDao.findAll();

        assertThat(actualResult).containsExactlyInAnyOrder(
            new Link(firstId, "www.url.com", OffsetDateTime.of(2010,
                2,
                2,
                2,
                2,
                2,
                0,
                ZoneOffset.UTC
            ), OffsetDateTime.of(2010,
                2,
                2,
                2,
                2,
                2,
                0,
                ZoneOffset.UTC
            )),
            new Link(secondId, "www.link.com", OffsetDateTime.of(2010,
                2,
                2,
                2,
                2,
                2,
                0,
                ZoneOffset.UTC
            ), OffsetDateTime.of(2010,
                2,
                2,
                2,
                2,
                2,
                0,
                ZoneOffset.UTC
            ))
        );
    }

    @Test
    @Transactional
    @Rollback
    void getLinkByUrlTest() {
        linkDao.createIfNotExist("www.url.com", OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            0,
            ZoneOffset.UTC
        ), OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            0,
            ZoneOffset.UTC
        )).get();

        Optional<Link> actualResult = linkDao.getLinkByUrl("www.url.com");

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get().getUrl()).isEqualTo("www.url.com");
    }

    @Test
    @Transactional
    @Rollback
    void getLinkByIdTest() {
        long id = linkDao.createIfNotExist("www.url.com", OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            0,
            ZoneOffset.UTC
        ), OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            0,
            ZoneOffset.UTC
        )).get();

        Optional<Link> actualResult = linkDao.getLinkById(id);

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get().getId()).isEqualTo(id);
        assertThat(actualResult.get().getUrl()).isEqualTo("www.url.com");
    }

    @Test
    @Transactional
    @Rollback
    void findChatsByLinkIdTest() {
        long id1 = linkDao.createIfNotExist("www.url.com", OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            0,
            ZoneOffset.UTC
        ), OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            0,
            ZoneOffset.UTC
        )).get();
        long id2 = linkDao.createIfNotExist("www.test.com", OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            0,
            ZoneOffset.UTC
        ), OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            0,
            ZoneOffset.UTC
        )).get();

        chatDao.createIfNotExist(1L);

        connectionDao.createIfNotExist(1L, id1);
        connectionDao.createIfNotExist(1L, id2);
        List<Link> actualResult = linkDao.findAllLinksByChatId(1L);

        assertThat(actualResult.stream().map(Link::getId)).containsExactlyInAnyOrder(id1, id2);
    }
}
