package databaseForNews.workerWithDatabase;
import newsagregator.Article;
import java.util.List;

public class HtmlExporter {
    public static String export(List<Article> articles) {
        StringBuilder html = new StringBuilder();
        html.append("""
                    <html>
                    <head>
                        <title>Последние новости</title>
                        <meta charset="UTF-8">
                    </head>
                    <body>
                        <h1>Последние новости</h1>
                """);

        for (Article article : articles) {
            html.append("<div class=\"article\">\n")
                    .append("<h2>").append(article.getTitle()).append("</h2>\n")
                    .append("<p><a href=\\").append(article.getLink()).append("</a></p>\\n")
                    .append("<p>").append(article.getDescription()).append("</p>\n")
                    .append("</div>\n");
        }
        html.append("""
                    </body>
                    </html>
                """);
        return html.toString();
    }
}
