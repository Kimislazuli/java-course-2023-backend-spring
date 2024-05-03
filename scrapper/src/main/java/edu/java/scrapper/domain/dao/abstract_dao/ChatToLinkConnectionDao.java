package edu.java.scrapper.domain.dao.abstract_dao;

import edu.java.scrapper.domain.model.connection.ChatToLinkConnection;
import edu.java.scrapper.exception.NotExistException;
import java.util.List;
import java.util.Optional;

public interface ChatToLinkConnectionDao {

    Optional<ChatToLinkConnection> createIfNotExist(long chatId, long linkId);

    void remove(long chatId, long linkId) throws NotExistException;

    List<ChatToLinkConnection> findAll();

    List<ChatToLinkConnection> findAllByChatId(long chatId);

    List<ChatToLinkConnection> findAllByLinkId(long linkId);

    Optional<ChatToLinkConnection> findByComplexId(long chatId, long linkId);
}

