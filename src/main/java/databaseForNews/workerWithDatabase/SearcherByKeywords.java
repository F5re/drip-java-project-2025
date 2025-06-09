package databaseForNews.workerWithDatabase;

import databaseForNews.ConnectToDatabase.DataSourceProvided;
import newsagregator.Article;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearcherByKeywords {
    private DataSource dataSource;

    public SearcherByKeywords(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Article> search(String keyword) throws SQLException {
        List<Article> result = new ArrayList<>();
        String sql = "SELECT * FROM articles where title ILIKE ? or main_text ILIKE ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            String query = "%" + keyword + "%";
            preparedStatement.setString(1, query);
            preparedStatement.setString(2, query);
            ResultSet resultSet = preparedStatement.executeQuery();
            CreateArticle createArticle = new CreateArticle();
            while (resultSet.next()) {
                result.add(createArticle.create(resultSet));
            }
        }
        return result;
    }
}
