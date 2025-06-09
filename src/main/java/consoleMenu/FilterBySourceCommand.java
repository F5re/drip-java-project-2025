package consoleMenu;

import databaseForNews.workerWithDatabase.FilterBySource;
import newsagregator.Article;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class FilterBySourceCommand implements MenuCommand {
    private final FilterBySource filter;

    public FilterBySourceCommand(FilterBySource filter) {
        this.filter = filter;
    }

    @Override
    public void execute(Scanner scanner) throws SQLException {
        System.out.print("Введите название источника (например, rbc, lenta, ria): ");
        String source = scanner.nextLine().trim();
        List<Article> articles = filter.filter(source);
        if (articles.isEmpty()) {
            System.out.println("Нет статей для источника \"" + source + "\".\n");
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
        return "1) Фильтрация по источнику";
    }


}
