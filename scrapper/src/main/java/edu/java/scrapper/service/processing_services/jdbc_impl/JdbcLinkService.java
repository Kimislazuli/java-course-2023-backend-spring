package edu.java.scrapper.service.jdbc_impl;

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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {
    private final JdbcLinkDao linkDao;
    private final JdbcChatDao chatDao;
    private final JdbcChatToLinkConnectionDao connectionDao;

    @Override
    public Link add(long tgChatId, URI url) throws AlreadyExistException {
        if (!isChatExists(tgChatId)) {
            chatDao.createIfNotExist(tgChatId);
        }

        Optional<Link> optionalLink = linkDao.getLinkByUrl(url.toString());
        long linkId;
        Link link;

        if (optionalLink.isEmpty()) {
            OffsetDateTime timestamp = OffsetDateTime.now();
            linkId = linkDao.createIfNotExist(url.toString(), timestamp, timestamp).get();
            link = new Link(linkId, url.toString(), timestamp, timestamp);
        } else {
            link = optionalLink.get();
            linkId = link.getId();
        }

        Optional<ChatToLinkConnection> id = connectionDao.createIfNotExist(tgChatId, linkId);

        if (id.isEmpty()) {
            throw new AlreadyExistException("This pair already exists");
        }

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

        List<ChatToLinkConnection> connections = connectionDao.findAllByLinkId(linkId);
        if (connections.stream().noneMatch(p -> p.getChatId() == tgChatId)) {
            throw new NotExistException("This pair doesn't exist");
        }

        connectionDao.remove(tgChatId, linkId);

        if (connections.size() == 1) {
            linkDao.remove(linkId);
        }

        return link;
    }

    @Override
    public Collection<Link> listAll(long tgChatId) {
        return linkDao.findAllLinksByChatId(tgChatId);
    }

    @Override
    public List<Long> linkedChatIds(long linkId) {
        return connectionDao.findAllByLinkId(linkId)
            .stream()
            .map(ChatToLinkConnection::getChatId)
            .toList();
    }

    private boolean isChatExists(long id) {
        return chatDao.getById(id).isPresent();
    }
}
