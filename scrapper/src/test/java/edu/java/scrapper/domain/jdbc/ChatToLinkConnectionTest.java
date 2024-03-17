package edu.java.scrapper.domain.jdbc;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.dao.jdbc.JdbcChatDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcChatToLinkConnectionDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.exception.AlreadyExistException;
import edu.java.scrapper.domain.exception.NotExistException;
import edu.java.scrapper.domain.model.connection.ChatToLinkConnection;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ChatToLinkConnectionTest extends IntegrationTest {
    @Autowired
    private JdbcChatDao chatRepository;

    @Autowired
    private JdbcLinkDao linkRepository;

    @Autowired
    private JdbcChatToLinkConnectionDao connectionRepository;

    @Test
    @Transactional
    @Rollback
    void addSuccessfullyTest() throws AlreadyExistException {
        chatRepository.add(1L);
        long linkId = linkRepository.add("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN);
        connectionRepository.add(1L, linkId);

        List<ChatToLinkConnection> actualResult = connectionRepository.findAll();

        assertThat(actualResult).containsExactly(new ChatToLinkConnection(1L, linkId));
    }

    @Test
    @Transactional
    @Rollback
    void addExistedChatTest() {
        assertThrows(AlreadyExistException.class, () -> {
            chatRepository.add(1L);
            long linkId = linkRepository.add("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN);
            connectionRepository.add(1L, linkId);
            connectionRepository.add(1L, linkId);
        });
    }

    @Test
    @Transactional
    @Rollback
    void removeSuccessfullyTest() throws AlreadyExistException, NotExistException {
        chatRepository.add(1L);
        long linkId = linkRepository.add("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN);
        connectionRepository.add(1L, linkId);
        connectionRepository.remove(1L, linkId);

        List<ChatToLinkConnection> actualResult = connectionRepository.findAll();

        assertThat(actualResult).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void removeNotExistedChatTest() {
        assertThrows(NotExistException.class, () -> {
            connectionRepository.remove(22L, 22L);
        });
    }

    @Test
    @Transactional
    @Rollback
    void findAllTest() throws AlreadyExistException {
        chatRepository.add(1L);
        chatRepository.add(2L);
        long linkId = linkRepository.add("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN);

        connectionRepository.add(1L, linkId);
        connectionRepository.add(2L, linkId);

        List<ChatToLinkConnection> actualResult = connectionRepository.findAll();

        assertThat(actualResult).containsExactlyInAnyOrder(
            new ChatToLinkConnection(1L, linkId),
            new ChatToLinkConnection(2L, linkId)
        );
    }
}
