package newsagregator;

import java.io.IOException;
import java.util.List;

public interface RssParser {
    List<Article> parse(String rssUrl) throws IOException;
}