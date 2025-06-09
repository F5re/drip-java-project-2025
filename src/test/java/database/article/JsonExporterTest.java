package database.article;

import databaseForNews.workerWithDatabase.JsonExporter;
import newsagregator.Article;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonExporterTest {
    @Test
    void testJsonExport() {
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

        String json = JsonExporter.export(articles);
        assertTrue(json.contains("[\n"));
        assertTrue(json.contains("Title1"));
        assertTrue(json.contains("http://link1"));
        assertTrue(json.contains(","));
    }

    @Test
    void testJsonExportNull() {
        List<Article> articles = new ArrayList<>();
        String json = JsonExporter.export(articles);
        assertTrue(json.contains(""));
    }
}
