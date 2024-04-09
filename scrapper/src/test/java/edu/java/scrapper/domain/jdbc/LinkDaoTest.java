package edu.java.scrapper.domain.jdbc;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.dao.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.model.link.Link;
import edu.java.scrapper.exception.NotExistException;
import java.time.OffsetDateTime;
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
public class LinkDaoTest extends IntegrationTest {
    @Autowired
    private JdbcLinkDao repository;

    @Test
    @Transactional
    @Rollback
    void addSuccessfullyTest() {
        long id = repository.createIfNotExist("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN).get();

        List<Link> actualResult = repository.findAll();

        assertThat(actualResult).containsExactly(new Link(id, "www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN));
    }

    @Test
    @Transactional
    @Rollback
    void addExistedChatTest() {
        repository.createIfNotExist("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN);
        Optional<Long> actualResult = repository.createIfNotExist("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN);

        assertThat(actualResult).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void removeSuccessfullyTest() throws NotExistException {
        long id = repository.createIfNotExist("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN).get();
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
    void findAllTest() {
        long firstId = repository.createIfNotExist("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN).get();
        long secondId = repository.createIfNotExist("www.link.com", OffsetDateTime.MAX, OffsetDateTime.MAX).get();

        List<Link> actualResult = repository.findAll();

        assertThat(actualResult).containsExactlyInAnyOrder(
            new Link(firstId, "www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN),
            new Link(secondId, "www.link.com", OffsetDateTime.MAX, OffsetDateTime.MAX)
        );
    }


    @Test
    @Transactional
    @Rollback
    void getLinkByUrlTest() {
        repository.createIfNotExist("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN).get();

        Optional<Link> actualResult = repository.getLinkByUrl("www.url.com");

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get().url()).isEqualTo("www.url.com");
    }

    @Test
    @Transactional
    @Rollback
    void getLinkByIdTest() {
        long id = repository.createIfNotExist("www.url.com", OffsetDateTime.MIN, OffsetDateTime.MIN).get();

        Optional<Link> actualResult = repository.getLinkById(id);

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get().id()).isEqualTo(id);
        assertThat(actualResult.get().url()).isEqualTo("www.url.com");
    }
}
