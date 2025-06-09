package consoleMenu;

import databaseForNews.workerWithDatabase.NewsRefresh;
import newsagregator.Article;

import java.util.List;
import java.util.Scanner;

public class NewsRefreshCommand implements MenuCommand {
    private final NewsRefresh service;
    private final List<String> urls;

    public NewsRefreshCommand(NewsRefresh service, List<String> urls) {
        this.service = service;
        this.urls = urls;
    }

    @Override
    public void execute(Scanner scanner)  {
        try {
            List<Article> newArticles = service.getNewArticles(urls);
            if (newArticles.size() > 0) {
                System.out.println("Найдено новых статей: " + newArticles.size());
                for (Article article : newArticles) {
                    System.out.println(article.toString());
                }
            } else {
                System.out.println("Нет новых новостей");
            }
        } catch (Exception e) {
            System.err.println("Ошибка при обновлении новостей: " + e.getMessage());
        }
    }

    @Override
    public String description() {
        return "9) обновить список новостей вручную";
    }
}
