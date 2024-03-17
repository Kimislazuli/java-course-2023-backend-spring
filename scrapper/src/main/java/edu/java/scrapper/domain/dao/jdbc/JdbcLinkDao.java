package edu.java.scrapper.domain.dao.jdbc;

import edu.java.scrapper.domain.exception.AlreadyExistException;
import edu.java.scrapper.domain.exception.NotExistException;
import edu.java.scrapper.domain.model.link.Link;
import edu.java.scrapper.domain.model.link.LinkRowMapper;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcLinkDao {
    private final JdbcClient client;
    private final LinkRowMapper mapper;

    public long add(String url, OffsetDateTime lastUpdate, OffsetDateTime lastCheck) throws AlreadyExistException {
        String query = "INSERT INTO link (url, last_update, last_check) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            client.sql(query)
                .param(url)
                .param(lastUpdate)
                .param(lastCheck)
                .update(keyHolder);

            return keyHolder.getKeys() != null ? (long) keyHolder.getKeys().get("id") : -1;
        } catch (Exception e) {
            throw new AlreadyExistException("This link already exists");
        }
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
}
