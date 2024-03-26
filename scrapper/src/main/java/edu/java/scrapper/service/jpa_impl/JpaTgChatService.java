package edu.java.scrapper.service.jpa_impl;

import edu.java.scrapper.domain.dao.jpa.JpaChatDao;
import edu.java.scrapper.domain.model.chat.Chat;
import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.exception.RepeatedRegistrationException;
import edu.java.scrapper.service.TgChatService;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaTgChatService implements TgChatService {
    private final JpaChatDao chatDao;

    @Override
    @Transactional
    public void register(long tgChatId) throws RepeatedRegistrationException {
        if (tgChatId <= 0) {
            throw new IllegalArgumentException("Id should be positive");
        }
        if (!chatDao.existsById(tgChatId)) {
            chatDao.saveAndFlush(new Chat(tgChatId, 0));
        } else {
            throw new RepeatedRegistrationException("This chat already exists");
        }
    }

    @Override
    @Transactional
    public void unregister(long tgChatId) throws NotExistException {
        if (chatDao.existsById(tgChatId)) {
            chatDao.deleteById(tgChatId);
        } else {
            throw new NotExistException("This chat doesn't exists");
        }
    }

    @Override
    public Optional<Chat> getChat(long tgChatId) {
        return chatDao.findById(tgChatId);
    }

    @Override
    @Transactional
    public void setState(long chatId, int state) throws NotExistException {
        Optional<Chat> chat = chatDao.findById(chatId);
        if (chat.isPresent()) {
            chat.get().setState(state);
        } else {
            throw new NotExistException("This chat doesn't exist");
        }
    }
}
