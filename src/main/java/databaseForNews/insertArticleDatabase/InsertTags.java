package databaseForNews.insertArticleDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class InsertTags {
    public void insertTags(Connection connection, Long articleId, List<String> tags) throws SQLException {
        if (tags == null || tags.isEmpty()) {
            return;
        }
        String sqlInsertTag = "INSERT INTO tags (name) VALUES (?) ON CONFLICT (name) DO NOTHING";
        String sqlGetTagId = "SELECT id FROM tags WHERE name = ?";
        String sqlLinkTag = "INSERT INTO article_tags (article_id, tag_id) VALUES (?,?) ON CONFLICT (article_id, tag_id) DO NOTHING";
        try (PreparedStatement psInsert = connection.prepareStatement(sqlInsertTag);
             PreparedStatement psGet = connection.prepareStatement(sqlGetTagId);
             PreparedStatement psLinkTag = connection.prepareStatement(sqlLinkTag)) {
            for (String tag : tags) {
                psInsert.setString(1, tag);
                psInsert.executeUpdate();
                psGet.setString(1, tag);
                try (ResultSet resultSet = psGet.executeQuery()) {
                    if (resultSet.next()) {
                        long tagId = resultSet.getLong("id");
                        psLinkTag.setLong(1, articleId);
                        psLinkTag.setLong(2, tagId);
                        psLinkTag.executeUpdate();
                    }
                }
            }
        }
    }
}
