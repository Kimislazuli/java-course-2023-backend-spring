package edu.java.scrapper.service.processing_services.jooq;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.dao.jdbc.JdbcChatDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcChatToLinkConnectionDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.dao.jooq.JooqChatDao;
import edu.java.scrapper.domain.dao.jooq.JooqChatToLinkConnectionDao;
import edu.java.scrapper.domain.dao.jooq.JooqLinkDao;
import edu.java.scrapper.domain.jooq.Tables;
import edu.java.scrapper.domain.model.link.Link;
import edu.java.scrapper.exception.AlreadyExistException;
import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.exception.RepeatedRegistrationException;
import edu.java.scrapper.service.processing_services.LinkService;
import jakarta.transaction.Transactional;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@SpringBootTest(properties = {"app.database-access-type=jooq"})
@Transactional
public class JooqLinkServiceTest extends IntegrationTest {
    @Autowired
    LinkService linkService;
    @Autowired
    JooqLinkDao linkDao;
    @Autowired
    JooqChatDao chatDao;
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
    void registerSuccessfullyTest() throws RepeatedRegistrationException, NotExistException, AlreadyExistException {
        chatDao.createIfNotExist(1L);
        Link link = linkService.add(1L, URI.create("string"));

        assertThat(linkDao.getLinkById(link.getId())).isPresent();
        assertThat(connectionDao.findByComplexId(1L, link.getId())).isPresent();
    }

    @Test
    void registerIfChatNotExistTest() throws RepeatedRegistrationException, NotExistException, AlreadyExistException {
        Link link = linkService.add(1L, URI.create("string"));

        assertThat(linkDao.getLinkById(link.getId())).isPresent();
        assertThat(connectionDao.findByComplexId(1L, link.getId())).isPresent();
    }

    @Test
    void reregistrationTest() {
        chatDao.createIfNotExist(1L);

        assertThrows(AlreadyExistException.class, () -> {
            linkService.add(1L, URI.create("string"));
            linkService.add(1L, URI.create("string"));
        });
    }

    @Test
    void removeTest() throws RepeatedRegistrationException, NotExistException, AlreadyExistException {
        chatDao.createIfNotExist(1L);
        Link link = linkService.add(1L, URI.create("www.url.com"));

        linkService.remove(1L, URI.create("www.url.com"));

        assertThat(linkDao.getLinkById(link.getId())).isEmpty();
    }

    @Test
    void removeIfLinkNotExistTest() {
        chatDao.createIfNotExist(1L);

        assertThrows(NotExistException.class, () -> {
            linkService.remove(1L, URI.create("www.url.com"));
        });
    }

    @Test
    void removeIfConnectionNotExistTest() throws RepeatedRegistrationException, AlreadyExistException,
            NotExistException {
        chatDao.createIfNotExist(1L);
        chatDao.createIfNotExist(1L);
        linkService.add(2L, URI.create("www.url.com"));

        assertThrows(NotExistException.class, () -> {
            linkService.remove(1L, URI.create("www.url.com"));
        });
    }

    @Test
    void listTest() throws RepeatedRegistrationException, AlreadyExistException, NotExistException {
        chatDao.createIfNotExist(1L);
        linkService.add(1L, URI.create("1"));
        linkService.add(1L, URI.create("2"));
        Collection<Link> actualResult = linkService.listAll(1L);

        assertThat(actualResult.stream().map(Link::getUrl)).containsExactlyInAnyOrder("1", "2");
    }

    @Test
    void linkedChatsTest() throws RepeatedRegistrationException, AlreadyExistException, NotExistException {
        chatDao.createIfNotExist(1L);
        chatDao.createIfNotExist(2L);
        Link link = linkService.add(1L, URI.create("string"));
        linkService.add(2L, URI.create("string"));

        List<Long> actualResult = linkService.linkedChatIds(link.getId());
        assertThat(actualResult).containsExactlyInAnyOrder(1L, 2L);
    }
}
