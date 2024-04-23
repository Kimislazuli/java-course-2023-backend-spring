package edu.java.scrapper.service.jpa;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.dao.jpa.JpaChatDao;
import edu.java.scrapper.domain.dao.jpa.JpaChatToLinkConnectionDao;
import edu.java.scrapper.domain.dao.jpa.JpaLinkDao;
import edu.java.scrapper.domain.model.chat.Chat;
import edu.java.scrapper.domain.model.connection.ConnectionPK;
import edu.java.scrapper.domain.model.link.Link;
import edu.java.scrapper.exception.AlreadyExistException;
import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.exception.RepeatedRegistrationException;
import edu.java.scrapper.service.LinkService;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@SpringBootTest(properties = {"app.database-access-type=jpa"})
public class JpaLinkServiceTest extends IntegrationTest {
    @Autowired
    LinkService linkService;
    @Autowired
    JpaLinkDao linkDao;
    @Autowired
    JpaChatDao chatDao;
    @Autowired
    JpaChatToLinkConnectionDao connectionDao;

    @BeforeEach
    void setUp() {
        connectionDao.deleteAll();
        linkDao.deleteAll();
        chatDao.deleteAll();
    }

    @Test
    void registerSuccessfullyTest() throws RepeatedRegistrationException, NotExistException, AlreadyExistException {
        chatDao.saveAndFlush(new Chat(1L));
        Link link = linkService.add(1L, URI.create("string"));

        assertThat(linkDao.findById(link.getId())).isPresent();
        assertThat(connectionDao.findById(new ConnectionPK(1L, link.getId()))).isPresent();
    }

    @Test
    void reregistrationTest() {
        chatDao.saveAndFlush(new Chat(1L));

        assertThrows(AlreadyExistException.class, () -> {
            linkService.add(1L, URI.create("string"));
            linkService.add(1L, URI.create("string"));
        });
    }

    @Test
    void removeTest() throws RepeatedRegistrationException, NotExistException, AlreadyExistException {
        chatDao.saveAndFlush(new Chat(1L));
        Link link = linkService.add(1L, URI.create("string"));

        linkService.remove(1L, URI.create("string"));
        assertThat(linkDao.findById(link.getId())).isEmpty();
    }

    @Test
    void removeNotExistTest() {
        chatDao.saveAndFlush(new Chat(1L));

        assertThrows(NotExistException.class, () -> {
            linkService.remove(1L, URI.create("string"));
        });
    }

    @Test
    void listTest() throws RepeatedRegistrationException, NotExistException, AlreadyExistException {
        chatDao.saveAndFlush(new Chat(1L));
        linkService.add(1L, URI.create("1"));
        linkService.add(1L, URI.create("2"));
        Collection<Link> actualResult = linkService.listAll(1L);

        assertThat(actualResult.stream().map(Link::getUrl)).containsExactlyInAnyOrder("1", "2");
    }

    @Test
    void linkedChatsTest() throws RepeatedRegistrationException, NotExistException, AlreadyExistException {
        chatDao.saveAndFlush(new Chat(1L));
        chatDao.saveAndFlush(new Chat(2L));
        Link link = linkService.add(1L, URI.create("string"));
        linkService.add(2L, URI.create("string"));

        List<Long> actualResult = linkService.linkedChatIds(link.getId());
        assertThat(actualResult).containsExactlyInAnyOrder(1L, 2L);
    }
}
