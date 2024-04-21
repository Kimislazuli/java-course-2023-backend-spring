package edu.java.scrapper.service.processing_services.jpa_impl;

import edu.java.scrapper.domain.dao.jpa.JpaChatDao;
import edu.java.scrapper.domain.dao.jpa.JpaChatToLinkConnectionDao;
import edu.java.scrapper.domain.dao.jpa.JpaLinkDao;
import edu.java.scrapper.domain.model.chat.Chat;
import edu.java.scrapper.domain.model.connection.ChatToLinkConnection;
import edu.java.scrapper.domain.model.connection.ConnectionPK;
import edu.java.scrapper.domain.model.link.Link;
import edu.java.scrapper.exception.AlreadyExistException;
import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.service.processing_services.LinkService;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JpaLinkService implements LinkService {
    private final JpaLinkDao linkDao;
    private final JpaChatDao chatDao;
    private final JpaChatToLinkConnectionDao connectionDao;
    private static final String CHAT_NOT_EXIST = "This chat doesn't exist";
    private static final String URL_NOT_EXIST = "This url doesn't exist";

    @Override
    @Transactional
    public Link add(long tgChatId, URI url)
        throws AlreadyExistException {
        Optional<Chat> chatOptional = chatDao.findById(tgChatId);
        if (chatOptional.isEmpty()) {
            chatDao.saveAndFlush(new Chat(tgChatId));
        }

        Optional<Link> linkOptional = linkDao.findByUrl(url.toString());
        Link link;
        if (linkOptional.isEmpty()) {
            link = new Link(url.toString(), OffsetDateTime.now(), OffsetDateTime.now());
            linkDao.saveAndFlush(link);
        } else {
            link = linkOptional.get();
        }

        if (connectionDao.findById(new ConnectionPK(tgChatId, link.getId())).isPresent()) {
            throw new AlreadyExistException("This chat to link connection already exists.");
        }
        connectionDao.saveAndFlush(new ChatToLinkConnection(tgChatId, link.getId()));
        return link;
    }

    @Override
    @Transactional
    public Link remove(long tgChatId, URI url) throws NotExistException {
        chatDao.findById(tgChatId).orElseThrow(() -> new NotExistException(CHAT_NOT_EXIST));

        Optional<Link> linkOptional = linkDao.findByUrl(url.toString());

        if (linkOptional.isEmpty()) {
            throw new NotExistException(URL_NOT_EXIST);
        } else {
            Link link = linkOptional.get();
            ConnectionPK pk = new ConnectionPK(tgChatId, link.getId());
            if (connectionDao.findById(pk).isPresent()) {
                connectionDao.deleteById(pk);
                List<Long> chatIds = connectionDao.findAllChatsByLinkId(link.getId());
                if (chatIds.isEmpty()) {
                    linkDao.delete(link);
                }
                return link;
            } else {
                throw new NotExistException("This link doesn't connect to this chat.");
            }
        }
    }

    @Override
    public Collection<Link> listAll(long tgChatId) {
        List<ChatToLinkConnection> connections = connectionDao.findAll();
        return connections.stream()
            .filter(c -> c.getChatId() == tgChatId)
            .map(ChatToLinkConnection::getLinkId)
            .map(linkDao::findById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }

    @Override
    public List<Long> linkedChatIds(long linkId) {
        return connectionDao.findAllChatsByLinkId(linkId)
            .stream()
            .toList();
    }
}
