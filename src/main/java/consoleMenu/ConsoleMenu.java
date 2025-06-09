package consoleMenu;

import databaseForNews.ConnectToDatabase.DataSourceProvided;
import databaseForNews.workerWithDatabase.*;
import newsagregator.ParseMainText;
import newsagregator.RssParseWithJsoup;
import java.io.PrintStream;
import java.sql.Connection;
import java.util.*;

public class ConsoleMenu {
    private final List<MenuEntry> commands = new ArrayList<>();
    private final PrintStream out;
    private final Scanner scanner;

    public ConsoleMenu(Scanner scanner, PrintStream out) {
        this.scanner = scanner;
        this.out = out;
    }

    public void register(String key, MenuCommand command) {
        commands.add(new MenuEntry(key, command));
    }

    public void run() throws Exception {
        while (true) {
            out.println("=== МЕНЮ ===");
            for (MenuEntry entry : commands) {
                out.println(entry.command.description());
            }
            out.print("Выберите пункт: ");

            if (!scanner.hasNextLine()) {
                out.println();
                break;
            }

            String choice = scanner.nextLine().trim();

            MenuEntry selectedEntry = null;
            for (MenuEntry entry : commands) {
                if (entry.key.equals(choice)) {
                    selectedEntry = entry;
                    break;
                }
            }

            if (selectedEntry == null) {
                out.println("Неверный выбор, повторите.\n");
                continue;
            }

            MenuCommand cmd = selectedEntry.command;
            cmd.execute(scanner);
            if (choice.equals("0")) {
                break;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        PrintStream out = System.out;

        var ds = DataSourceProvided.getDataSource();

        Connection conn = ds.getConnection();
        ConsoleMenu menu = new ConsoleMenu(scanner, out);

        menu.register("1", new FilterByDateCommand(new FilterByDate(ds)));
        menu.register("2", new FilterBySourceCommand(new FilterBySource(ds)));
        menu.register("3", new FilterByCategoryCommand(new FilterByCategory(ds)));
        menu.register("4", new SearchByKeywordCommand(new SearcherByKeywords(ds)));
        menu.register("5", new AnalyticsByCategoryCommand(new AnalyticsService(conn)));
        menu.register("6", new AnalyticsBySourceCommand(new AnalyticsService(conn)));
        menu.register("7", new LatestNewsCommand(new LatestNews(conn)));
        ParseMainText parseMainText = new ParseMainText(10);
        menu.register("8", new ShowMainTextCommand(parseMainText));

        RssParseWithJsoup rssParser = new RssParseWithJsoup(30, 3);
        NewsRefresh newsRefresh = new NewsRefresh(ds, rssParser);
        List<String> rssUrls = List.of(
                "https://rssexport.rbc.ru/rbcnews/news/30/full.rss",
                "https://lenta.ru/rss",
                "https://www.aljazeera.com/xml/rss/all.xml"
        );
        menu.register("9", new NewsRefreshCommand(newsRefresh, rssUrls));
        menu.register("10", new MovingNewsCommand(new MovingNews(ds)));
        menu.register("0", new ExitCommand());

        menu.run();
        conn.close();
        rssParser.shutdown();
    }

    private static class MenuEntry {
        String key;
        MenuCommand command;

        MenuEntry(String key, MenuCommand command) {
            this.key = key;
            this.command = command;
        }
    }
}

