package newsagregator;

import databaseForNews.insertArticleDatabase.SaverArticle;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        List<String> rssUrls = List.of(
                "https://rssexport.rbc.ru/rbcnews/news/30/full.rss",
                "https://lenta.ru/rss",
                "https://www.aljazeera.com/xml/rss/all.xml"
        );

        int threadPoolSize = rssUrls.size();
        RssParseWithJsoup rssParser = new RssParseWithJsoup(30, threadPoolSize);
        SaverArticle saverArticle = new SaverArticle();

        ExecutorService parsingExecutor = Executors.newFixedThreadPool(threadPoolSize);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            List<String> errors = processFeeds(rssUrls, rssParser, saverArticle, parsingExecutor);
            for (String e : errors) {
                System.err.println(e);
            }
            if (errors.isEmpty()) {
                System.out.println("Done");
            }
        };

        scheduler.scheduleAtFixedRate(task, 0, 10, TimeUnit.MINUTES);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down scheduler and parser...");
            scheduler.shutdownNow();
            parsingExecutor.shutdownNow();
            rssParser.shutdown();
        }));
    }

    protected static List<String> processFeeds(List<String> rssUrls, RssParseWithJsoup parser, SaverArticle saver, ExecutorService executor) {
        List<Future<String>> futures = new ArrayList<>();

        for (String rssUrl : rssUrls) {
            futures.add(executor.submit(() -> {
                try {
                    List<Article> articles = parser.parse(rssUrl);
                    String host = new URI(rssUrl).getHost();
                    String[] parts = host.split("\\.");
                    String source = host;
                    if (parts.length >= 2) {
                        source = parts[parts.length - 2];
                    }
                    for (Article a : articles) {
                        a.setSource(source);
                        try {
                            saver.saveArticleDatabase(a);
                        } catch (SQLException ex) {
                            return "save_error: " + a.getTitle() + " " + ex.getMessage();
                        }
                    }
                    return null;
                } catch (IOException ex) {
                    return "parse_error: " + rssUrl + " " + ex.getMessage();
                }
            }));
        }

        List<String> errors = new ArrayList<>();
        for (Future<String> f : futures) {
            try {
                String error = f.get();
                if (error != null) {
                    errors.add(error);
                }
            } catch (Exception ex) {
                errors.add("executor_error: " + ex.getMessage());
            }
        }
        return errors;
    }
}

