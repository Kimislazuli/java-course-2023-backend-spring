package edu.java.scrapper.service.jdbc;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.dao.jdbc.JdbcChatDao;
import edu.java.scrapper.domain.model.chat.Chat;
import edu.java.scrapper.exception.AlreadyExistException;
import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.exception.RepeatedRegistrationException;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.TgChatService;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@SpringBootTest(properties = {"app.database-access-type=jdbc"})
public class JdbcChatServiceTest extends IntegrationTest {
    @Autowired
    TgChatService tgChatService;
    @Autowired
    JdbcChatDao jdbcTgChatRepository;
    @Autowired
    LinkService linkService;

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
    void registerSuccessfullyTest() throws RepeatedRegistrationException {
        tgChatService.register(1L);

        Optional<Chat> chat = jdbcTgChatRepository.getById(1L);
        assertThat(chat).isPresent();
        assertThat(chat.get().getId()).isEqualTo(1L);
    }

    @Test
    void getChatTest() {
        jdbcTgChatRepository.createIfNotExist(1L);

        Optional<Chat> chat = jdbcTgChatRepository.getById(1L);
        assertThat(chat).isPresent();
        assertThat(chat.get().getId()).isEqualTo(1L);
    }

    @Test
    void registerWrongIdTest() {
        assertThrows(IllegalArgumentException.class, () -> tgChatService.register(-1L));
    }

    @Test
    void reregistrationTest() {
        assertThrows(RepeatedRegistrationException.class, () -> {
            tgChatService.register(1L);
            tgChatService.register(1L);
        });
    }

    @Test
    void unregisterSuccessfullyTest() throws RepeatedRegistrationException, NotExistException {
        tgChatService.register(1L);
        assertThat(jdbcTgChatRepository.getById(1L)).isPresent();
        tgChatService.unregister(1L);
        assertThat(jdbcTgChatRepository.getById(1L)).isEmpty();
    }

    @Test
    void unregisterWithLinksTest() throws RepeatedRegistrationException, NotExistException, AlreadyExistException {
        tgChatService.register(1L);
        linkService.add(1L, URI.create("www.url.com"));
        assertThat(jdbcTgChatRepository.getById(1L)).isPresent();
        tgChatService.unregister(1L);
        assertThat(jdbcTgChatRepository.getById(1L)).isEmpty();
    }

    @Test
    void unregisterNotExistingTest() {
        assertThrows(NotExistException.class, () -> {
            tgChatService.unregister(1L);
        });
    }

    @Test
    void setStateTest() throws RepeatedRegistrationException, NotExistException {
        tgChatService.register(1L);
        Optional<Chat> chat = tgChatService.getChat(1L);
        assertThat(chat).isPresent();
        assertThat(chat.get().getId()).isEqualTo(1L);
        assertThat(chat.get().getState()).isEqualTo(0);

        tgChatService.setState(1L, 2);
        Optional<Chat> actualResult = tgChatService.getChat(1L);
        assertThat(actualResult).isPresent();
        assertThat(actualResult.get().getId()).isEqualTo(1L);
        assertThat(actualResult.get().getState()).isEqualTo(2);
    }
}
