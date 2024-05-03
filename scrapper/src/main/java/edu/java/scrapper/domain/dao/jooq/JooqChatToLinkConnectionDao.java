package edu.java.scrapper.domain.dao.jooq;

import edu.java.scrapper.domain.dao.abstract_dao.ChatToLinkConnectionDao;
import edu.java.scrapper.domain.jooq.Tables;
import edu.java.scrapper.domain.jooq.tables.records.ChatToLinkConnectionRecord;
import edu.java.scrapper.domain.model.connection.ChatToLinkConnection;
import edu.java.scrapper.exception.NotExistException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JooqChatToLinkConnectionDao implements ChatToLinkConnectionDao {
    private final DSLContext context;

    public Optional<ChatToLinkConnection> createIfNotExist(long chatId, long linkId) {
        ChatToLinkConnectionRecord chatToLinkConnectionRecord = context.insertInto(Tables.CHAT_TO_LINK_CONNECTION)
            .columns(Tables.CHAT_TO_LINK_CONNECTION.CHAT_ID, Tables.CHAT_TO_LINK_CONNECTION.LINK_ID)
            .values(chatId, linkId)
            .onConflictDoNothing()
            .returning()
            .fetchOne();

        return chatToLinkConnectionRecord != null ? Optional.of(new ChatToLinkConnection(chatId, linkId))
            : Optional.empty();
    }

    public void remove(long chatId, long linkId) throws NotExistException {
        ChatToLinkConnectionRecord chatToLinkConnectionRecord = context.deleteFrom(Tables.CHAT_TO_LINK_CONNECTION)
            .where(Tables.CHAT_TO_LINK_CONNECTION.CHAT_ID.eq(chatId))
            .and(Tables.CHAT_TO_LINK_CONNECTION.LINK_ID.eq(linkId))
            .returning()
            .fetchOne();

        if (chatToLinkConnectionRecord == null) {
            throw new NotExistException("This chat-link pair doesn't exist.");
        }
    }

    public List<ChatToLinkConnection> findAll() {
        return context.select(Tables.CHAT_TO_LINK_CONNECTION.fields())
            .from(Tables.CHAT_TO_LINK_CONNECTION)
            .fetch()
            .map(r -> new ChatToLinkConnection(
                r.get(Tables.CHAT_TO_LINK_CONNECTION.CHAT_ID),
                r.get(Tables.CHAT_TO_LINK_CONNECTION.LINK_ID)
            ));
    }

    public List<ChatToLinkConnection> findAllByChatId(long chatId) {
        return context.select(Tables.CHAT_TO_LINK_CONNECTION.fields())
            .from(Tables.CHAT_TO_LINK_CONNECTION)
            .where(Tables.CHAT_TO_LINK_CONNECTION.CHAT_ID.eq(chatId))
            .fetch()
            .map(r -> new ChatToLinkConnection(
                r.get(Tables.CHAT_TO_LINK_CONNECTION.CHAT_ID),
                r.get(Tables.CHAT_TO_LINK_CONNECTION.LINK_ID)
            ));
    }

    public List<ChatToLinkConnection> findAllByLinkId(long linkId) {
        return context.select(Tables.CHAT_TO_LINK_CONNECTION.fields())
            .from(Tables.CHAT_TO_LINK_CONNECTION)
            .where(Tables.CHAT_TO_LINK_CONNECTION.LINK_ID.eq(linkId))
            .fetch()
            .map(r -> new ChatToLinkConnection(
                r.get(Tables.CHAT_TO_LINK_CONNECTION.CHAT_ID),
                r.get(Tables.CHAT_TO_LINK_CONNECTION.LINK_ID)
            ));
    }

    public Optional<ChatToLinkConnection> findByComplexId(long chatId, long linkId) {
        return context.select(Tables.CHAT_TO_LINK_CONNECTION.fields())
            .from(Tables.CHAT_TO_LINK_CONNECTION)
            .where(Tables.CHAT_TO_LINK_CONNECTION.CHAT_ID.eq(chatId))
            .and(Tables.CHAT_TO_LINK_CONNECTION.LINK_ID.eq(linkId))
            .fetchOptional()
            .map(r -> new ChatToLinkConnection(
                r.get(Tables.CHAT_TO_LINK_CONNECTION.CHAT_ID),
                r.get(Tables.CHAT_TO_LINK_CONNECTION.LINK_ID)
            ));
    }
}
