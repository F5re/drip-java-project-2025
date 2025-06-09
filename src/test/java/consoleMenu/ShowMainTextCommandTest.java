package consoleMenu;


import newsagregator.ParseMainText;
import org.junit.jupiter.api.*;
import java.io.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ShowMainTextCommandTest {
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outBuf;

    @BeforeEach
    void setUpStreams() {
        outBuf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBuf));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void testExecuteSuccess() throws Exception {
        ParseMainText stubParser = new ParseMainText(1) {
            @Override
            public String parseMainText(String link) {
                return "OK_TEXT";
            }
        };

        ShowMainTextCommand command = new ShowMainTextCommand(stubParser);
        String input = "http://example.com/article\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        command.execute(scanner);
        String out = outBuf.toString();
        assertTrue(out.contains("Введите URL статьи:"));
        assertTrue(out.contains("Полный текст статьи"));
        assertTrue(out.contains("OK_TEXT"));
    }

    @Test
    void testExecuteFailureMessage() throws Exception {
        ParseMainText noParser = new ParseMainText(1) {
            @Override
            public String parseMainText(String link) {
                return "Не удалось загрузить страницу";
            }
        };
        ShowMainTextCommand command = new ShowMainTextCommand(noParser);
        String input = "bad-url\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        command.execute(scanner);
        String out = outBuf.toString();
        assertTrue(out.contains("Введите URL статьи:"));
        assertTrue(out.contains("Не удалось загрузить страницу"));
    }
}

