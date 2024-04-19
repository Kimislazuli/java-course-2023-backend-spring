package edu.java.scrapper.domain.jdbc;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.dao.jdbc.JdbcChatDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcChatToLinkConnectionDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.model.connection.ChatToLinkConnection;
import edu.java.scrapper.exception.AlreadyExistException;
import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.exception.RepeatedRegistrationException;
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
public class ChatToLinkConnectionTest extends IntegrationTest {
    @Autowired
    private JdbcChatDao chatRepository;

    @Autowired
    private JdbcLinkDao linkRepository;

    @Autowired
    private JdbcChatToLinkConnectionDao connectionRepository;

    @BeforeEach
    void setUp() {
        try (Connection connection = POSTGRES.createConnection("");
             PreparedStatement deleteChat = connection.prepareStatement("DELETE FROM public.chat");
             PreparedStatement deleteLink = connection.prepareStatement("DELETE FROM public.link");
             PreparedStatement deleteConnection = connection.prepareStatement(
                     "DELETE FROM public.chat_to_link_connection")) {
            deleteConnection.execute();
            deleteChat.execute();
            deleteLink.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Transactional
    @Rollback
    void addSuccessfullyTest() {
        chatRepository.createIfNotExist(1L);
        long linkId = linkRepository.createIfNotExist("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN).get();
        connectionRepository.createIfNotExist(1L, linkId);

        List<ChatToLinkConnection> actualResult = connectionRepository.findAll();

        assertThat(actualResult).containsExactly(new ChatToLinkConnection(1L, linkId));
    }

    @Test
    @Transactional
    @Rollback
    void addExistedChatTest() {
        chatRepository.createIfNotExist(1L);
        long linkId = linkRepository.createIfNotExist("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN).get();
        connectionRepository.createIfNotExist(1L, linkId);
        Optional<ChatToLinkConnection> actualResult = connectionRepository.createIfNotExist(1L, linkId);

        assertThat(actualResult).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void removeSuccessfullyTest() throws NotExistException {
        chatRepository.createIfNotExist(1L);
        long linkId = linkRepository.createIfNotExist("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN).get();
        connectionRepository.createIfNotExist(1L, linkId);
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
    void findAllTest() {
        chatRepository.createIfNotExist(1L);
        chatRepository.createIfNotExist(2L);
        long linkId = linkRepository.createIfNotExist("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN).get();

        connectionRepository.createIfNotExist(1L, linkId);
        connectionRepository.createIfNotExist(2L, linkId);

        List<ChatToLinkConnection> actualResult = connectionRepository.findAll();

        assertThat(actualResult).containsExactlyInAnyOrder(
                new ChatToLinkConnection(1L, linkId),
                new ChatToLinkConnection(2L, linkId)
        );
    }

    @Test
    @Transactional
    @Rollback
    void findAllByLinkIdTest() {
        chatRepository.createIfNotExist(1L);
        chatRepository.createIfNotExist(2L);
        long linkId =
                linkRepository.createIfNotExist("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN).get();

        connectionRepository.createIfNotExist(1L, linkId);
        connectionRepository.createIfNotExist(2L, linkId);

        List<ChatToLinkConnection> actualResult = connectionRepository.findAllByLinkId(linkId);

        assertThat(actualResult).containsExactlyInAnyOrder(
                new ChatToLinkConnection(1L, linkId),
                new ChatToLinkConnection(2L, linkId)
        );
    }

    @Test
    @Transactional
    @Rollback
    void findAllByLinkChatTest() {
        chatRepository.createIfNotExist(1L);
        long firstLink =
                linkRepository.createIfNotExist("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN).get();
        long secondLink =
                linkRepository.createIfNotExist("www.google.com", OffsetDateTime.MIN, OffsetDateTime.MIN).get();

        connectionRepository.createIfNotExist(1L, firstLink);
        connectionRepository.createIfNotExist(1L, secondLink);

        List<ChatToLinkConnection> actualResult = connectionRepository.findAllByChatId(1L);

        assertThat(actualResult).containsExactlyInAnyOrder(
                new ChatToLinkConnection(1L, firstLink),
                new ChatToLinkConnection(1L, secondLink)
        );
    }

    @Test
    @Transactional
    @Rollback
    void findByComplexIdTest() throws AlreadyExistException, RepeatedRegistrationException {
        chatRepository.createIfNotExist(1L);
        long linkId =
                linkRepository.createIfNotExist("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN).get();

        connectionRepository.createIfNotExist(1L, linkId);

        Optional<ChatToLinkConnection> actualResult = connectionRepository.findByComplexId(1L, linkId);

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(new ChatToLinkConnection(1L, linkId));
    }
}
