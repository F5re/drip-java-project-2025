package database.article;

import databaseForNews.workerWithDatabase.LatestNews;
import newsagregator.Article;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.*;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LatestNewsTest {
    private DataSource testDataSource;
    private Connection connection;
    private LatestNews latestNews;

    @BeforeEach
    void setup() throws SQLException {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        ds.setUser("sa");
        ds.setPassword("");
        testDataSource = ds;
        connection = testDataSource.getConnection();
        connection.setAutoCommit(false);
        latestNews = new LatestNews(connection);

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS articles");
            stmt.execute("""
                CREATE TABLE articles (
                    id SERIAL PRIMARY KEY,
                    title VARCHAR(255),
                    link VARCHAR(255),
                    description TEXT,
                    public_date TIMESTAMP,
                    source VARCHAR(100)
                )
            """);

            stmt.execute("""
                INSERT INTO articles (title, link, description, public_date, source)
                VALUES 
                    ('Title 1', 'http://link1', 'desc 1', CURRENT_TIMESTAMP, 'BBC'),
                    ('Title 2', 'http://link2', 'desc 2', TIMESTAMP '2020-01-01 00:00:00', 'CNN'),
                    ('Title 3', 'http://link3', 'desc 3', TIMESTAMP '2021-01-01 00:00:00', 'BBC')
            """);
        }
    }

    @AfterEach
    void rollback() throws SQLException {
        connection.rollback();
        connection.close();
    }

    @Test
    void testSortByDate() throws SQLException {
        List<Article> articles = latestNews.takeLatestNews("public_date");

        assertEquals(3, articles.size());
        assertEquals("Title 1", articles.get(0).getTitle());
        assertEquals("Title 3", articles.get(1).getTitle());
        assertEquals("Title 2", articles.get(2).getTitle());
    }

    @Test
    void testSortBySource() throws SQLException {
        List<Article> articles = latestNews.takeLatestNews("source");

        assertEquals(3, articles.size());
        assertEquals("Title 2", articles.get(0).getTitle());
        assertEquals("Title 1", articles.get(1).getTitle());
    }

    @Test
    void testEmptyTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM articles");
        }
        connection.commit();
        List<Article> articles = latestNews.takeLatestNews("public_date");
        assertTrue(articles.isEmpty());
    }

    @Test
    void testInvalidSortField() {
        assertThrows(SQLException.class, () -> latestNews.takeLatestNews("no_column"));
    }
}
