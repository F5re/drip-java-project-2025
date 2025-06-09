package databaseForNews.workerWithDatabase;

import newsagregator.Article;

import java.util.List;

public class JsonExporter {
    public static String export(List<Article> articles) {
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            json.append("  {\n");
            json.append("    \"title\": \"").append(escape(article.getTitle())).append("\",\n");
            json.append("    \"link\": \"").append(escape(article.getLink())).append("\",\n");
            json.append("    \"description\": \"").append(escape(article.getDescription())).append("\"\n");
            json.append("  }");
            if (i < articles.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("]");
        return json.toString();
    }

    private static String escape(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }
}

