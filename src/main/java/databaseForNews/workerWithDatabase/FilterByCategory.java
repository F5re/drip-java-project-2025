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

public class FilterByCategory{
    private DataSource datasource;

    public FilterByCategory(DataSource datasource) {
        this.datasource = datasource;
    }

    public List<Article> filter(String category) throws SQLException{
        List<Article> result = new ArrayList<>();
        String sql = "SELECT * FROM articles WHERE category = ?";
        try (Connection connection = datasource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, category);
            ResultSet resultSet = preparedStatement.executeQuery();
            CreateArticle createArticle = new CreateArticle();
            while (resultSet.next()) {
                result.add(createArticle.create(resultSet));
            }
        }
        return result;
    }

}
