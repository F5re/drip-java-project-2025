package consoleMenu;

import newsagregator.ParseMainText;

import java.util.Scanner;

public class ShowMainTextCommand implements MenuCommand {
    private final ParseMainText parse;

    public ShowMainTextCommand(ParseMainText parse) {
        this.parse = parse;
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.print("Введите URL статьи: ");
        String link = scanner.nextLine().trim();
        String mainText = parse.parseMainText(link);
        System.out.println("\nПолный текст статьи");
        mainText = comfortableToReadText(mainText);
        System.out.println(mainText);
        System.out.println();
    }

    @Override
    public String description() {
        return "8) показать полный текст по ссылке";
    }

    private String comfortableToReadText(String text) {
        int count = 0;
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            result.append(c);
            count++;
            if (c == ' ' && count > 160) {
                result.append('\n');
                count = 0;
            }
        }
        return result.toString();
    }
}
