package edu.java.scrapper.service;

import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.exception.RepeatedRegistrationException;

public interface TgChatService {
    void register(long tgChatId) throws RepeatedRegistrationException;

    void unregister(long tgChatId) throws NotExistException;
}
