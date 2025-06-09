package database.article;

import databaseForNews.workerWithDatabase.NewsRefresh;
import newsagregator.Article;
import newsagregator.RssParseWithJsoup;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class NewsRefreshTest {

    private static DataSource ds;

    @BeforeAll
    static void inputDb() throws SQLException {
        JdbcDataSource h2ds = new JdbcDataSource();
        h2ds.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        h2ds.setUser("sa");
        h2ds.setPassword("");
        ds = h2ds;

        try (Connection conn = ds.getConnection();
             Statement st = conn.createStatement()) {
            st.execute("DROP TABLE IF EXISTS articles");
            st.execute("""
                CREATE TABLE articles (
                  id SERIAL PRIMARY KEY,
                  title TEXT,
                  link TEXT UNIQUE NOT NULL,
                  description TEXT,
                  public_date TIMESTAMP,
                  category TEXT,
                  main_text TEXT,
                  source TEXT
                )
            """);
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO articles " +
                            "(title, link, description, public_date, category, main_text, source) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                ps.setString(1, "Old News");
                ps.setString(2, "http://link1");
                ps.setString(3, "desc");
                ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now().minusDays(2)));
                ps.setString(5, "general");
                ps.setString(6, "oldcontent");
                ps.setString(7, "rbk");
                ps.executeUpdate();
            }
        }
    }

    @AfterAll
    static void tearDownDb() throws SQLException {
        try (Connection conn = ds.getConnection();
             Statement st = conn.createStatement()) {
            st.execute("DROP TABLE IF EXISTS articles");
        }
    }

    @Test
    void getNewArticles() throws Exception {
        Article old = new Article.Builder()
                .title("Old News")
                .link("http://link1")
                .description("desc")
                .releaseDay(LocalDateTime.now().minusDays(2))
                .category("general")
                .mainText("oldcontent")
                .source("SourceX")
                .build();
        Article fresh = new Article.Builder()
                .title("Fresh News")
                .link("http://fresh.news")
                .description("newdesc")
                .releaseDay(LocalDateTime.now())
                .category("updates")
                .mainText("newcontent")
                .source("SourceY")
                .build();

        RssParseWithJsoup parser = new RssParseWithJsoup(5, 2) {
            @Override
            public List<Article> parse(String rssUrl) {
                return List.of(old, fresh);
            }
        };
        NewsRefresh refresh = new NewsRefresh(ds, parser);
        List<Article> newOnes = refresh.getNewArticles(List.of("http://example.com/rss"));
        assertEquals(1, newOnes.size());
        assertEquals("http://fresh.news", newOnes.get(0).getLink());
        assertEquals("Fresh News", newOnes.get(0).getTitle());
    }

    @Test
    void getOldArticles() throws Exception {
        Article existing = new Article.Builder()
                .title("old News")
                .link("http://link1")
                .build();

        RssParseWithJsoup stubParser = new RssParseWithJsoup(5, 2) {
            @Override
            public List<Article> parse(String rssUrl) {
                return List.of(existing);
            }
        };

        NewsRefresh refresh = new NewsRefresh(ds, stubParser);
        List<Article> newOnes = refresh.getNewArticles(List.of("http://any.rss"));

        assertTrue(newOnes.isEmpty());
    }

    @Test
    void doNotGetNewArticle() throws Exception {
        RssParseWithJsoup stubParser = new RssParseWithJsoup(5, 2) {
            @Override
            public List<Article> parse(String rssUrl) {
                return List.of();
            }
        };

        NewsRefresh refresh = new NewsRefresh(ds, stubParser);
        List<Article> newOnes = refresh.getNewArticles(List.of("http://any.rss"));
        assertTrue(newOnes.isEmpty());
    }
}
