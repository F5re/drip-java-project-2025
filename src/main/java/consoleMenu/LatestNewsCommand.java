package consoleMenu;

import databaseForNews.workerWithDatabase.LatestNews;
import newsagregator.Article;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import databaseForNews.workerWithDatabase.CsvExporter;
import databaseForNews.workerWithDatabase.HtmlExporter;
import databaseForNews.workerWithDatabase.JsonExporter;

public class LatestNewsCommand implements MenuCommand {
    private final LatestNews latestNews;

    public LatestNewsCommand(LatestNews latestNews) {
        this.latestNews = latestNews;
    }

    @Override
    public void execute(Scanner scanner) throws SQLException {
        System.out.println("Выберите сортировку:");
        System.out.println("1) По дате");
        System.out.println("2) По источнику");
        System.out.print("Ваш выбор [1–2]: ");
        String choice = scanner.nextLine().trim();
        String sortBy = "public_date";
        if ("2".equals(choice)) {
            sortBy = "source";
        }
        List<Article> news = latestNews.takeLatestNews(sortBy);
        if (news.isEmpty()) {
            System.out.println("Нет новостей по этой сортировке\n");
            return;
        }
        System.out.println("Выберите формат вывода:");
        System.out.println("1) Обычный");
        System.out.println("2) CSV");
        System.out.println("3) JSON");
        System.out.println("4) HTML");
        System.out.print("Ваш выбор [1–4]: ");
        String format = scanner.nextLine().trim();

        switch (format) {
            case "2" -> System.out.println(CsvExporter.export(news));
            case "3" -> System.out.println(JsonExporter.export(news));
            case "4" -> System.out.println(HtmlExporter.export(news));
            default -> {
                for (Article article : news) {
                    System.out.println(article);
                }
            }
        }
        System.out.println();
    }

    @Override
    public String description() {
        return "7) Отображение последних новостей";
    }
}


