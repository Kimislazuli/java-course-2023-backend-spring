package edu.java.scrapper.service.processing_services.jdbc_impl;

import edu.java.scrapper.domain.dao.jdbc.JdbcChatDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcChatToLinkConnectionDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.model.connection.ChatToLinkConnection;
import edu.java.scrapper.domain.model.link.Link;
import edu.java.scrapper.exception.AlreadyExistException;
import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.exception.RepeatedRegistrationException;
import edu.java.scrapper.service.processing_services.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {
    private final JdbcLinkDao linkDao;
    private final JdbcChatDao chatDao;
    private final JdbcChatToLinkConnectionDao connectionDao;

    @Override
    public Link add(long tgChatId, URI url) throws AlreadyExistException, RepeatedRegistrationException {
        if (!isChatExists(tgChatId)) {
            chatDao.add(tgChatId);
        }

        Optional<Link> optionalLink = linkDao.getLinkByUrl(url.toString());
        long linkId;
        Link link;

        if (optionalLink.isEmpty()) {
            OffsetDateTime timestamp = OffsetDateTime.now();
            linkId = linkDao.add(url.toString(), timestamp, timestamp);
            link = new Link(linkId, url.toString(), timestamp, timestamp);
        } else {
            link = optionalLink.get();
            linkId = link.getId();
        }

        if (isPairExists(tgChatId, linkId)) {
            throw new AlreadyExistException("This pair already exists");
        }
        connectionDao.add(tgChatId, linkId);
        return link;
    }

    @Override
    public Link remove(long tgChatId, URI url) throws NotExistException {
        Optional<Link> optionalLink = linkDao.getLinkByUrl(url.toString());
        long linkId;
        Link link;

        if (optionalLink.isEmpty()) {
            throw new NotExistException("Link doesn't exist");
        } else {
            link = optionalLink.get();
            linkId = link.getId();
        }

        long amountOfConnections = connectionDao.findAll()
            .stream()
            .filter(p -> p.getLinkId() == linkId)
            .count();

        connectionDao.remove(tgChatId, linkId);

        if (amountOfConnections == 1) {
            linkDao.remove(linkId);
        } else if (amountOfConnections == 0) {
            throw new NotExistException("This pair doesn't exist");
        }

        return link;
    }

    @Override
    public Collection<Link> listAll(long tgChatId) {
        return connectionDao.findAll()
            .stream()
            .filter(e -> e.getChatId() == tgChatId)
            .map(ChatToLinkConnection::getLinkId)
            .map(linkDao::getLinkById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }

    @Override
    public List<Long> linkedChatIds(long linkId) {
        return connectionDao.findAllByLinkId(linkId)
            .stream()
            .map(ChatToLinkConnection::getChatId)
            .toList();
    }

    private boolean isChatExists(long id) {
        long chatCount = chatDao.findAll()
            .stream()
            .filter(c -> c.getId() == id)
            .count();

        return chatCount == 1;
    }

    private boolean isPairExists(long chatId, long linkId) {
        long chatCount = connectionDao.findAll()
            .stream()
            .filter(c -> c.getChatId() == chatId && c.getLinkId() == linkId)
            .count();

        return chatCount == 1;
    }
}
