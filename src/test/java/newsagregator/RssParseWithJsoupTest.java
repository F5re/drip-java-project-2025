package newsagregator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RssParseWithJsoupTest {
    private RssParseWithJsoup parser;

    @AfterEach
    void tearDown() {
        if (parser != null) {
            parser.shutdown();
        }
    }

    private String buildRssXml(int count) {
        StringBuilder sb = new StringBuilder();
        sb.append("<rss><channel>");
        for (int i = 1; i <= count; i++) {
            sb.append("<item>");
            sb.append("<title>Title-").append(i).append("</title>");
            sb.append("<link>http://example.com/").append(i).append("</link>");
            sb.append("<description>Desc-").append(i).append("</description>");
            sb.append("</item>");
        }
        sb.append("</channel></rss>");
        return sb.toString();
    }

    private Document parseStringToDocument(String xml) {
        return Jsoup.parse(xml, "", Parser.xmlParser());
    }

    @Test
    void parseReturnsArticlesForValidRss() throws IOException {
        String rssXml = buildRssXml(2);
        Document mockDoc = parseStringToDocument(rssXml);

        ContentParser contentParser = new ContentParser() {
            private int counter = 0;
            @Override
            public Article parseArticle(Element itemElement) {
                if (counter == 0) {
                    counter++;
                    return new Article.Builder()
                            .title("Title-1")
                            .link("http://example.com/1")
                            .build();
                }
                return new Article.Builder()
                        .title("Title-2")
                        .link("http://example.com/2")
                        .build();
            }
        };

        ParseMainText parseMainText = new ParseMainText(5) {
            @Override
            public String parseMainText(String url) {
                return "MainTextStub";
            }
        };
        parser = new RssParseWithJsoup(5, 2, contentParser, parseMainText) {
            @Override
            protected Document getDocument(String rssUrl) {
                return mockDoc;
            }
        };
        List<Article> result = parser.parse("http://любая-ссылка-не-важна");
        assertEquals(2, result.size());
        assertEquals("MainTextStub", result.get(0).getMainText());
        assertEquals("MainTextStub", result.get(1).getMainText());
        assertEquals("Title-1", result.get(0).getTitle());
        assertEquals("http://example.com/1", result.get(0).getLink());
        assertEquals("Title-2", result.get(1).getTitle());
        assertEquals("http://example.com/2", result.get(1).getLink());
    }

    @Test
    void parseSkipsDuplicateTitles() throws IOException {
        String xml = "<rss><channel>"
                + "<item><title>Same</title><link>http://example1.com</link></item>"
                + "<item><title>Same</title><link>http://example2.com</link></item>"
                + "</channel></rss>";
        Document mockDoc = parseStringToDocument(xml);
        ContentParser contentParser = new ContentParser() {
            private int count = 0;
            @Override
            public Article parseArticle(Element itemElement) {
                if (count == 0) {
                    count++;
                    return new Article.Builder()
                            .title("Same")
                            .link("http://example1.com")
                            .build();
                }
                return new Article.Builder()
                        .title("Same")
                        .link("http://example2.com")
                        .build();
            }
        };

        ParseMainText parseMainText = new ParseMainText(5) {
            @Override
            public String parseMainText(String url) {
                return "we dont need main text";
            }
        };

        parser = new RssParseWithJsoup(5, 2, contentParser, parseMainText) {
            @Override
            protected Document getDocument(String rssUrl) {
                return mockDoc;
            }
        };

        List<Article> result = parser.parse("ignored-url");
        assertEquals(1, result.size());
        assertEquals("Same", result.get(0).getTitle());
        assertEquals("http://example1.com", result.get(0).getLink());
    }

    @Test
    void parseSkipsArticlesWithoutLink() throws IOException {
        String xml = "<rss><channel>"
                + "<item><title>Only</title><description>no link</description></item>"
                + "</channel></rss>";
        Document mockDoc = parseStringToDocument(xml);
        ContentParser contentParser = new ContentParser() {
            @Override
            public Article parseArticle(Element itemElement) {
                return new Article.Builder()
                        .title("Only")
                        .link(null)
                        .build();
            }
        };
        ParseMainText parseMainText = new ParseMainText(5) {
            @Override
            public String parseMainText(String url) {
                return "never";
            }
        };

        parser = new RssParseWithJsoup(5, 2, contentParser, parseMainText) {
            @Override
            protected Document getDocument(String rssUrl) {
                return mockDoc;
            }
        };
        List<Article> result = parser.parse("ignored");
        assertTrue(result.isEmpty());
    }

    @Test
    void parseThrowsIOExceptionWhenFetchFails() {
        ContentParser contentParser = new ContentParser() {
            @Override
            public Article parseArticle(Element itemElement) {
                return null;
            }
        };
        ParseMainText parseMainText = new ParseMainText(5) {
            @Override
            public String parseMainText(String url) {
                return null;
            }
        };

        parser = new RssParseWithJsoup(5, 2, contentParser, parseMainText) {
            @Override
            protected Document getDocument(String rssUrl) throws IOException {
                throw new IOException("Simulated failure");
            }
        };
        assertThrows(IOException.class, () -> parser.parse("different-url"));
    }

    @Test
    void parseHandlesTimeoutInsideMainTextParsing() throws IOException {
        String xml = "<rss><channel>"
                + "<item><title>T</title><link>http://x</link></item>"
                + "</channel></rss>";
        Document mockDoc = parseStringToDocument(xml);
        ContentParser contentParser = new ContentParser() {
            @Override
            public Article parseArticle(Element itemElement) {
                return new Article.Builder()
                        .title("Title")
                        .link("http://example.com")
                        .build();
            }
        };
        ParseMainText slowParseMainText = new ParseMainText(1) {
            @Override
            public String parseMainText(String url) {
                try {
                    Thread.sleep(2_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "doesn't matter";
            }
        };
        parser = new RssParseWithJsoup(1, 1, contentParser, slowParseMainText) {
            @Override
            protected Document getDocument(String rssUrl) {
                return mockDoc;
            }
        };

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> parser.parse("ignored-url"),
                "Ожидаем RuntimeException при таймауте внутри waitForCompletion");
        assertTrue(ex.getMessage().contains("Ошибка при загрузке статьи"));
    }

    @Test
    void shutdownTerminatesExecutor() {
        ContentParser contentParser = new ContentParser() {
            @Override
            public Article parseArticle(Element itemElement) {
                return null;
            }
        };
        ParseMainText parseMainText = new ParseMainText(5) {
            @Override
            public String parseMainText(String url) {
                return null;
            }
        };

        parser = new RssParseWithJsoup(5, 2, contentParser, parseMainText);

        assertFalse(parser.getExecutor().isShutdown(), "До shutdown пул открыт");
        parser.shutdown();
        assertTrue(parser.getExecutor().isShutdown(), "После shutdown пул должен быть shutdown");
        assertTrue(parser.getExecutor().isTerminated(), "Пул должен завершиться после awaitTermination");
    }
}

