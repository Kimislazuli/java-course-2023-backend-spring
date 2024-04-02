package edu.java.scrapper.domain.jdbc;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.dao.jdbc.JdbcChatDao;
import edu.java.scrapper.domain.model.chat.Chat;
import edu.java.scrapper.exception.NotExistException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ChatDaoTest extends IntegrationTest {
    @Autowired
    private JdbcChatDao repository;

    @Test
    @Transactional
    @Rollback
    void addSuccessfullyTest() {
        repository.add(22L);

        List<Chat> actualResult = repository.findAll();

        assertThat(actualResult).containsExactly(new Chat(22L, 0));
    }

    @Test
    @Transactional
    @Rollback
    void addExistedChatTest() {
        repository.add(22L);
        Optional<Long> actualResult = repository.add(22L);

        assertThat(actualResult).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void removeSuccessfullyTest() throws NotExistException {
        repository.add(22L);
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
    @Transactional
    @Rollback
    void getByIdTest() {
        repository.add(11L);

        Optional<Chat> actualResult = repository.getById(11L);

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get().id()).isEqualTo(11L);
    }

    @Test
    @Transactional
    @Rollback
    void setStateTest() {
        repository.add(11L);

        repository.setState(11L, 2);
        Optional<Chat> actualResult = repository.getById(11L);

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get().state()).isEqualTo(2);
    }
}
