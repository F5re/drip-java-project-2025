package consoleMenu;

import databaseForNews.workerWithDatabase.NewsRefresh;
import newsagregator.Article;
import newsagregator.RssParseWithJsoup;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NewsRefreshCommandTest {
    private static DataSource ds;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outBuf;

    @BeforeEach
    void setUpStreams() {
        outBuf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBuf));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @BeforeAll
    static void setupDb() throws SQLException {
        JdbcDataSource h2 = new JdbcDataSource();
        h2.setURL("jdbc:h2:mem:testcmd;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        h2.setUser("sa");
        h2.setPassword("");
        ds = h2;

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
                    "INSERT INTO articles (title, link, description, public_date, category, main_text, source) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                ps.setString(1, "Old news");
                ps.setString(2, "http://link1");
                ps.setString(3, "desc");
                ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
                ps.setString(5, "cat");
                ps.setString(6, "txt");
                ps.setString(7, "src");
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
    void testNewsRefresh() {
        Article old = new Article.Builder()
                .title("Old news")
                .link("http://link1")
                .build();

        Article fresh = new Article.Builder()
                .title("Fresh news")
                .link("http://link2")
                .description("desc")
                .releaseDay(LocalDateTime.now())
                .category("updates")
                .mainText("txt")
                .source("src")
                .build();

        RssParseWithJsoup parser = new RssParseWithJsoup(5, 1) {
            @Override
            public List<Article> parse(String rssUrl) {
                return List.of(old, fresh);
            }
        };

        NewsRefresh refresh = new NewsRefresh(ds, parser);
        NewsRefreshCommand command = new NewsRefreshCommand(refresh, List.of("http://test.rss"));
        String input = "";
        Scanner scanner = new Scanner(input);
        command.execute(scanner);
        String out = outBuf.toString();
        assertTrue(out.contains("Найдено новых статей"));
        assertTrue(out.contains("Fresh news"));
        assertTrue(out.contains("http://link2"));
    }

    @Test
    void testOldNews() {
        Article old = new Article.Builder()
                .title("Old news")
                .link("http://link1")
                .build();
        RssParseWithJsoup parser = new RssParseWithJsoup(5, 1) {
            @Override
            public List<Article> parse(String rssUrl) {
                return List.of(old);
            }
        };

        NewsRefresh refresh = new NewsRefresh(ds, parser);
        NewsRefreshCommand command = new NewsRefreshCommand(refresh, List.of("http://test.rss"));
        String input = "";
        Scanner scanner = new Scanner(input);
        command.execute(scanner);
        String out = outBuf.toString();
        assertTrue(out.contains("Нет новых новостей"));
    }

}
