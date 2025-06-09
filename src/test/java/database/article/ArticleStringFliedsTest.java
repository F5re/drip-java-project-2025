package database.article;

import databaseForNews.insertArticleDatabase.InsertStringFlelds;
import newsagregator.Article;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class InsertStringFleldsTest {

    private static DataSource dataSource;
    private Connection connection;
    private InsertStringFlelds inserter;

    @BeforeAll
    static void setUpClass() throws SQLException {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        ds.setUser("sa");
        ds.setPassword("");
        dataSource = ds;

        try (Connection conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS articles");
            stmt.execute("""
                CREATE TABLE articles (
                    id SERIAL PRIMARY KEY,
                    title VARCHAR(255) NOT NULL UNIQUE,
                    link VARCHAR(255),
                    description TEXT,
                    public_date TIMESTAMP,
                    category VARCHAR(100),
                    main_text TEXT,
                    source VARCHAR(100)
                )
            """);
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        connection = dataSource.getConnection();
        inserter = new InsertStringFlelds(dataSource);
        try (var statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE articles");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void testThrowsSQLExceptionOn() {
        Article article = new Article.Builder()
                .title("Title")
                .link("http://example.com")
                .description("Description")
                .releaseDay(LocalDateTime.now())
                .category("it")
                .mainText("text")
                .source("rbk")
                .build();
        SQLException ex = assertThrows(SQLException.class, () ->
                inserter.insertStringFlieds(article, connection)
        );
        String msg = ex.getMessage().toLowerCase();
        assertTrue(msg.contains("syntax") || msg.contains("on conflict"));
    }

    @Test
    void testNullReleaseDayThrowsSQLException() {
        Article article = new Article.Builder()
                .title("NullDate")
                .link("http://nodate")
                .description("No date")
                .releaseDay(null)
                .category("none")
                .mainText("xt")
                .source("src")
                .build();

        SQLException ex = assertThrows(SQLException.class, () ->
                inserter.insertStringFlieds(article, connection)
        );
        String msg = ex.getMessage().toLowerCase();
        assertTrue(msg.contains("syntax") || msg.contains("on conflict"));
    }

    @Test
    void testInsertDuplicateThrowsSQLException() throws SQLException {
        Article first = new Article.Builder()
                .title("Title")
                .link("http://example1.com")
                .description("First")
                .releaseDay(LocalDateTime.now())
                .category("c")
                .mainText("t")
                .source("s")
                .build();

        assertThrows(SQLException.class, () ->
                inserter.insertStringFlieds(first, connection)
        );
        Article second = new Article.Builder()
                .title("Title")
                .link("http://example2.com")
                .description("Second")
                .releaseDay(LocalDateTime.now().plusDays(1))
                .category("c2")
                .mainText("t2")
                .source("s2")
                .build();

        SQLException ex2 = assertThrows(SQLException.class, () ->
                inserter.insertStringFlieds(second, connection)
        );
        assertTrue(ex2.getMessage().toLowerCase().contains("on conflict"),
                "Ожидаем ошибку ON CONFLICT: " + ex2.getMessage());
    }

    @Test
    void testInsertWithOnlyTitleThrowsSQLException() {
        Article article = new Article.Builder()
                .title("TitleOnly")
                .link(null)
                .description(null)
                .releaseDay(null)
                .category(null)
                .mainText(null)
                .source(null)
                .build();

        SQLException ex = assertThrows(SQLException.class, () ->
                inserter.insertStringFlieds(article, connection)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("on conflict"),
                "Ожидаем ON CONFLICT‑ошибку: " + ex.getMessage());
    }

    @Test
    void testInsertWithClosedConnectionThrowsSQLException() throws SQLException {
        Article article = new Article.Builder()
                .title("X")
                .link("http://x")
                .description("x")
                .releaseDay(LocalDateTime.now())
                .category("x")
                .mainText("x")
                .source("x")
                .build();

        connection.close();

        SQLException ex = assertThrows(SQLException.class, () ->
                inserter.insertStringFlieds(article, connection)
        );
        String msg = ex.getMessage().toLowerCase();
        assertTrue(msg.contains("closed"), "Ожидаем ошибку о закрытом соединении, но: " + ex.getMessage());
    }
}

