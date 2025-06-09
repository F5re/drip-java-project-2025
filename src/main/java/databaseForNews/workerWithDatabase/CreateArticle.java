package databaseForNews.workerWithDatabase;

import newsagregator.Article;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateArticle {
    public Article create(ResultSet resultSet) throws SQLException {
        return new Article.Builder()
                .title(resultSet.getString("title"))
                .link(resultSet.getString("link"))
                .description(resultSet.getString("description"))
                .releaseDay(resultSet.getTimestamp("public_date").toLocalDateTime())
                .category(resultSet.getString("category"))
                .mainText(resultSet.getString("main_text"))
                .source(resultSet.getString("source"))
                .build();
    }
}
