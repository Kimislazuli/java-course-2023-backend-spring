package edu.java.scrapper.domain.dao.jdbc;

import  edu.java.scrapper.domain.model.chat.Chat;
import edu.java.scrapper.domain.model.chat.ChatRowMapper;
import edu.java.scrapper.exception.NotExistException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcChatDao {
    private final JdbcClient client;
    private final ChatRowMapper mapper;

    public Optional<Long> createIfNotExist(long chatId) {
        String query = "INSERT INTO chat (id) VALUES (?) ON CONFLICT DO NOTHING";

        int rowsAffected = client.sql(query).param(chatId).update();

        return rowsAffected == 1 ? Optional.of(chatId) : Optional.empty();
    }

    public void remove(long chatId) throws NotExistException {
        String query = "DELETE FROM chat WHERE id = ?";

        int rowsAffected = client.sql(query).param(chatId).update();

        if (rowsAffected == 0) {
            throw new NotExistException("This chat doesn't exist.");
        }
    }

    public List<Chat> findAll() {
        String query = "SELECT * FROM chat";

        return client.sql(query).query(mapper).list();
    }

    public Optional<Chat> getById(long id) {
        String query = "SELECT * FROM chat WHERE id = ?";

        return client.sql(query).param(id).query(mapper).optional();
    }

    public void setState(long chatId, int state) {
        if (state < 0 || state > 2) {
            throw new IllegalArgumentException("States have numbers from 0 to 2.");
        }
        String query = "UPDATE chat SET state = ? WHERE id = ?";
        log.info(String.valueOf(client.sql(query).param(state).param(chatId).update()));
    }
}
