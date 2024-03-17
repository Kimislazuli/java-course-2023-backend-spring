package edu.java.scrapper.domain.model.connection;

import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ChatToLinkConnectionRowMapper implements RowMapper<ChatToLinkConnection> {

    @Override
    public ChatToLinkConnection mapRow(@NotNull ResultSet resultSet, int rowNum) throws SQLException {
        return new ChatToLinkConnection(
            resultSet.getLong(1),
            resultSet.getLong(2)
        );
    }
}
