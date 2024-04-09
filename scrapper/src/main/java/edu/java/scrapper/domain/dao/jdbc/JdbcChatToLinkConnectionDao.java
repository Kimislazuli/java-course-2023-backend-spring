package edu.java.scrapper.domain.dao.jdbc;

import edu.java.scrapper.domain.model.connection.ChatToLinkConnection;
import edu.java.scrapper.domain.model.connection.ChatToLinkConnectionRowMapper;
import edu.java.scrapper.exception.NotExistException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcChatToLinkConnectionDao {
    private final JdbcClient client;
    private final ChatToLinkConnectionRowMapper mapper;

    public Optional<ChatToLinkConnection> createIfNotExist(long chatId, long linkId) {
        String query = "INSERT INTO chat_to_link_connection (chat_id, link_id) VALUES (?, ?) ON CONFLICT DO NOTHING";

        int rowsAffected = client.sql(query)
            .param(chatId)
            .param(linkId)
            .update();

        return rowsAffected == 1 ? Optional.of(new ChatToLinkConnection(chatId, linkId)) : Optional.empty();
    }

    public void remove(long chatId, long linkId) throws NotExistException {
        String query = "DELETE FROM chat_to_link_connection WHERE chat_id = ? AND link_id = ?";

        int rowsAffected = client.sql(query)
            .param(chatId)
            .param(linkId)
            .update();

        if (rowsAffected == 0) {
            throw new NotExistException("This chat-link pair doesn't exist.");
        }
    }

    public List<ChatToLinkConnection> findAll() {
        String query = "SELECT * FROM chat_to_link_connection";

        return client.sql(query).query(mapper).list();
    }

    public List<ChatToLinkConnection> findAllByChatId(long chatId) {
        String query = "SELECT * FROM chat_to_link_connection WHERE chat_id = ?";

        return client.sql(query).param(chatId).query(mapper).list();
    }

    public List<ChatToLinkConnection> findAllByLinkId(long linkId) {
        String query = "SELECT * FROM chat_to_link_connection WHERE link_id = ?";

        return client.sql(query).param(linkId).query(mapper).list();
    }

    public Optional<ChatToLinkConnection> findByComplexId(long chatId, long linkId) {
        String query = "SELECT * FROM chat_to_link_connection WHERE chat_id = ? AND link_id = ?";

        return client.sql(query).param(chatId).param(linkId).query(mapper).optional();
    }
}
