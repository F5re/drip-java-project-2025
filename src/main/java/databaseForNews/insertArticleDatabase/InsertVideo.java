package databaseForNews.insertArticleDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class InsertVideo {
    public void insertVideo(Connection connection, Long articleId, List<String> videosUrl) throws SQLException {
        if (videosUrl == null || videosUrl.isEmpty()) {
            return;
        }
        String sqlStatement = "INSERT INTO article_videos (article_id, url) VALUES (?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement) ) {
            for (String Url : videosUrl) {
                preparedStatement.setLong(1, articleId);
                preparedStatement.setString(2, Url);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }
    }
}
