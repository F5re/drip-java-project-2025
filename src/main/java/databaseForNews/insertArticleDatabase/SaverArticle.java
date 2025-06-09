package databaseForNews.insertArticleDatabase;

import databaseForNews.ConnectToDatabase.DataSourceProvided;
import newsagregator.Article;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class SaverArticle {
    private final DataSource dataSource;
    private final InsertStringFlelds InsertStringFlieds;
    private final InsertImages InsertImages;
    private final InsertVideo InsertVideo;
    private final InsertTags InsertTags;

    public SaverArticle() {
        this.dataSource = DataSourceProvided.getDataSource();
        this.InsertStringFlieds = new InsertStringFlelds(dataSource);
        this.InsertImages = new InsertImages();
        this.InsertVideo = new InsertVideo();
        this.InsertTags = new InsertTags();
    }

    public void saveArticleDatabase(Article article) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                Long articleId = InsertStringFlieds.insertStringFlieds(article, connection);
                if (articleId == null) {
                    connection.rollback();
                    return;
                }
                List<String> images = article.getImageUrl();
                List<String> videos = article.getVideoUrl();
                List<String> tags = article.getTags();

                InsertImages.insertImages(connection, articleId, images);
                InsertVideo.insertVideo(connection, articleId, videos);
                InsertTags.insertTags(connection, articleId, tags);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new SQLException("Произошла ошибка во время insert " + e);
            }
        }
    }
}