package edu.java.scrapper.service.processing_services.jpa;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.dao.jpa.JpaChatDao;
import edu.java.scrapper.domain.dao.jpa.JpaChatToLinkConnectionDao;
import edu.java.scrapper.domain.dao.jpa.JpaLinkDao;
import edu.java.scrapper.domain.model.chat.Chat;
import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.exception.RepeatedRegistrationException;
import edu.java.scrapper.service.processing_services.TgChatService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@SpringBootTest(properties = {"app.database-access-type=jpa", "app.retry-config.backoff-type=linear",
    "app.retry-config.status-codes=500, 501",
    "app.retry-config.jitter=0.1", "app.retry-config.attempts=2", "app.retry-config.min-delay=200"})
public class JpaChatServiceTest extends IntegrationTest {
    @Autowired
    TgChatService tgChatService;
    @Autowired
    JpaChatDao jpaTgChatRepository;
    @Autowired
    JpaChatToLinkConnectionDao jpaChatToLinkConnectionDao;

    @BeforeEach
    void setUp() {
        jpaChatToLinkConnectionDao.deleteAll();
        jpaTgChatRepository.deleteAll();
    }

    @Test
    void registerSuccessfullyTest() throws RepeatedRegistrationException {
        tgChatService.register(1L);

        assertThat(jpaTgChatRepository.findById(1L)).isPresent();
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
    void unregisterSuccessfullyTest() throws NotExistException, RepeatedRegistrationException {
        tgChatService.register(1L);
        assertThat(jpaTgChatRepository.findById(1L)).isPresent();
        tgChatService.unregister(1L);
        assertThat(jpaTgChatRepository.findById(1L)).isEmpty();
    }

    @Test
    void unregisterNotExistingTest() {
        assertThrows(NotExistException.class, () -> {
            tgChatService.unregister(1L);
        });
    }

    @Test
    void getChatTest() throws RepeatedRegistrationException {
        tgChatService.register(1L);
        Optional<Chat> chat = tgChatService.getChat(1L);
        assertThat(chat).isPresent();
        assertThat(chat.get().getId()).isEqualTo(1L);
        assertThat(chat.get().getState()).isEqualTo((short) 0);
    }

    @Test
    void setStateTest() throws RepeatedRegistrationException, NotExistException {
        tgChatService.register(1L);
        Optional<Chat> chat = tgChatService.getChat(1L);
        assertThat(chat).isPresent();
        assertThat(chat.get().getId()).isEqualTo(1L);
        assertThat(chat.get().getState()).isEqualTo((short) 0);

        tgChatService.setState(1L, (short) 2);
        Optional<Chat> actualResult = tgChatService.getChat(1L);
        assertThat(actualResult).isPresent();
        assertThat(actualResult.get().getId()).isEqualTo(1L);
        assertThat(actualResult.get().getState()).isEqualTo((short) 2);
    }
}
