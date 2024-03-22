package edu.java.scrapper.domain.model.connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

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
