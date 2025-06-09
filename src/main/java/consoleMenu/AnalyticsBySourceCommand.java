package consoleMenu;

import databaseForNews.workerWithDatabase.AnalyticsService;

import java.sql.SQLException;
import java.util.Scanner;

public class AnalyticsBySourceCommand implements MenuCommand {
    private final AnalyticsService analyticsService;

    public AnalyticsBySourceCommand(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @Override
    public void execute(Scanner scanner) throws SQLException {
        var countsBySource = analyticsService.countBySource();
        System.out.println("Количество статей по источникам:");
        countsBySource.forEach((source, count) ->
                System.out.println(" • " + source + ": " + count)
        );
        System.out.println();
    }

    @Override
    public String description() {
        return "6) Аналитика: число статей по источникам\"";
    }
}
