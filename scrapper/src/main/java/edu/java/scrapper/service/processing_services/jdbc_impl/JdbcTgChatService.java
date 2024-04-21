package edu.java.scrapper.service.processing_services.jdbc_impl;

import edu.java.scrapper.domain.dao.jdbc.JdbcChatDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcChatToLinkConnectionDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.model.chat.Chat;
import edu.java.scrapper.domain.model.connection.ChatToLinkConnection;
import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.exception.RepeatedRegistrationException;
import jakarta.transaction.Transactional;
import edu.java.scrapper.service.processing_services.TgChatService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
public class JdbcTgChatService implements TgChatService {
    private final JdbcLinkDao linkDao;
    private final JdbcChatDao chatDao;
    private final JdbcChatToLinkConnectionDao connectionDao;

    @Override
    public void register(long tgChatId) throws RepeatedRegistrationException {
        Optional<Chat> chatOptional = chatDao.getById(tgChatId);

        if (tgChatId <= 0) {
            throw new IllegalArgumentException("Id should be positive");
        }

        if (chatOptional.isPresent()) {
            throw new RepeatedRegistrationException("This chat already exists");
        }

        chatDao.createIfNotExist(tgChatId);
    }

    @Override
    public void unregister(long tgChatId) throws NotExistException {
        Optional<Chat> chatOptional = chatDao.getById(tgChatId);
        if (chatOptional.isEmpty()) {
            throw new NotExistException("This chat doesn't exist");
        }


        List<Long> links = connectionDao.findAllByChatId(tgChatId)
            .stream()
            .map(ChatToLinkConnection::getLinkId)
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

    @Override
    public Optional<Chat> getChat(long tgChatId) {
        return chatDao.getById(tgChatId);
    }

    @Override
    public void setState(long chatId, int state) {
        chatDao.setState(chatId, state);
    }
}
