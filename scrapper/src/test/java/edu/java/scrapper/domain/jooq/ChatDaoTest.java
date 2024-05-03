package edu.java.scrapper.domain.jooq;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.dao.jooq.JooqChatDao;
import edu.java.scrapper.domain.model.chat.Chat;
import edu.java.scrapper.exception.NotExistException;
import java.util.List;
import java.util.Optional;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {"app.database-access-type=jooq"})
public class ChatDaoTest extends IntegrationTest {
    @Autowired
    private JooqChatDao repository;
    @Autowired
    private DSLContext dsl;

    @BeforeEach
    void setUp() {
        var connection = dsl.delete(Tables.CHAT_TO_LINK_CONNECTION).execute();
        var chat = dsl.delete(Tables.CHAT).execute();
    }

    @Test
    @Transactional
    @Rollback
    void addSuccessfullyTest() {
        repository.createIfNotExist(22L);

        List<Chat> actualResult = repository.findAll();

        assertThat(actualResult).containsExactly(new Chat(22L, (short) 0));
    }

    @Test
    @Transactional
    @Rollback
    void addExistedChatTest() {
        repository.createIfNotExist(22L);
        Optional<Long> actualResult = repository.createIfNotExist(22L);

        assertThat(actualResult).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void removeSuccessfullyTest() throws NotExistException {
        repository.createIfNotExist(22L);
        repository.remove(22L);

        List<Chat> actualResult = repository.findAll();

        assertThat(actualResult).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void removeNotExistedChatTest() {
        assertThrows(NotExistException.class, () -> {
            repository.remove(22L);
        });
    }

    @Test
    void getByIdTest() {
        repository.createIfNotExist(11L);

        Optional<Chat> actualResult = repository.getById(11L);

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get().getId()).isEqualTo(11L);
    }

    @Test
    @Transactional
    @Rollback
    void setStateTest() {
        repository.createIfNotExist(11L);

        repository.setState(11L, (short) 2);
        Optional<Chat> actualResult = repository.getById(11L);

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get().getState()).isEqualTo((short) 2);
    }

    @Test
    @Transactional
    @Rollback
    void setIllegalStateTest() {
        repository.createIfNotExist(11L);

        assertThrows(
            InvalidDataAccessApiUsageException.class,
            () -> repository.setState(11L, (short) 3)
        );
    }
}
