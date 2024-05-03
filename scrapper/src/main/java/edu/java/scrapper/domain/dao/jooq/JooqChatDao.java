package edu.java.scrapper.domain.dao.jooq;

import edu.java.scrapper.domain.dao.abstract_dao.ChatDao;
import edu.java.scrapper.domain.jooq.Tables;
import edu.java.scrapper.domain.jooq.tables.records.ChatRecord;
import edu.java.scrapper.domain.model.chat.Chat;
import edu.java.scrapper.exception.NotExistException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JooqChatDao implements ChatDao {
    private final DSLContext context;

    public Optional<Long> createIfNotExist(long chatId) {
        ChatRecord chatRecord = context.insertInto(Tables.CHAT)
            .columns(Tables.CHAT.ID)
            .values(chatId)
            .onConflictDoNothing()
            .returning(Tables.CHAT.ID)
            .fetchOne();

        return chatRecord != null ? Optional.of(chatId) : Optional.empty();
    }

    public void remove(long chatId) throws NotExistException {
        ChatRecord chatRecord = context.deleteFrom(Tables.CHAT)
            .where(Tables.CHAT.ID.equal(chatId))
            .returning(Tables.CHAT.ID)
            .fetchOne();

        if (chatRecord == null) {
            throw new NotExistException("This chat doesn't exist.");
        }
    }

    public List<Chat> findAll() {
        return context.select(Tables.CHAT.fields())
            .from(Tables.CHAT)
            .fetch()
            .map(r -> new Chat(r.get(Tables.CHAT.ID), r.get(Tables.CHAT.STATE)));
    }

    public Optional<Chat> getById(long id) {
        return context.select(Tables.CHAT.fields())
            .from(Tables.CHAT)
            .where(Tables.CHAT.ID.equal(id))
            .fetchOptional()
            .map(r -> new Chat(r.get(Tables.CHAT.ID), r.get(Tables.CHAT.STATE)));
    }

    public void setState(long chatId, short state) {
        if (state < 0 || state > 2) {
            throw new IllegalArgumentException("States have numbers from 0 to 2.");
        }
        context.update(Tables.CHAT)
            .set(Tables.CHAT.STATE, state)
            .where(Tables.CHAT.ID.eq(chatId))
            .returning().fetchOptional();
    }
}
