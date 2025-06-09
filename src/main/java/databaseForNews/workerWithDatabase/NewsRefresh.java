package databaseForNews.workerWithDatabase;

import newsagregator.Article;
import newsagregator.RssParseWithJsoup;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class NewsRefresh {
    private final DataSource ds;
    private final RssParseWithJsoup parser;

    public NewsRefresh(DataSource ds, RssParseWithJsoup parser) {
        this.ds = ds;
        this.parser = parser;
    }

    public List<Article> getNewArticles(List<String> rssUrls) throws Exception {
        Set<String> existingLinks = new HashSet<>();
        try (Connection conn = ds.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT link FROM articles")) {
            while (rs.next()) {
                existingLinks.add(rs.getString("link"));
            }
        }

        List<Article> newArticles = new ArrayList<>();
        for (String rssUrl : rssUrls) {
            List<Article> parsed = parser.parse(rssUrl);
            for (Article a : parsed) {
                if (!existingLinks.contains(a.getLink())) {
                    newArticles.add(a);
                }
            }
        }
        return newArticles;
    }
}
