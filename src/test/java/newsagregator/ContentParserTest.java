package newsagregator;

import newsagregator.Article;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContentParserTest {

    @InjectMocks
    private ContentParser contentParser;

    private Element mockItem;
    private Document testDocument;

    @BeforeEach
    void setUp() {
        String xml = "<item>" +
                "<title>Test Title</title>" +
                "<link>https://example.com</link>" +
                "<description>Test Description</description>" +
                "<pubDate>Sun, 01 Jun 2025 15:27:14 +0300</pubDate>" +
                "<category>Test Category</category>" +
                "<enclosure type=\"image/jpeg\" url=\"img1.jpg\"/>" +
                "<enclosure type=\"video/mp4\" url=\"video1.mp4\"/>" +
                "<tag>Tag1</tag><tag>Tag2</tag>" +
                "</item>";

        testDocument = Jsoup.parse(xml, "", Parser.xmlParser());
        mockItem = testDocument.selectFirst("item");
        contentParser = new ContentParser();
    }

    @Test
    void testCorrectArticle() throws IOException {
        Article article = contentParser.parseArticle(mockItem);

        assertNotNull(article);
        assertEquals("Test Title", article.getTitle());
        assertEquals("https://example.com", article.getLink());
        assertEquals("Test Description", article.getDescription());
        LocalDateTime expectedDate = ZonedDateTime.parse(
                "Sun, 01 Jun 2025 15:27:14 +0300",
                DateTimeFormatter.RFC_1123_DATE_TIME
        ).toLocalDateTime();
        assertEquals(expectedDate, article.getReleaseDay());
        assertEquals("Test Category", article.getCategory());
        assertIterableEquals(List.of("img1.jpg"), article.getImageUrl());
        assertIterableEquals(List.of("video1.mp4"), article.getVideoUrl());
        assertIterableEquals(List.of("Tag1", "Tag2"), article.getTags());
    }

    @Test
    void testNullFields() throws IOException {
        testDocument.select("title, link, description").remove();

        Article article = contentParser.parseArticle(mockItem);

        assertNull(article.getTitle());
        assertNull(article.getLink());
        assertNull(article.getDescription());
        LocalDateTime expectedDate = ZonedDateTime.parse(
                "Sun, 01 Jun 2025 15:27:14 +0300",
                DateTimeFormatter.RFC_1123_DATE_TIME
        ).toLocalDateTime();
        assertEquals(expectedDate, article.getReleaseDay());
    }
}
