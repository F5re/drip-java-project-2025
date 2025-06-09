package database.article;

import databaseForNews.workerWithDatabase.AnalyticsService;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AnalyticsServiceTest {

    private DataSource testDataSource;
    private AnalyticsService analyticsService;
    private Connection sharedConnection;

    @BeforeEach
    void setupDatabase() throws SQLException {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        ds.setUser("sa");
        ds.setPassword("");
        testDataSource = ds;
        sharedConnection = testDataSource.getConnection();
        sharedConnection.setAutoCommit(false);
        analyticsService = new AnalyticsService(sharedConnection);

        try (Statement stmt = sharedConnection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS articles");
            stmt.execute("""
                CREATE TABLE articles (
                    id SERIAL PRIMARY KEY,
                    title VARCHAR(255),
                    link VARCHAR(255),
                    description TEXT,
                    public_date TIMESTAMP,
                    category VARCHAR(100),
                    main_text TEXT,
                    source VARCHAR(100)
                )
            """);
            stmt.execute("""
                INSERT INTO articles (title, link, description, public_date, category, main_text, source)
                VALUES
                  ('Title 1', 'http://example1', 'description1', TIMESTAMP '2020-01-01 00:00:00', 'it', 'content1', 'rbk'),
                  ('Sports News', 'http://link2', 'description2', CURRENT_TIMESTAMP, 'sports', 'content2', 'BBC'),
                  ('Yesterday News', 'http://link3', 'description3', TIMESTAMPADD('DAY', -1, CURRENT_TIMESTAMP), 'news', 'content3', 'BBC')
            """);
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (sharedConnection != null) {
            sharedConnection.rollback();
            sharedConnection.close();
        }
    }

    @Test
    void countByCategory_shouldReturnCorrectCounts() throws SQLException {
        Map<String, Integer> result = analyticsService.countByCategory();

        assertEquals(3, result.size());
        assertEquals(1, result.get("it"));
        assertEquals(1, result.get("sports"));
        assertEquals(1, result.get("news"));
    }

    @Test
    void countBySource_shouldReturnCorrectCounts() throws SQLException {
        Map<String, Integer> result = analyticsService.countBySource();

        assertEquals(2, result.size());
        assertEquals(1, result.get("rbk"));
        assertEquals(2, result.get("BBC"));
    }

    @Test
    void countBySource_shouldHandleNullValues() throws SQLException {
        try (PreparedStatement ps = sharedConnection.prepareStatement(
                "INSERT INTO articles (title, link, source) VALUES (?, ?, ?)")) {
            ps.setString(1, "Null Source Article");
            ps.setString(2, "https://example.com/null");
            ps.setNull(3, Types.VARCHAR);
            ps.executeUpdate();
        }
        sharedConnection.commit();

        Map<String, Integer> result = analyticsService.countBySource();

        assertEquals(3, result.size());
        assertTrue(result.containsKey(null));
        assertEquals(1, result.get(null));
    }

    @Test
    void countByCategory_shouldHandleEmptyTable() throws SQLException {
        try (Statement stmt = sharedConnection.createStatement()) {
            stmt.execute("DELETE FROM articles");
        }
        sharedConnection.commit(); // Фиксируем изменения

        Map<String, Integer> result = analyticsService.countByCategory();
        assertTrue(result.isEmpty());
    }
}