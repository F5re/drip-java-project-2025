package database.article;

import databaseForNews.workerWithDatabase.MovingNews;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MovingNewsTest {
    private static DataSource ds;

    @BeforeAll
    static void inputDb() {
        JdbcDataSource h2 = new JdbcDataSource();
        h2.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        h2.setUser("sa");
        h2.setPassword("");
        ds = h2;
    }

    @BeforeEach
    void setUp() throws SQLException {
        try (Connection conn = ds.getConnection();
             Statement st = conn.createStatement()) {
            st.execute("DROP TABLE IF EXISTS articles");
            st.execute("DROP TABLE IF EXISTS old_articles");
            st.execute("""
                CREATE TABLE old_articles (
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
                ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now().minusDays(40)));
                ps.setString(5, "general");
                ps.setString(6, "oldcontent");
                ps.setString(7, "rbk");
                ps.executeUpdate();
            }
        }
    }

    @Test
    void testMoving() throws SQLException {
        MovingNews moveNews = new MovingNews(ds);
        int moved = moveNews.MovingNewsToOld();
        assertEquals(1, moved);
        try (Connection conn = ds.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM articles");
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                assertEquals(0, rs.getInt(1));
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM old_articles");
                 ResultSet rs = ps.executeQuery()) {
                  rs.next();
                  assertEquals(1, rs.getInt(1));
            }
        }
    }

    @Test
    void testNoMoving() throws SQLException {
        MovingNews mover = new MovingNews(ds);
        int movedFirst = mover.MovingNewsToOld();
        int movedSecond = mover.MovingNewsToOld();
        assertEquals(0, movedSecond);
    }
}
