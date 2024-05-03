package edu.java.scrapper.domain.dao.jooq;

import edu.java.scrapper.domain.dao.abstract_dao.LinkDao;
import edu.java.scrapper.domain.jooq.Tables;
import edu.java.scrapper.domain.jooq.tables.records.LinkRecord;
import edu.java.scrapper.domain.model.link.Link;
import edu.java.scrapper.exception.NotExistException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JooqLinkDao implements LinkDao {
    private final DSLContext context;

    public Optional<Long> createIfNotExist(String url, OffsetDateTime lastUpdate, OffsetDateTime lastCheck) {
        LinkRecord linkRecord = context.insertInto(Tables.LINK)
            .columns(Tables.LINK.URL, Tables.LINK.LAST_UPDATE, Tables.LINK.LAST_CHECK)
            .values(url, lastUpdate, lastCheck)
            .onConflictDoNothing()
            .returning(Tables.LINK.ID)
            .fetchOne();

        return linkRecord != null ? Optional.of(linkRecord.getId()) : Optional.empty();
    }

    public void remove(long linkId) throws NotExistException {
        LinkRecord linkRecord = context.deleteFrom(Tables.LINK)
            .where(Tables.LINK.ID.equal(linkId))
            .returning()
            .fetchOne();

        if (linkRecord == null) {
            throw new NotExistException("This link doesn't exist.");
        }
    }

    public List<Link> findAll() {
        return context.select(Tables.LINK.fields())
            .from(Tables.LINK)
            .fetch()
            .map(r -> new Link(
                r.get(Tables.LINK.ID),
                r.get(Tables.LINK.URL),
                OffsetDateTime.ofInstant(r.get(Tables.LINK.LAST_UPDATE).toInstant(), ZoneOffset.UTC),
                OffsetDateTime.ofInstant(r.get(Tables.LINK.LAST_CHECK).toInstant(), ZoneOffset.UTC)
            ));
    }

    public Optional<Link> getLinkByUrl(String url) {
        return context.select(Tables.LINK.fields())
            .from(Tables.LINK)
            .where(Tables.LINK.URL.equal(url))
            .fetchOptional()
            .map(r -> new Link(
                r.get(Tables.LINK.ID),
                r.get(Tables.LINK.URL),
                OffsetDateTime.ofInstant(r.get(Tables.LINK.LAST_UPDATE).toInstant(), ZoneOffset.UTC),
                OffsetDateTime.ofInstant(r.get(Tables.LINK.LAST_CHECK).toInstant(), ZoneOffset.UTC)
            ));
    }

    public Optional<Link> getLinkById(long id) {
        return context.select(Tables.LINK.fields())
            .from(Tables.LINK)
            .where(Tables.LINK.ID.equal(id))
            .fetchOptional()
            .map(r -> new Link(
                r.get(Tables.LINK.ID),
                r.get(Tables.LINK.URL),
                OffsetDateTime.ofInstant(r.get(Tables.LINK.LAST_UPDATE).toInstant(), ZoneOffset.UTC),
                OffsetDateTime.ofInstant(r.get(Tables.LINK.LAST_CHECK).toInstant(), ZoneOffset.UTC)
            ));
    }

    public List<Link> findOldLinksToCheck(OffsetDateTime timestamp) {
        return context.select(Tables.LINK.fields())
            .from(Tables.LINK)
            .where(Tables.LINK.LAST_CHECK.le(timestamp))
            .fetch()
            .map(r -> new Link(
                r.get(Tables.LINK.ID),
                r.get(Tables.LINK.URL),
                OffsetDateTime.ofInstant(r.get(Tables.LINK.LAST_UPDATE).toInstant(), ZoneOffset.UTC),
                OffsetDateTime.ofInstant(r.get(Tables.LINK.LAST_CHECK).toInstant(), ZoneOffset.UTC)
            ));
    }

    public void updateCheckTime(long linkId, OffsetDateTime timestamp) {
        context.update(Tables.LINK)
            .set(Tables.LINK.LAST_CHECK, timestamp)
            .where(Tables.LINK.ID.eq(linkId))
            .returning().fetchOptional();
    }

    public void updateUpdateTime(long linkId, OffsetDateTime timestamp) {
        context.update(Tables.LINK)
            .set(Tables.LINK.LAST_UPDATE, timestamp)
            .where(Tables.LINK.ID.eq(linkId))
            .returning().fetchOptional();
    }

    public List<Link> findAllLinksByChatId(long chatId) {
        return context.select()
            .from(Tables.CHAT_TO_LINK_CONNECTION)
            .join(Tables.LINK).on(Tables.LINK.ID.eq(Tables.CHAT_TO_LINK_CONNECTION.LINK_ID))
            .fetch()
            .map(r -> new Link(
                r.get(Tables.LINK.ID),
                r.get(Tables.LINK.URL),
                OffsetDateTime.ofInstant(r.get(Tables.LINK.LAST_UPDATE).toInstant(), ZoneOffset.UTC),
                OffsetDateTime.ofInstant(r.get(Tables.LINK.LAST_CHECK).toInstant(), ZoneOffset.UTC)
            ));
    }
}
