package consoleMenu;

import databaseForNews.workerWithDatabase.FilterByCategory;
import newsagregator.Article;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class FilterByCategoryCommand implements MenuCommand {
    private FilterByCategory filter;

    public FilterByCategoryCommand(FilterByCategory filter) {
        this.filter = filter;
    }

    @Override
    public void execute(Scanner scanner) throws SQLException {
        System.out.print("Введите категорию (например, politics, sports, news): ");
        String category = scanner.nextLine().trim();

        List<Article> articles = filter.filter(category);
        if (articles.isEmpty()) {
            System.out.println("Нет статей в категории \"" + category + "\".\n");
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
        return "3) Фильтрация по категории";
    }
}
