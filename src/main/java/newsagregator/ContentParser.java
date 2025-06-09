package newsagregator;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ContentParser {
    public Article  parseArticle (Element item) throws IOException {
        String title = parseText(item, "title");
        String link = parseText(item, "link");
        String description = parseText(item, "description");
        String pubDay = parseText(item, "pubDate");
        ZonedDateTime releaseDay = ZonedDateTime.parse(pubDay, DateTimeFormatter.RFC_1123_DATE_TIME);
        String category = parseText(item, "category");
        List<String> imagesUrl = parseMediaUrls(item, "image/","enclosure");
        List<String> videoUrl = parseMediaUrls(item, "video/","enclosure");
        List<String> tags = parseTags(item, "tag", "rbc_news|tag");

        return new Article.Builder()
                .title(title)
                .link(link)
                .description(description)
                .releaseDay(releaseDay.toLocalDateTime())
                .category(category)
                .imageUrl(imagesUrl)
                .videoUrl(videoUrl)
                .tags(tags)
                .build()
        ;
    }

    private String parseText(Element item, String field) {
        Element elem = item.selectFirst(field);
        if (elem == null) {
            return null;
        }
        return elem.text().trim();
    }

    private List<String> parseTags(Element item, String... queries) {
        List<String> result = new ArrayList<>();
        for (String query : queries) {
            Elements elements = item.select(query);
            for (Element elem : elements) {
                String text = elem.text().trim();
                if (!text.isEmpty()) {
                    result.add(text);
                }
            }
        }
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    private List<String> parseMediaUrls(Element item, String typeOfUrl, String... queries) {
        List<String> result = new ArrayList<>();
        for (String query : queries) {
            Elements elements = item.select(query);
            for (Element elem : elements) {
                String type = elem.attr("type");
                if (elem.hasAttr("url") && type.startsWith(typeOfUrl)) {
                    result.add(elem.attr("url").trim());
                }
            }
        }
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }
} 