package edu.java.scrapper.domain.dao.abstract_dao;

import edu.java.scrapper.domain.model.chat.Chat;
import edu.java.scrapper.exception.NotExistException;
import java.util.List;
import java.util.Optional;

public interface ChatDao {
    Optional<Long> createIfNotExist(long chatId);

    void remove(long chatId) throws NotExistException;

    List<Chat> findAll();

    Optional<Chat> getById(long id);

    void setState(long chatId, short state);
}
