package consoleMenu;

import databaseForNews.workerWithDatabase.FilterByDate;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class FilterByDateCommandTest {
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

    private FilterByDateCommand makeCommandWithSampleData() throws SQLException {
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
                VALUES ('TestTitle','http://test','Desc',TIMESTAMP '2025-01-01 10:00:00','cat','text','src')
            """);
        }
        return new FilterByDateCommand(new FilterByDate(ds));
    }

    @Test
    void testExecuteFoundOne() throws SQLException {
        FilterByDateCommand command = makeCommandWithSampleData();
        String input = """
            2025-01-01T00:00
            2025-01-02T00:00
            """;
        Scanner scanner = new Scanner(input);
        command.execute(scanner);
        String out = outBuf.toString();
        assertTrue(out.contains("Найдено 1 статей"));
        assertTrue(out.contains("TestTitle"));
    }

    @Test
    void testExecuteNoResults() throws SQLException {
        FilterByDateCommand command = makeCommandWithSampleData();
        String input = """
            2024-12-01T00:00
            2024-12-31T23:59
            """;
        Scanner scanner = new Scanner(input);
        command.execute(scanner);
        String out = outBuf.toString();
        assertTrue(out.contains("Нет статей в диапазоне"));
    }
}


