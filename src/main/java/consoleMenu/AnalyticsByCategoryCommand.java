package consoleMenu;

import databaseForNews.workerWithDatabase.AnalyticsService;

import java.sql.SQLException;
import java.util.Scanner;

public class AnalyticsByCategoryCommand implements MenuCommand {
    private final AnalyticsService analyticsService;

    public AnalyticsByCategoryCommand(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @Override
    public void execute(Scanner scanner) throws SQLException {
        var countsByCategory = analyticsService.countByCategory();
        System.out.println("Количество статей по категориям:");
        countsByCategory.forEach((category, count) ->
                System.out.println(" • " + category + ": " + count)
        );
        System.out.println();
    }

    @Override
    public String description() {

        return "5) Аналитика: число статей по категориям\"";
    }
}
