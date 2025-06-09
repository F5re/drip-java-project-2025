package databaseForNews.workerWithDatabase;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;

public class MovingNews {
    private DataSource datasource;

    public MovingNews(DataSource datasource) {
        this.datasource = datasource;
    }

    public int MovingNewsToOld() throws SQLException {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Timestamp thirtyDays = Timestamp.valueOf(thirtyDaysAgo);
        String moveSql = """
                INSERT INTO old_articles (title, link, description, public_date, category, main_text, source)
                SELECT title, link, description, public_date, category, main_text, source
                FROM articles
                WHERE public_date < ?
    """;
        String deleteSql = """
                DELETE FROM articles
                WHERE public_date < ?
        """;
        try (Connection connection = datasource.getConnection()) {
            connection.setAutoCommit(false);
            int moved;
            try (PreparedStatement statementInsert = connection.prepareStatement(moveSql)) {
                statementInsert.setTimestamp(1, thirtyDays);
                moved = statementInsert.executeUpdate();
            }
            try (PreparedStatement statementDelete = connection.prepareStatement(deleteSql)) {
                statementDelete.setTimestamp(1, thirtyDays);
                statementDelete.executeUpdate();
            }
            connection.commit();
            return moved;
        }
    }
}
