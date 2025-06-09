package databaseForNews.workerWithDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsService {
    private final Connection conn;

    public AnalyticsService(Connection conn) {
        this.conn = conn;
    }

    public Map<String, Integer> countByCategory() throws SQLException {
        String sql = "SELECT category, COUNT(*) AS count FROM articles GROUP BY category";
        Map<String, Integer> result = new HashMap<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString("category"), rs.getInt("count"));
            }
        }
        return result;
    }

    public Map<String, Integer> countBySource() throws SQLException {
        String sql = "SELECT source, COUNT(*) AS cnt FROM articles GROUP BY source";
        Map<String, Integer> result = new HashMap<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString("source"), rs.getInt("cnt"));
            }
        }
        return result;
    }
}
