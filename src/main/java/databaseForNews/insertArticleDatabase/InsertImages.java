package databaseForNews.insertArticleDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class InsertImages {
    public void insertImages(Connection connection, Long articleId, List<String> imagesUrl) throws SQLException {
        if (imagesUrl == null || imagesUrl.isEmpty()) {
            return;
        }
        String sqlStatement = "INSERT INTO article_images (article_id, url) VALUES (?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            for (String Url : imagesUrl) {
                preparedStatement.setLong(1, articleId);
                preparedStatement.setString(2, Url);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }
    }
}
