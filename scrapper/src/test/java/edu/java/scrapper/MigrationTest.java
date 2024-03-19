package edu.java.scrapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class MigrationTest extends IntegrationTest {
    @Test
    public void chatTableTest() throws SQLException {
        try (Connection connection = POSTGRES.createConnection("");
             PreparedStatement sqlQuery = connection.prepareStatement("SELECT * FROM public.chat");
             ResultSet result = sqlQuery.executeQuery()) {
            String actualResult = result.getMetaData().getColumnName(1);
            assertThat(actualResult).isEqualTo("id");
        }
    }

    @Test
    public void linkTableTest() throws SQLException {
        try (Connection connection = POSTGRES.createConnection("");
             PreparedStatement sqlQuery = connection.prepareStatement("SELECT * FROM link");
             ResultSet result = sqlQuery.executeQuery()) {
            String firstColumn = result.getMetaData().getColumnName(1);
            String secondColumn = result.getMetaData().getColumnName(2);
            String thirdColumn = result.getMetaData().getColumnName(3);
            String fourthColumn = result.getMetaData().getColumnName(4);
            assertThat(firstColumn).isEqualTo("id");
            assertThat(secondColumn).isEqualTo("url");
            assertThat(thirdColumn).isEqualTo("last_update");
            assertThat(fourthColumn).isEqualTo("last_check");
        }
    }

    @Test
    public void connectionTableTest() throws SQLException {
        try (Connection connection = POSTGRES.createConnection("");
             PreparedStatement sqlQuery = connection.prepareStatement("SELECT * FROM chat_to_link_connection");
             ResultSet result = sqlQuery.executeQuery()) {
            String firstColumn = result.getMetaData().getColumnName(1);
            String secondColumn = result.getMetaData().getColumnName(2);
            assertThat(firstColumn).isEqualTo("chat_id");
            assertThat(secondColumn).isEqualTo("link_id");
        }
    }
}
