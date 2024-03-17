package edu.java.scrapper.service.jdbc_impl;

import edu.java.scrapper.domain.dao.jdbc.JdbcChatDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcChatToLinkConnectionDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.model.chat.Chat;
import edu.java.scrapper.domain.model.connection.ChatToLinkConnection;
import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.exception.RepeatedRegistrationException;
import edu.java.scrapper.service.TgChatService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JdbcTgChatService implements TgChatService {
    private final JdbcLinkDao linkDao;
    private final JdbcChatDao chatDao;
    private final JdbcChatToLinkConnectionDao connectionDao;

    @Override
    public void register(long tgChatId) throws RepeatedRegistrationException {
        Optional<Chat> chatOptional = chatDao.getById(tgChatId);

        if (chatOptional.isPresent()) {
            throw new RepeatedRegistrationException("This chat already exists");
        }

        chatDao.add(tgChatId);
    }

    @Override
    public void unregister(long tgChatId) throws NotExistException {
        Optional<Chat> chatOptional = chatDao.getById(tgChatId);
        if (chatOptional.isEmpty()) {
            throw new NotExistException("This chat doesn't exist");
        }


        List<Long> links = connectionDao.findAllByChatId(tgChatId)
            .stream()
            .map(ChatToLinkConnection::linkId)
            .toList();
        for (Long linkId : links) {
            List<ChatToLinkConnection> connections = connectionDao.findAllByLinkId(linkId);

            connectionDao.remove(tgChatId, linkId);

            if (connections.size() == 1) {
                linkDao.remove(linkId);
            }
        }

        chatDao.remove(tgChatId);
    }
}
