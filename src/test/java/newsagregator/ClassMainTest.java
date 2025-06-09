package newsagregator;

import databaseForNews.insertArticleDatabase.SaverArticle;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class MainTest {
    private static Article createSampleArticle() {
        return new Article.Builder()
                .title("T1")
                .link("L1")
                .description("D1")
                .build();
    }

    @Test
    void everythingSucceed() {
        RssParseWithJsoup stubParser = new RssParseWithJsoup(10, 1) {
            @Override
            public List<Article> parse(String url) {
                return List.of(createSampleArticle());
            }
        };
        SaverArticle stubSaver = new SaverArticle() {
            @Override
            public void saveArticleDatabase(Article article) {
            }
        };
        ExecutorService exec = Executors.newSingleThreadExecutor();

        List<String> errors = Main.processFeeds(List.of("http://example.com/rss"), stubParser, stubSaver, exec);
        assertTrue(errors.isEmpty());
    }

    @Test
    void whenParserThrows_thenParseError() {
        RssParseWithJsoup badParser = new RssParseWithJsoup(10, 1) {
            @Override
            public List<Article> parse(String url) throws IOException {
                throw new IOException("timeout");
            }
        };
        SaverArticle stubSaver = new SaverArticle() {
            @Override
            public void saveArticleDatabase(Article article) { }
        };
        ExecutorService exec = Executors.newSingleThreadExecutor();
        List<String> errors = Main.processFeeds(List.of("http://bad/rss"), badParser, stubSaver, exec);

        assertEquals(1, errors.size());
        assertTrue(errors.get(0).startsWith("parse_error: http://bad/rss"));
    }

    @Test
    void whenSaverThrows_thenSaveError() {
        RssParseWithJsoup stubParser = new RssParseWithJsoup(10, 1) {
            @Override
            public List<Article> parse(String url) throws IOException {
                return List.of(createSampleArticle());
            }
        };
        SaverArticle badSaver = new SaverArticle() {
            @Override
            public void saveArticleDatabase(Article article) throws SQLException {
                throw new SQLException("DB down");
            }
        };
        ExecutorService exec = Executors.newSingleThreadExecutor();

        List<String> errors = Main.processFeeds(List.of("http://ok/rss"), stubParser, badSaver, exec);

        assertEquals(1, errors.size());
        assertTrue(errors.get(0).startsWith("save_error: T1"));
    }

    @Test
    void testDomainExtraction() {
        RssParseWithJsoup stubParser = new RssParseWithJsoup(1,1) {
            @Override
            public List<Article> parse(String url) {
                return List.of(createSampleArticle());
            }
        };
        SaverArticle stubSaver = new SaverArticle() {
            @Override
            public void saveArticleDatabase(Article article) {
                assertEquals("lenta", article.getSource());
            }
        };
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Main.processFeeds(List.of("https://rss.lenta.ru"), stubParser, stubSaver, exec);
    }
}
