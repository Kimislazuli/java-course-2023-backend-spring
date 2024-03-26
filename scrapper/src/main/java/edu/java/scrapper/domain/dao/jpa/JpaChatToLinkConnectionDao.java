package edu.java.scrapper.domain.dao.jpa;

import edu.java.scrapper.domain.model.connection.ChatToLinkConnection;
import edu.java.scrapper.domain.model.connection.ConnectionPK;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaChatToLinkConnectionDao extends JpaRepository<ChatToLinkConnection, ConnectionPK> {
    @Query(value = "SELECT chat_id FROM chat_to_link_connection WHERE link_id = :linkId", nativeQuery = true)
    List<Long> findAllChatsByLinkId(long linkId);
}
