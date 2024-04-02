package edu.java.scrapper.domain.dao.jdbc;

import edu.java.scrapper.domain.model.link.Link;
import edu.java.scrapper.domain.model.link.LinkRowMapper;
import edu.java.scrapper.exception.NotExistException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcLinkDao {
    private final JdbcClient client;
    private final LinkRowMapper mapper;

    public Optional<Long> add(String url, OffsetDateTime lastUpdate, OffsetDateTime lastCheck) {
        String query = "INSERT INTO link (url, last_update, last_check) VALUES (?, ?, ?) ON CONFLICT DO NOTHING";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        client.sql(query)
            .param(url)
            .param(lastUpdate)
            .param(lastCheck)
            .update(keyHolder);

        return keyHolder.getKeys() != null ? Optional.of((long) keyHolder.getKeys().get("id")) : Optional.empty();
    }

    public void remove(long linkId) throws NotExistException {
        String query = "DELETE FROM link WHERE id = ?";

        int rowsAffected = client.sql(query).param(linkId).update();

        if (rowsAffected == 0) {
            throw new NotExistException("This link doesn't exist.");
        }
    }

    public List<Link> findAll() {
        String query = "SELECT * FROM link";

        return client.sql(query).query(mapper).list();
    }

    public Optional<Link> getLinkByUrl(String url) {
        String query = "SELECT * FROM link WHERE url = ?";

        return client.sql(query).param(url).query(mapper).optional();
    }

    public Optional<Link> getLinkById(long id) {
        String query = "SELECT * FROM link WHERE id = ?";

        return client.sql(query).param(id).query(mapper).optional();
    }

    public List<Link> findOldLinksToCheck(OffsetDateTime timestamp) {
        String query = "SELECT * FROM link WHERE last_check < ?";

        return client.sql(query).param(timestamp).query(mapper).list();
    }

    public void updateCheckTime(long linkId, OffsetDateTime timestamp) {
        String query = "UPDATE link SET last_check = ? WHERE id = ?";

        client.sql(query).param(timestamp).param(linkId).update();
    }

    public void updateUpdateTime(long linkId, OffsetDateTime timestamp) {
        String query = "UPDATE link SET last_update = ? WHERE id = ?";

        client.sql(query).param(timestamp).param(linkId).update();
    }
}
