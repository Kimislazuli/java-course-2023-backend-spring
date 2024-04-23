package edu.java.scrapper.domain.dao.jpa;

import edu.java.scrapper.domain.model.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaChatDao extends JpaRepository<Chat, Long> {
}
