package edu.java.scrapper.domain.jooq;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.dao.jooq.JooqChatDao;
import edu.java.scrapper.domain.dao.jooq.JooqChatToLinkConnectionDao;
import edu.java.scrapper.domain.dao.jooq.JooqLinkDao;
import edu.java.scrapper.domain.model.connection.ChatToLinkConnection;
import edu.java.scrapper.exception.AlreadyExistException;
import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.exception.RepeatedRegistrationException;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {"app.database-access-type=jooq"})
public class ChatToLinkConnectionTest extends IntegrationTest {
    @Autowired
    private JooqLinkDao linkRepository;
    @Autowired
    private JooqChatDao chatRepository;
    @Autowired
    private JooqChatToLinkConnectionDao connectionRepository;
    @Autowired
    private DSLContext dsl;

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
        chatRepository.createIfNotExist(1L);
        long linkId = linkRepository.createIfNotExist("www.url.com", OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            2,
            ZoneOffset.UTC
        ), OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            2,
            ZoneOffset.UTC
        )).get();
        connectionRepository.createIfNotExist(1L, linkId);

        List<ChatToLinkConnection> actualResult = connectionRepository.findAll();

        assertThat(actualResult).containsExactly(new ChatToLinkConnection(1L, linkId));
    }

    @Test
    @Transactional
    @Rollback
    void addExistedChatTest() {
        chatRepository.createIfNotExist(1L);
        long linkId = linkRepository.createIfNotExist("www.url.com", OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            2,
            ZoneOffset.UTC
        ), OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            2,
            ZoneOffset.UTC
        )).get();
        connectionRepository.createIfNotExist(1L, linkId);
        Optional<ChatToLinkConnection> actualResult = connectionRepository.createIfNotExist(1L, linkId);

        assertThat(actualResult).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void removeSuccessfullyTest() throws NotExistException {
        chatRepository.createIfNotExist(1L);
        long linkId = linkRepository.createIfNotExist("www.url.com", OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            2,
            ZoneOffset.UTC
        ), OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            2,
            ZoneOffset.UTC
        )).get();
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
        long linkId = linkRepository.createIfNotExist("www.url.com", OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            2,
            ZoneOffset.UTC
        ), OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            2,
            ZoneOffset.UTC
        )).get();

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
        long linkId = linkRepository.createIfNotExist("www.url.com", OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            2,
            ZoneOffset.UTC
        ), OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            2,
            ZoneOffset.UTC
        )).get();

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
        long firstLink = linkRepository.createIfNotExist("www.url.com", OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            2,
            ZoneOffset.UTC
        ), OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            2,
            ZoneOffset.UTC
        )).get();
        long secondLink = linkRepository.createIfNotExist("www.google.com", OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            2,
            ZoneOffset.UTC
        ), OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            2,
            ZoneOffset.UTC
        )).get();

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
        long linkId = linkRepository.createIfNotExist("www.url.com", OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            2,
            ZoneOffset.UTC
        ), OffsetDateTime.of(2010,
            2,
            2,
            2,
            2,
            2,
            2,
            ZoneOffset.UTC
        )).get();

        connectionRepository.createIfNotExist(1L, linkId);

        Optional<ChatToLinkConnection> actualResult = connectionRepository.findByComplexId(1L, linkId);

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(new ChatToLinkConnection(1L, linkId));
    }
}
