package consoleMenu;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

class ExitCommandTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outBuf;

    @BeforeEach
    void setUp() {
        outBuf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBuf));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testExitCommandPrintsBye() {
        Scanner scanner = new Scanner("");
        ExitCommand exit = new ExitCommand();
        exit.execute(scanner);

        String out = outBuf.toString().trim();
        assertEquals("Выход из приложения", out);
    }
}

class ConsoleMenuTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outBuf;

    @BeforeEach
    void redirectOutput() {
        outBuf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBuf));
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOut);
    }

    @Test
    void testMenuExit() throws Exception {
        String userInput = "0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(userInput.getBytes()));
        ConsoleMenu menu = new ConsoleMenu(scanner, System.out);
        menu.register("0", new ExitCommand());
        menu.run();

        String output = outBuf.toString();
        assertTrue(output.contains("=== МЕНЮ ==="));
        assertTrue(output.contains("Выберите пункт:"));
        assertTrue(output.contains("Выход из приложения"));
    }

    @Test
    void testInvalidThenExit() throws Exception {
        String userInput = "50\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(userInput.getBytes()));

        ConsoleMenu menu = new ConsoleMenu(scanner, System.out);
        menu.register("0", new ExitCommand());

        menu.run();
        String output = outBuf.toString();

        assertTrue(output.contains("Неверный выбор"));
        assertTrue(output.contains("Выход из приложения"));
    }
}
