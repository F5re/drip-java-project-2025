package consoleMenu;

import databaseForNews.workerWithDatabase.SearcherByKeywords;
import newsagregator.Article;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class SearchByKeywordCommand implements MenuCommand {
    private final SearcherByKeywords searcher;

    public SearchByKeywordCommand(SearcherByKeywords searcher) {
        this.searcher = searcher;
    }

    @Override
    public void execute(Scanner scanner) throws SQLException {
        System.out.print("Введите ключевое слово для поиска: ");
        String keyword = scanner.nextLine().trim();
        List<Article> articles = searcher.search(keyword);
        if (articles.isEmpty()) {
            System.out.println("По запросу \"" + keyword + "\" ничего не найдено.\n");
        } else {
            System.out.println("Найдено " + articles.size() + " статей:");
            for (Article article : articles) {
                System.out.println(article.toString());
            }
            System.out.println();
        }
    }

    @Override
    public String description() {
        return "4) Поиск по ключевому слову";
    }
}
