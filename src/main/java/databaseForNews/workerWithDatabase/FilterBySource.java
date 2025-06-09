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

public class FilterBySource{
    private final DataSource dataSource;
    private final CreateArticle createArticle;

    public FilterBySource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.createArticle = new CreateArticle();
    }
    public List<Article> filter(String source) throws SQLException {
        List<Article> result = new ArrayList<>();
        String sql = "SELECT * FROM articles WHERE source = ?";
        try (Connection connection = dataSource.getConnection();
              PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, source);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(createArticle.create(resultSet));
            }
        }
        return result;
    }
}
