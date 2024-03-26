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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
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
             PreparedStatement deleteConnection = connection.prepareStatement("DELETE FROM public.chat_to_link_connection")) {
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
    void addSuccessfullyTest() throws AlreadyExistException, RepeatedRegistrationException {
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
    void removeSuccessfullyTest() throws AlreadyExistException, NotExistException, RepeatedRegistrationException {
        chatRepository.add(5L);
        long linkId = linkRepository.add("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN);
        connectionRepository.add(5L, linkId);
        connectionRepository.remove(5L, linkId);

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
    void findAllTest() throws AlreadyExistException, RepeatedRegistrationException {
        chatRepository.add(3L);
        chatRepository.add(4L);
        long linkId = linkRepository.add("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN);

        connectionRepository.add(3L, linkId);
        connectionRepository.add(4L, linkId);

        List<ChatToLinkConnection> actualResult = connectionRepository.findAll();

        assertThat(actualResult.stream().map(ChatToLinkConnection::getChatId)).containsExactlyInAnyOrder(3L, 4L);
    }
}
