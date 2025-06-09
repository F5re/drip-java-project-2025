package consoleMenu;

import databaseForNews.workerWithDatabase.LatestNews;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;


class LatestNewsCommandTest {

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

    private LatestNewsCommand makeCommandWithSampleData() throws SQLException {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");

        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS articles");
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS articles (
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
                VALUES ('TestTitle','http://test','Desc',TIMESTAMP '2025-01-01 10:00:00','cat','text','rbk')
            """);
        }

        return new LatestNewsCommand(new LatestNews(ds.getConnection()));
    }

    @Test
    void testExecuteJsonFormat() throws Exception {
        LatestNewsCommand command = makeCommandWithSampleData();
        String input = "1\n3\n";
        Scanner scanner = new Scanner(input);
        command.execute(scanner);
        String output = outBuf.toString();

        assertTrue(output.contains("  {\n"));
    }

    @Test
    void testExecuteCsvFormat() throws Exception {
        LatestNewsCommand command = makeCommandWithSampleData();
        String input = "1\n2\n";
        Scanner scanner = new Scanner(input);
        command.execute(scanner);
        String output = outBuf.toString();

        assertTrue(output.contains("TestTitle,http://test,Desc"));
    }

    @Test
    void testExecuteHtmlFormat() throws Exception {
        LatestNewsCommand command = makeCommandWithSampleData();
        String input = "1\n4\n";
        Scanner scanner = new Scanner(input);
        command.execute(scanner);
        String output = outBuf.toString();

        assertTrue(output.contains("<html>") || output.contains("<tr>"));
    }

    @Test
    void testExecutePlainFormat() throws Exception {
        LatestNewsCommand command = makeCommandWithSampleData();
        String input = "1\nplain\n";
        Scanner scanner = new Scanner(input);
        command.execute(scanner);
        String output = outBuf.toString();

        assertTrue(output.contains("TestTitle"), "Обычный формат должен содержать заголовок статьи");
    }
}



