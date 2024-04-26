package edu.java.scrapper.service.jdbc;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.dao.jdbc.JdbcChatDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcChatToLinkConnectionDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.model.link.Link;
import edu.java.scrapper.exception.AlreadyExistException;
import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.exception.RepeatedRegistrationException;
import edu.java.scrapper.service.LinkService;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@SpringBootTest(properties = {"app.database-access-type=jdbc", "app.retry-config.backoff-type=linear", "app.retry-config.status-codes=500, 501",
    "app.retry-config.jitter=0.1", "app.retry-config.attempts=2", "app.retry-config.min-delay=200"})
@Transactional
public class JdbcLinkServiceTest extends IntegrationTest {
    @Autowired
    LinkService linkService;
    @Autowired
    JdbcLinkDao linkDao;
    @Autowired
    JdbcChatDao chatDao;
    @Autowired
    JdbcChatToLinkConnectionDao connectionDao;

    @BeforeEach
    void setUp() {
        try (Connection connection = POSTGRES.createConnection("");
             PreparedStatement sqlQueryChat = connection.prepareStatement("DELETE FROM public.chat");
             PreparedStatement sqlQueryLink = connection.prepareStatement("DELETE FROM public.link");
             PreparedStatement sqlQueryConnection = connection.prepareStatement("DELETE FROM public.chat_to_link_connection");
        ) {
            sqlQueryConnection.execute();
            sqlQueryChat.execute();
            sqlQueryLink.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
