package edu.java.scrapper.domain.jdbc;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.dao.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.exception.AlreadyExistException;
import edu.java.scrapper.domain.exception.NotExistException;
import edu.java.scrapper.domain.model.link.Link;
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
public class LinkDaoTest extends IntegrationTest {
    @Autowired
    private JdbcLinkDao repository;

    @Test
    @Transactional
    @Rollback
    void addSuccessfullyTest() throws AlreadyExistException {
        long id = repository.add("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN);

        List<Link> actualResult = repository.findAll();

        assertThat(actualResult).containsExactly(new Link(id, "www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN));
    }

    @Test
    @Transactional
    @Rollback
    void addExistedChatTest() {
        assertThrows(AlreadyExistException.class, () -> {
            repository.add("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN);
            repository.add("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN);
        });
    }

    @Test
    @Transactional
    @Rollback
    void removeSuccessfullyTest() throws AlreadyExistException, NotExistException {
        long id = repository.add("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN);
        repository.remove(id);

        List<Link> actualResult = repository.findAll();

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
        long firstId = repository.add("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN);
        long secondId = repository.add("www.link.com", OffsetDateTime.MAX, OffsetDateTime.MAX);

        List<Link> actualResult = repository.findAll();

        assertThat(actualResult).containsExactlyInAnyOrder(
            new Link(firstId, "www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN),
            new Link(secondId, "www.link.com", OffsetDateTime.MAX, OffsetDateTime.MAX)
        );
    }
}
