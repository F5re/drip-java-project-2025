package databaseForNews.insertArticleDatabase;

import newsagregator.Article;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;

public class InsertStringFlelds {
    private final DataSource dataSource;

    public InsertStringFlelds(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Long insertStringFlieds(Article article, Connection connection) throws SQLException {
        String sqlStatement = """
                        INSERT INTO articles (title, link, description, public_date, category, main_text, source)
                        VALUES (?,?,?,?,?,?,?)
                        ON CONFLICT (title) DO NOTHING
                        RETURNING ID
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            String articleTitle = article.getTitle();
            String articleLink = article.getLink();
            String articleDescription = article.getDescription();
            String articleCategory = article.getCategory();
            LocalDateTime articleReleaseDay = article.getReleaseDay();
            String articleMainText = article.getMainText();
            String articleSource = article.getSource();

            preparedStatement.setString(1, articleTitle);
            preparedStatement.setString(2, articleLink);
            preparedStatement.setString(3, articleDescription);
            if (articleReleaseDay != null) {
                preparedStatement.setTimestamp(4, Timestamp.valueOf(articleReleaseDay));
            } else {
                preparedStatement.setNull(4, Types.TIMESTAMP);
            }
            preparedStatement.setString(5, articleCategory);
            preparedStatement.setString(6, articleMainText);
            preparedStatement.setString(7, articleSource);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("id");
                }
                return null;
            }
        }
    }
}
