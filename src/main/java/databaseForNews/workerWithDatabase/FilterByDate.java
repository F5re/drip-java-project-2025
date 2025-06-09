package databaseForNews.workerWithDatabase;

import databaseForNews.ConnectToDatabase.DataSourceProvided;
import newsagregator.Article;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FilterByDate{
    private final DataSource dataSource;

    public FilterByDate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Article> filter(LocalDateTime from, LocalDateTime to) throws SQLException{
        List<Article> result = new ArrayList<>();
        String sql = "SELECT * FROM articles WHERE public_date BETWEEN ? AND ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(from));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(to));
            ResultSet resultSet = preparedStatement.executeQuery();
            CreateArticle createArticle = new CreateArticle();
            while (resultSet.next()) {
                result.add(createArticle.create(resultSet));
            }
        }
        return result;
    }
}
