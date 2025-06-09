package database.article;

import databaseForNews.workerWithDatabase.HtmlExporter;
import newsagregator.Article;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HtmlExporterTest {
    @Test
    void testHtmlExport() {
        List<Article> articles = List.of(
                new Article.Builder()
                        .title("Title1")
                        .link("http://link1")
                        .description("description")
                        .releaseDay(LocalDateTime.of(2025, 6, 7, 13, 39))
                        .category("category1")
                        .source("source1")
                        .build()
        );

        String html = HtmlExporter.export(articles);
        assertTrue(html.contains("<html>"));
        assertTrue(html.contains("<title>Последние новости</title>"));
        assertTrue(html.contains("Title1"));
        assertTrue(html.contains("http://link1"));
    }
}
