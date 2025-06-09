package databaseForNews.workerWithDatabase;

import newsagregator.Article;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LatestNews {
    private final Connection connection;

    public LatestNews(Connection connection) {
        this.connection = connection;
    }

    public List<Article> takeLatestNews(String sortBy) throws SQLException {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT title, link, description, source FROM articles ORDER BY " + sortBy + " DESC LIMIT 10";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Article article = new Article.Builder()
                        .title(resultSet.getString("title"))
                        .link(resultSet.getString("link"))
                        .description(resultSet.getString("description"))
                        .build();
                articles.add(article);
            }
        }
        return articles;
    }
}
