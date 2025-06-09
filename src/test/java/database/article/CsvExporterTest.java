package database.article;

import databaseForNews.workerWithDatabase.CsvExporter;
import newsagregator.Article;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CsvExporterTest {

    @Test
    void testCsvExport() {
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

        String csv = CsvExporter.export(articles);
        assertTrue(csv.contains("Title,Link,Description"));
        assertTrue(csv.contains("Title1"));
        assertTrue(csv.contains("http://link1"));
    }
}
