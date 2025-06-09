package consoleMenu;

import databaseForNews.workerWithDatabase.MovingNews;
import org.junit.jupiter.api.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import org.h2.jdbcx.JdbcDataSource;
import java.sql.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MovingNewsCommandTest {
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

    private MovingNewsCommand makeCommandWithSampleData() throws SQLException {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");

        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS articles");
            stmt.execute("DROP TABLE IF EXISTS old_articles");

            stmt.execute("""
                CREATE TABLE articles (
                  id SERIAL PRIMARY KEY,
                  title VARCHAR(255) UNIQUE,
                  link VARCHAR(255),
                  description TEXT,
                  public_date TIMESTAMP,
                  category VARCHAR(100),
                  main_text TEXT,
                  source VARCHAR(100)
                )
            """);

            stmt.execute("""
                CREATE TABLE old_articles (
                  id SERIAL PRIMARY KEY,
                  title VARCHAR(255) UNIQUE,
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
                VALUES ('Old News', 'http://link1', 'desc', TIMESTAMP '2024-01-01 10:00:00', 'cat', 'text', 'rbk')
            """);
        }

        return new MovingNewsCommand(new MovingNews(ds));
    }

    @Test
    void testExecuteMovesOldArticles() throws SQLException {
        MovingNewsCommand command = makeCommandWithSampleData();
        String input = "";
        Scanner scanner = new Scanner(input);
        command.execute(scanner);

        String output = outBuf.toString();
        assertTrue(output.contains("Перемещено в old_articles: 1"));
    }

    @Test
    void testExecuteNoOldArticles() throws SQLException {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb2;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");

        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS articles");
            stmt.execute("DROP TABLE IF EXISTS old_articles");

            stmt.execute("""
                CREATE TABLE articles (
                  id SERIAL PRIMARY KEY,
                  title VARCHAR(255) UNIQUE,
                  link VARCHAR(255),
                  description TEXT,
                  public_date TIMESTAMP,
                  category VARCHAR(100),
                  main_text TEXT,
                  source VARCHAR(100)
                )
            """);

            stmt.execute("""
                CREATE TABLE old_articles (
                  id SERIAL PRIMARY KEY,
                  title VARCHAR(255) UNIQUE,
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
                VALUES ('Fresh News', 'http://fresh', 'desc', CURRENT_TIMESTAMP, 'cat', 'text', 'rbk')
            """);
        }
        MovingNewsCommand command = new MovingNewsCommand(new MovingNews(ds));
        String input = "";
        Scanner scanner = new Scanner(input);
        command.execute(scanner);
        String output = outBuf.toString();
        assertTrue(output.contains("Устаревших статей нет."));
    }
}


