package edu.java.scrapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class MigrationTest extends IntegrationTest {
    @Test
    public void chatTableTest() throws SQLException {
        try (Connection connection = POSTGRES.createConnection("")) {
            PreparedStatement sqlQuery = connection.prepareStatement("SELECT * FROM public.chat");
            String actualResult = sqlQuery.executeQuery().getMetaData().getColumnName(1);
            assertThat(actualResult).isEqualTo("id");
        }
    }

    @Test
    public void linkTableTest() throws SQLException {
        try (Connection connection = POSTGRES.createConnection("");
             PreparedStatement sqlQuery = connection.prepareStatement("SELECT * FROM link")) {
            ResultSetMetaData result = sqlQuery.executeQuery().getMetaData();
            String firstColumn = result.getColumnName(1);
            String secondColumn = result.getColumnName(2);
            String thirdColumn = result.getColumnName(3);
            String fourthColumn = result.getColumnName(4);
            assertThat(firstColumn).isEqualTo("id");
            assertThat(secondColumn).isEqualTo("url");
            assertThat(thirdColumn).isEqualTo("last_update");
            assertThat(fourthColumn).isEqualTo("last_check");
        }

    }

    @Test
    public void connectionTableTest() throws SQLException {
        try (Connection connection = POSTGRES.createConnection("");
             PreparedStatement sqlQuery = connection.prepareStatement("SELECT * FROM chat_to_link_connection")) {
            ResultSetMetaData result = sqlQuery.executeQuery().getMetaData();
            String firstColumn = result.getColumnName(1);
            String secondColumn = result.getColumnName(2);
            assertThat(firstColumn).isEqualTo("chat_id");
            assertThat(secondColumn).isEqualTo("link_id");
        }
    }
}
