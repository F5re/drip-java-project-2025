package consoleMenu;

import newsagregator.Article;
import databaseForNews.workerWithDatabase.FilterByDate;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class FilterByDateCommand implements MenuCommand {
    private final FilterByDate filter;

    public FilterByDateCommand(FilterByDate filter) {
        this.filter = filter;
    }

    @Override
    public void execute(Scanner scanner) throws SQLException {
        System.out.print("Введите дату начала (YYYY-MM-DDTHH:MM): ");
        LocalDateTime from = LocalDateTime.parse(scanner.nextLine().trim());
        System.out.print("Введите дату окончания (YYYY-MM-DDTHH:MM): ");
        LocalDateTime to = LocalDateTime.parse(scanner.nextLine().trim());

        List<Article> list = filter.filter(from, to);
        if (list.isEmpty()) {
            System.out.println("Нет статей в диапазоне.");
        } else {
            System.out.println("Найдено " + list.size() + " статей:");
            for (Article a : list) {
                System.out.println(a);
            }
        }
        System.out.println();
    }

    @Override
    public String description() {
        return "1) Фильтрация по дате";
    }
}
