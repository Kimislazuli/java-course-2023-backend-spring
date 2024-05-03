package edu.java.scrapper.domain.model.chat;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class ChatRowMapper implements RowMapper<Chat> {
    @Override
    public Chat mapRow(@NotNull ResultSet resultSet, int rowNum) throws SQLException {
        return new Chat(resultSet.getLong(1), resultSet.getShort(2));
    }
}
