package edu.java.scrapper.domain.jdbc;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.dao.jdbc.JdbcChatDao;
import edu.java.scrapper.domain.model.chat.Chat;
import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.exception.RepeatedRegistrationException;
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
    void addSuccessfullyTest() throws RepeatedRegistrationException {
        repository.add(22L);

        List<Chat> actualResult = repository.findAll();

        assertThat(actualResult).containsExactly(new Chat(22L, 0));
    }

    @Test
    @Transactional
    @Rollback
    void addExistedChatTest() {
        assertThrows(RepeatedRegistrationException.class, () -> {
            repository.add(22L);
            repository.add(22L);
        });
    }

    @Test
    @Transactional
    @Rollback
    void removeSuccessfullyTest() throws RepeatedRegistrationException, NotExistException {
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
    void findAllTest() throws RepeatedRegistrationException {
        repository.add(11L);
        repository.add(48L);

        List<Chat> actualResult = repository.findAll();

        assertThat(actualResult).containsExactlyInAnyOrder(new Chat(11L, 0), new Chat(48L, 0));
    }
}
