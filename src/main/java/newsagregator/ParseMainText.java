package newsagregator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ParseMainText {
    private int timeOutSeconds;

    public ParseMainText(int timeOutSeconds) {
        this.timeOutSeconds = timeOutSeconds;
    }

    public String parseMainText(String link) {
        try {
            Document doc = Jsoup.connect(link)
                    .timeout(timeOutSeconds * 1000)
                    .get();
            Elements content = doc.select("div.article__text, article, div.content, div.entry-content, div.text, div.post-content, div.topic-body__content, div.js-topic__text");
            if (content.isEmpty()) {
                return "Основной код не найден";
            }
            return content.text().trim();
        } catch(IOException | IllegalArgumentException e) {
            return "Не удалось загрузить страницу";
        }
    }
}
