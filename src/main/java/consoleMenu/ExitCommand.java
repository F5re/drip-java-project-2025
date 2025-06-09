package consoleMenu;

import java.util.Scanner;

public class ExitCommand implements MenuCommand {
    @Override
    public void execute(Scanner scanner) {
        System.out.println("Выход из приложения");
    }

    @Override
    public String description() {
        return "0) выход";
    }
}
