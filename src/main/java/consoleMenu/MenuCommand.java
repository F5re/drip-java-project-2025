package consoleMenu;

import java.util.Scanner;

public interface MenuCommand {
    void execute(Scanner scanner) throws Exception;
    String description();
}
