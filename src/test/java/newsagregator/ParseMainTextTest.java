package newsagregator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ParseMainTextTest {

    @Test
    public void testValidLentaArticle() {
        ParseMainText parser = new ParseMainText(50);
        String link = "https://www.rbc.ru/sport/01/06/2025/683c753c9a7947fdd1f86dbd";
        String result = parser.parseMainText(link);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.startsWith("В первом тайме арбитры VAR более шести минут проверяли гол Облякова"));
    }

    @Test
    public void testInvalidUrl() {
        ParseMainText parser = new ParseMainText(5);
        String result = parser.parseMainText("ht!tp:/invalid-url");
        assertEquals("Не удалось загрузить страницу", result);
    }

    @Test
    public void testNonArticlePage() {
        ParseMainText parser = new ParseMainText(10);
        String result = parser.parseMainText("https://www.google.com");
        assertEquals("Основной код не найден", result);
    }
}