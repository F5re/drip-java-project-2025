package database.article;

import databaseForNews.workerWithDatabase.*;
import databaseForNews.ConnectToDatabase.DataSourceProvided;
import newsagregator.Article;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilterSearcherTest {

    private static DataSource testDataSource;
    private static CreateArticle createArticle;

    @BeforeAll
    static void setupDatabase() throws SQLException {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        ds.setUser("sa");
        ds.setPassword("");
        testDataSource = ds;
        createArticle = new CreateArticle();

        try (Connection conn = testDataSource.getConnection();
             Statement statement = conn.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS articles");
            statement.execute("""
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
            statement.execute("""
                INSERT INTO articles (title, link, description, public_date, category, main_text, source)
                VALUES
                  ('Title', 'http://example1', 'description1', TIMESTAMP '2020-01-01 00:00:00', 'it', 'content1', 'rbk')
            """);
            LocalDateTime now = LocalDateTime.now();
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO articles (title, link, description, public_date, category, main_text, source) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, "Sports News");
            ps.setString(2, "http://link2");
            ps.setString(3, "description2");
            ps.setTimestamp(4, Timestamp.valueOf(now));
            ps.setString(5, "sports");
            ps.setString(6, "content2");
            ps.setString(7, "BBC");
            ps.executeUpdate();

            LocalDateTime yesterday = now.minusDays(1);
            ps = conn.prepareStatement(
                    "INSERT INTO articles (title, link, description, public_date, category, main_text, source) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, "Yesterday News");
            ps.setString(2, "http://link3");
            ps.setString(3, "description3");
            ps.setTimestamp(4, Timestamp.valueOf(yesterday));
            ps.setString(5, "news");
            ps.setString(6, "content3");
            ps.setString(7, "BBC");
            ps.executeUpdate();

            DataSourceProvided.setTestDataSource(testDataSource);
        }
    }

    @Test
    void testFilterByDateEmptyResult() throws SQLException {
        FilterByDate filter = new FilterByDate(testDataSource);
        LocalDateTime from = LocalDateTime.now().plusDays(1);
        LocalDateTime to = LocalDateTime.now().plusDays(2);
        List<Article> articles = filter.filter(from, to);
        assertTrue(articles.isEmpty());
    }

    @Test
    void testFilterByDateSingleResult() throws SQLException {
        FilterByDate filter = new FilterByDate(testDataSource);
        LocalDateTime from = LocalDateTime.now().minusDays(1).minusHours(1);
        LocalDateTime to = LocalDateTime.now().minusDays(1).plusHours(1);
        List<Article> articles = filter.filter(from, to);
        assertEquals(1, articles.size());
        assertEquals("Yesterday News", articles.get(0).getTitle());
    }

    @Test
    void testFilterByDateNullInput() {
        FilterByDate filter = new FilterByDate(testDataSource);
        assertThrows(NullPointerException.class, () -> filter.filter(null, LocalDateTime.now()));
        assertThrows(NullPointerException.class, () -> filter.filter(LocalDateTime.now(), null));
    }

    @Test
    void testFilterBySourceTwoResults() throws SQLException {
        FilterBySource filter = new FilterBySource(testDataSource);
        List<Article> articles = filter.filter("BBC");
        assertEquals(2, articles.size());
        for (Article a : articles) {
            assertEquals("BBC", a.getSource());
        }
    }

    @Test
    void testFilterBySourceNoResults() throws SQLException {
        FilterBySource filter = new FilterBySource(testDataSource);
        List<Article> articles = filter.filter("CNN");
        assertTrue(articles.isEmpty());
    }

    @Test
    void testFilterBySourceNullInput() throws SQLException {
        FilterBySource filter = new FilterBySource(testDataSource);
        List<Article> articles = filter.filter(null);
        assertTrue(articles.isEmpty());
    }

    @Test
    void testFilterByCategorySqlInjectionSafe() throws SQLException {
        FilterByCategory filter = new FilterByCategory(testDataSource);
        String malicious = "sports' OR 1=1 --";
        List<Article> articles = filter.filter(malicious);
        assertTrue(articles.isEmpty());
    }

    @Test
    void testFilterByCategoryOneResult() throws SQLException {
        FilterByCategory filter = new FilterByCategory(testDataSource);
        List<Article> articles = filter.filter("sports");
        assertEquals(1, articles.size());
        assertEquals("sports", articles.get(0).getCategory());
    }


    @Test
    void testFilterByCategoryNullInput() throws SQLException {
        FilterByCategory filter = new FilterByCategory(testDataSource);
        List<Article> articles = filter.filter(null);
        assertTrue(articles.isEmpty());
    }

    @Test
    void testSearchByKeywordCaseInsensitive() throws SQLException {
        SearcherByKeywords search = new SearcherByKeywords(testDataSource);
        List<Article> articles = search.search("Title");
        assertFalse(articles.isEmpty());
        assertEquals("Title", articles.get(0).getTitle());
    }

    @Test
    void testSearchByKeywordInMainText() throws SQLException {
        SearcherByKeywords search = new SearcherByKeywords(testDataSource);
        List<Article> articles = search.search("CONTENT2".toLowerCase());
        assertEquals(1, articles.size());
        assertEquals("Sports News", articles.get(0).getTitle());
    }

    @Test
    void testSearchByKeywordNullInput() throws SQLException {
        SearcherByKeywords search = new SearcherByKeywords(testDataSource);
        List<Article> articles = search.search(null);
        assertTrue(articles.isEmpty());
    }

    @Test
    void testExceptionOnClosedConnection() {
        DataSource badDs = new DataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                Connection c = testDataSource.getConnection();
                c.close();
                return c;
            }

            @Override public Connection getConnection(String username, String password) throws SQLException { return getConnection(); }
            @Override public <T> T unwrap(Class<T> iface) { return null; }
            @Override public boolean isWrapperFor(Class<?> iface) { return false; }
            @Override public java.io.PrintWriter getLogWriter() { return null; }
            @Override public void setLogWriter(java.io.PrintWriter out) { }
            @Override public void setLoginTimeout(int seconds) { }
            @Override public int getLoginTimeout() { return 0; }
            @Override public java.util.logging.Logger getParentLogger() { return null; }
        };
        FilterBySource filter = new FilterBySource(badDs);
        SQLException ex = assertThrows(SQLException.class, () -> filter.filter("BBC"));
        assertTrue(ex.getMessage().toLowerCase().contains("closed"));
    }
}
