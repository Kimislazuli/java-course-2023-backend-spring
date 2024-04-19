package edu.java.scrapper.service;

import edu.java.scrapper.domain.model.chat.Chat;
import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.exception.RepeatedRegistrationException;
import java.util.Optional;

public interface TgChatService {
    void register(long tgChatId) throws RepeatedRegistrationException;

    void unregister(long tgChatId) throws NotExistException;

    Optional<Chat> getChat(long tgChatId);

    void setState(long chatId, int state);
}
