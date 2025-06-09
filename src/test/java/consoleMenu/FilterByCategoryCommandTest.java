package consoleMenu;

import databaseForNews.workerWithDatabase.FilterByCategory;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class FilterByCategoryCommandTest {
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

    private FilterByCategoryCommand makeCommandWithSampleData() throws SQLException {
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
        return new FilterByCategoryCommand(new FilterByCategory(ds));
    }

    @Test
    void testExecuteFoundOne() throws SQLException {
        FilterByCategoryCommand command = makeCommandWithSampleData();
        String input = "cat";
        Scanner scanner = new Scanner(input);
        command.execute(scanner);
        String out = outBuf.toString();
        assertTrue(out.contains("Найдено 1 статей"));
        assertTrue(out.contains("TestTitle"));
    }

    @Test
    void testExecuteNoResults() throws SQLException {
        FilterByCategoryCommand command = makeCommandWithSampleData();
        String input = "dog";
        Scanner scanner = new Scanner(input);
        command.execute(scanner);
        String out = outBuf.toString();
        assertTrue(out.contains("Нет статей в категории"));
    }
}
