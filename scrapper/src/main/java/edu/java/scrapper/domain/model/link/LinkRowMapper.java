package edu.java.scrapper.domain.model.link;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class LinkRowMapper implements RowMapper<Link> {
    @Override
    @SuppressWarnings("MagicNumber")
    public Link mapRow(@NotNull ResultSet resultSet, int rowNum) throws SQLException {
        return new Link(
            resultSet.getLong(1),
            resultSet.getString(2),
            resultSet.getObject(3, OffsetDateTime.class),
            resultSet.getObject(4, OffsetDateTime.class)
        );
    }
}
