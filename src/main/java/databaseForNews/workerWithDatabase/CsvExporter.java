package databaseForNews.workerWithDatabase;

import newsagregator.Article;

import java.util.List;

public class CsvExporter {
    public static String export(List<Article> articles) {
        StringBuilder sb = new StringBuilder("Title,Link,Description\n");
        for (Article article : articles) {
            sb.append(article.getTitle()).append(",")
                    .append(article.getLink()).append(",")
                    .append(article.getDescription()).append("\n");
        }
        return sb.toString();
    }
}
