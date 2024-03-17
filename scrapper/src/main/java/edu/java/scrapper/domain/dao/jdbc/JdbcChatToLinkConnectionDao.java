package edu.java.scrapper.domain.dao.jdbc;

import edu.java.scrapper.domain.exception.AlreadyExistException;
import edu.java.scrapper.domain.exception.NotExistException;
import edu.java.scrapper.domain.model.connection.ChatToLinkConnection;
import edu.java.scrapper.domain.model.connection.ChatToLinkConnectionRowMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcChatToLinkConnectionDao {
    private final JdbcClient client;
    private final ChatToLinkConnectionRowMapper mapper;

    // здесь аналогичная проблема: нет никакого айди, который можно было бы вернуть, в итоге
    // в итоге возвращается количество задействованных строк, что не очень логично с точки
    // зрения апи в целом.

    public long add(long chatId, long linkId) throws AlreadyExistException {
        try {
            String query = "INSERT INTO chat_to_link_connection (chat_id, link_id) VALUES (?, ?)";

            int rowsAffected = client.sql(query)
                .param(chatId)
                .param(linkId)
                .update();

            return rowsAffected == 1 ? rowsAffected : -1;
        } catch (Exception e) {
            throw new AlreadyExistException("This pair already exists in table.");
        }
    }

    public void remove(long chatId, long linkId) throws NotExistException {
        String query = "DELETE FROM chat_to_link_connection WHERE chat_id = ? AND link_id = ?";

        int rowsAffected = client.sql(query)
            .param(chatId)
            .param(linkId)
            .update();

        if (rowsAffected == 0) {
            throw new NotExistException("This chat-link pair doesn't exist.");
        } // ещё нужно как-то обрабатывать кейс отсутствия используемых айди в таблицах чатов/ссылок.
        // пока не придумала, как их разделить.
    }

    public List<ChatToLinkConnection> findAll() {
        String query = "SELECT * FROM chat_to_link_connection";
        return client.sql(query).query(mapper).list();
    }
}
