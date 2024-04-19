package edu.java.scrapper.service.jdbc;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.dao.jdbc.JdbcChatToLinkConnectionDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.model.link.Link;
import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.service.UpdaterService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"app.database-access-type=jdbc"})
public class JdbcUpdaterServiceTest extends IntegrationTest {
    @Autowired
    UpdaterService updaterService;
    @Autowired
    JdbcLinkDao linkDao;
    @Autowired
    JdbcChatToLinkConnectionDao connectionDao;

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
    void checkTest() throws NotExistException {
        linkDao.createIfNotExist(
                "www.url.com",
                OffsetDateTime.parse("2020-05-20T05:40:08.721Z"),
                OffsetDateTime.parse("2020-05-20T05:40:08.721Z")
        );

        Optional<Link> link = linkDao.getLinkByUrl("www.url.com");
        assertThat(link).isPresent();
        OffsetDateTime previous = link.get().getLastCheck();

        updaterService.check(link.get().getId(), OffsetDateTime.parse("2020-06-20T05:40:08.721Z"));

        Optional<Link> newLink = linkDao.getLinkByUrl("www.url.com");
        assertThat(newLink).isPresent();
        OffsetDateTime current = newLink.get().getLastCheck();

        assertThat(current).isAfter(previous);
    }

    @Test
    void updateTest() throws NotExistException {
        linkDao.createIfNotExist(
                "www.url.com",
                OffsetDateTime.parse("2020-05-20T05:40:08.721Z"),
                OffsetDateTime.parse("2020-05-20T05:40:08.721Z")
        );

        Optional<Link> link = linkDao.getLinkByUrl("www.url.com");
        assertThat(link).isPresent();
        OffsetDateTime previous = link.get().getLastUpdate();

        updaterService.update(link.get().getId(), OffsetDateTime.parse("2020-06-20T05:40:08.721Z"));

        Optional<Link> newLink = linkDao.getLinkByUrl("www.url.com");
        assertThat(newLink).isPresent();
        OffsetDateTime current = newLink.get().getLastUpdate();

        assertThat(current).isAfter(previous);
    }
}
