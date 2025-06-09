package consoleMenu;

import databaseForNews.workerWithDatabase.FilterBySource;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilterBySourceCommandTest {
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

    private FilterBySourceCommand makeCommandWithSampleData() throws SQLException {
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
        return new FilterBySourceCommand(new FilterBySource(ds));
    }

    @Test
    void testExecuteFoundOne() throws SQLException {
        FilterBySourceCommand command = makeCommandWithSampleData();
        String input = "rbk";
        Scanner scanner = new Scanner(input);
        command.execute(scanner);
        String out = outBuf.toString();
        assertTrue(out.contains("Найдено 1 статей"));
        assertTrue(out.contains("TestTitle"));
    }

    @Test
    void testExecuteNoResults() throws SQLException {
        FilterBySourceCommand command = makeCommandWithSampleData();
        String input = "dog";
        Scanner scanner = new Scanner(input);
        command.execute(scanner);
        String out = outBuf.toString();
        assertTrue(out.contains("Нет статей для источника"));
    }
}
