package newsagregator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class RssParseWithJsoup implements RssParser {
    private final ExecutorService executor;
    private final int timeOutSeconds;
    private final ContentParser contentParser;
    private final ParseMainText parserMainText;
    private final int threadPoolSize;

    public RssParseWithJsoup(int timeOutSeconds, int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
        this.timeOutSeconds = timeOutSeconds;
        this.contentParser = new ContentParser();
        this.parserMainText = new ParseMainText(timeOutSeconds);
    }

    //конструктор для тестов
    public RssParseWithJsoup(int timeOutSeconds, int threadPoolSize, ContentParser contentParser, ParseMainText parserMainText) {
        this.threadPoolSize = threadPoolSize;
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
        this.timeOutSeconds = timeOutSeconds;
        this.contentParser = contentParser;
        this.parserMainText = parserMainText;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    @Override
    public List<Article> parse(String rssUrl) throws IOException {
        Set<String> equalNews = new HashSet<>();
        List<Article> articles = new ArrayList<>();
        List<Future<?>> futures = new ArrayList<>();

        Document document = getDocument(rssUrl);

        Elements items = document.select("item");
        for (Element item : items) {
            Article article = contentParser.parseArticle(item);
            if (article.getTitle() == null || equalNews.contains(article.getTitle())) {
                continue;
            }
            equalNews.add(article.getTitle());
            if (article.getLink() == null) {
                continue;
            }
            futures.add(executor.submit(() -> {
                String mainText = parserMainText.parseMainText(article.getLink());
                article.setMainText(mainText);
            }));
            articles.add(article);
            if (articles.size() > 5) {
                break;
            }
        }
        waitForCompletion(futures);
        return articles;
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    protected Document getDocument(String rssUrl) throws IOException {
        return Jsoup.connect(rssUrl)
                .ignoreContentType(true)
                .timeout(timeOutSeconds * 1000)
                .get();
    }

    private void waitForCompletion(List<Future<?>> futures) {
        for (Future<?> future : futures) {
            try {
                future.get(timeOutSeconds, TimeUnit.SECONDS);
            } catch (InterruptedException | TimeoutException | ExecutionException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Ошибка при загрузке статьи: " + e.getMessage(), e);
            }
        }
    }
}
