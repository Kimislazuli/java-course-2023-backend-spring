package edu.java.scrapper.domain.jdbc;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.dao.jdbc.JdbcChatDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcChatToLinkConnectionDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.model.link.Link;
import edu.java.scrapper.exception.NotExistException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class LinkDaoTest extends IntegrationTest {
    @Autowired
    private JdbcLinkDao linkDao;

    @Autowired
    private JdbcChatDao chatDao;

    @Autowired
    private JdbcChatToLinkConnectionDao connectionDao;

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
    @Transactional
    @Rollback
    void addSuccessfullyTest() {
        long id = linkDao.createIfNotExist("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN).get();

        List<Link> actualResult = linkDao.findAll();

        assertThat(actualResult).containsExactly(new Link(id, "www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN));
    }

    @Test
    @Transactional
    @Rollback
    void addExistedChatTest() {
        linkDao.createIfNotExist("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN);
        Optional<Long> actualResult = linkDao.createIfNotExist("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN);

        assertThat(actualResult).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void removeSuccessfullyTest() throws NotExistException {
        long id = linkDao.createIfNotExist("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN).get();
        linkDao.remove(id);

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
        long firstId = linkDao.createIfNotExist("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN).get();
        long secondId = linkDao.createIfNotExist("www.link.com", OffsetDateTime.MAX, OffsetDateTime.MAX).get();

        List<Link> actualResult = linkDao.findAll();

        assertThat(actualResult).containsExactlyInAnyOrder(
            new Link(firstId, "www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN),
            new Link(secondId, "www.link.com", OffsetDateTime.MAX, OffsetDateTime.MAX)
        );
    }


    @Test
    @Transactional
    @Rollback
    void getLinkByUrlTest() {
        linkDao.createIfNotExist("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN).get();

        Optional<Link> actualResult = linkDao.getLinkByUrl("www.url.com");

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get().url()).isEqualTo("www.url.com");
    }

    @Test
    @Transactional
    @Rollback
    void getLinkByIdTest() {
        long id = linkDao.createIfNotExist("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN).get();

        Optional<Link> actualResult = linkDao.getLinkById(id);

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get().id()).isEqualTo(id);
        assertThat(actualResult.get().url()).isEqualTo("www.url.com");
    }

    @Test
    @Transactional
    @Rollback
    void findChatsByLinkIdTest() {
        long id1 = linkDao.createIfNotExist("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN).get();
        long id2 = linkDao.createIfNotExist("www.test.com", OffsetDateTime.MIN, OffsetDateTime.MIN).get();

        chatDao.createIfNotExist(1L);

        connectionDao.createIfNotExist(1L, id1);
        connectionDao.createIfNotExist(1L, id2);
        List<Link> actualResult = linkDao.findAllLinksByChatId(1L);

        assertThat(actualResult.stream().map(Link::id)).containsExactlyInAnyOrder(id1, id2);
    }
}
