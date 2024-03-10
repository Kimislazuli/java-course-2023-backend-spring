package edu.java.scrapper;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        try (Connection connection = POSTGRES.createConnection("")) {
            PreparedStatement sqlQuery = connection.prepareStatement("SELECT * FROM link");
            String firstColumn = sqlQuery.executeQuery().getMetaData().getColumnName(1);
            String secondColumn = sqlQuery.executeQuery().getMetaData().getColumnName(2);
            String thirdColumn = sqlQuery.executeQuery().getMetaData().getColumnName(3);
            String fourthColumn = sqlQuery.executeQuery().getMetaData().getColumnName(4);
            assertThat(firstColumn).isEqualTo("id");
            assertThat(secondColumn).isEqualTo("url");
            assertThat(thirdColumn).isEqualTo("last_update");
            assertThat(fourthColumn).isEqualTo("last_check");
        }
    }

    @Test
    public void connectionTableTest() throws SQLException {
        try (Connection connection = POSTGRES.createConnection("")) {
            PreparedStatement sqlQuery = connection.prepareStatement("SELECT * FROM chat_to_link_connection");
            String firstColumn = sqlQuery.executeQuery().getMetaData().getColumnName(1);
            String secondColumn = sqlQuery.executeQuery().getMetaData().getColumnName(2);
            assertThat(firstColumn).isEqualTo("chat_id");
            assertThat(secondColumn).isEqualTo("link_id");
        }
    }
}
