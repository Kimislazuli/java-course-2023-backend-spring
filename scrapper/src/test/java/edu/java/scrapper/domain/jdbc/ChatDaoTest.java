package edu.java.scrapper.domain.jdbc;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.dao.jdbc.JdbcChatDao;
import edu.java.scrapper.domain.exception.AlreadyExistException;
import edu.java.scrapper.domain.exception.NotExistException;
import edu.java.scrapper.domain.model.chat.Chat;
import java.util.List;
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
    void addSuccessfullyTest() throws AlreadyExistException {
        repository.add(22L);

        List<Chat> actualResult = repository.findAll();

        assertThat(actualResult).containsExactly(new Chat(22L));
    }

    @Test
    @Transactional
    @Rollback
    void addExistedChatTest() {
        assertThrows(AlreadyExistException.class, () -> {
            repository.add(22L);
            repository.add(22L);
        });
    }

    @Test
    @Transactional
    @Rollback
    void removeSuccessfullyTest() throws AlreadyExistException, NotExistException {
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
    void findAllTest() throws AlreadyExistException {
        repository.add(11L);
        repository.add(48L);

        List<Chat> actualResult = repository.findAll();

        assertThat(actualResult).containsExactlyInAnyOrder(new Chat(11L), new Chat(48L));
    }
}