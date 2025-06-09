package consoleMenu;

import databaseForNews.workerWithDatabase.MovingNews;
import java.util.Scanner;

public class MovingNewsCommand implements MenuCommand {
    private final MovingNews movingNews;

    public MovingNewsCommand(MovingNews movingNews) {
        this.movingNews = movingNews;
    }

    @Override
    public void execute(Scanner scanner) {
        try {
            int count = movingNews.MovingNewsToOld();
            if (count > 0) {
                System.out.println("Перемещено в old_articles: " + count);
            } else {
                System.out.println("Устаревших статей нет.");
            }
        } catch (Exception e) {
            System.err.println("Ошибка архивации: " + e.getMessage());
        }
    }

    @Override
    public String description() {
        return "10) Архивировать старые новости (старше 30 дней)";
    }
}
