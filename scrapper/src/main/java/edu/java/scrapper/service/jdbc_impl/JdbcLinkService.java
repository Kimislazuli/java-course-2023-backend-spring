package edu.java.scrapper.service.jdbc_impl;

import edu.java.scrapper.domain.dao.jdbc.JdbcChatDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcChatToLinkConnectionDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.model.connection.ChatToLinkConnection;
import edu.java.scrapper.domain.model.link.Link;
import edu.java.scrapper.exception.AlreadyExistException;
import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.exception.RepeatedRegistrationException;
import edu.java.scrapper.service.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
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
            linkId = linkDao.add(url.toString(), timestamp, timestamp).get();
            link = new Link(linkId, url.toString(), timestamp, timestamp);
        } else {
            link = optionalLink.get();
            linkId = link.id();
        }

        Optional<ChatToLinkConnection> id = connectionDao.add(tgChatId, linkId);

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
            linkId = link.id();
        }

        long amountOfConnections = connectionDao.findAll()
            .stream()
            .filter(p -> p.linkId() == linkId)
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
            .filter(e -> e.chatId() == tgChatId)
            .map(ChatToLinkConnection::linkId)
            .map(linkDao::getLinkById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }

    @Override
    public List<Long> linkedChats(long linkId) {
        return connectionDao.findAllByLinkId(linkId)
            .stream()
            .map(ChatToLinkConnection::chatId)
            .toList();
    }

    private boolean isChatExists(long id) {
        return chatDao.getById(id).isPresent();
    }
}
