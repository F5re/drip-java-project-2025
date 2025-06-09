package newsagregator;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArticleTest {
    private final String title = "Test Title";
    private final String link = "http://test.com";
    private final String description = "Test Description";
    private final LocalDateTime releaseDay = LocalDateTime.of(2025, 6, 1, 15, 27);
    private final String category = "Test Category";
    private final List<String> imageUrls = Arrays.asList("http://test.com/image1.jpg", "http://test.com/image2.jpg");
    private final List<String> videoUrls = Arrays.asList("http://test.com/video1.mp4", "http://test.com/video2.mp4");
    private final List<String> tags = Arrays.asList("tag1", "tag2", "tag3");

    @Test
    void testBuilder() {
        Article article = new Article.Builder()
                .title(title)
                .link(link)
                .description(description)
                .releaseDay(releaseDay)
                .category(category)
                .imageUrl(imageUrls)
                .videoUrl(videoUrls)
                .tags(tags)
                .build();

        assertEquals(title, article.getTitle());
        assertEquals(link, article.getLink());
        assertEquals(description, article.getDescription());
        assertEquals(releaseDay, article.getReleaseDay());
        assertEquals(category, article.getCategory());
        assertEquals(imageUrls, article.getImageUrl());
        assertEquals(videoUrls, article.getVideoUrl());
        assertEquals(tags, article.getTags());
        assertNull(article.getMainText());
    }

    @Test
    void testSetMainText() {
        Article article = new Article.Builder()
                .title(title)
                .link(link)
                .description(description)
                .releaseDay(releaseDay)
                .category(category)
                .imageUrl(imageUrls)
                .videoUrl(videoUrls)
                .tags(tags)
                .build();
        
        String mainText = "Test Main Text";
        article.setMainText(mainText);
        
        assertEquals(mainText, article.getMainText());
    }

    @Test
    void testSameTitle_ReturnsTrue() {
        Article article1 = new Article.Builder()
                .title(title)
                .link(link)
                .description(description)
                .releaseDay(releaseDay)
                .category(category)
                .imageUrl(imageUrls)
                .videoUrl(videoUrls)
                .tags(tags)
                .build();

        Article article2 = new Article.Builder()
                .title(title)
                .link("different-link")
                .description("different-description")
                .releaseDay(LocalDateTime.of(2025, 5, 1, 15, 27))
                .category("different-category")
                .imageUrl(Collections.emptyList())
                .videoUrl(Collections.emptyList())
                .tags(Collections.emptyList())
                .build();
        
        assertTrue(article1.equals(article2));
    }

    @Test
    void testDifferentTitle_ReturnsFalse() {
        Article article1 = new Article.Builder()
                .title(title)
                .link(link)
                .description(description)
                .releaseDay(releaseDay)
                .category(category)
                .imageUrl(imageUrls)
                .videoUrl(videoUrls)
                .tags(tags)
                .build();

        Article article2 = new Article.Builder()
                .title("Different Title")
                .link(link)
                .description(description)
                .releaseDay(releaseDay)
                .category(category)
                .imageUrl(imageUrls)
                .videoUrl(videoUrls)
                .tags(tags)
                .build();
        
        assertFalse(article1.equals(article2));
    }

    @Test
    void testNullObject_ReturnsFalse() {
        Article article = new Article.Builder()
                .title(title)
                .link(link)
                .description(description)
                .releaseDay(releaseDay)
                .category(category)
                .imageUrl(imageUrls)
                .videoUrl(videoUrls)
                .tags(tags)
                .build();
        
        assertFalse(article.equals(null));
    }

    @Test
    void testHashCode() {
        Article article1 = new Article.Builder()
                .title(title)
                .link(link)
                .description(description)
                .releaseDay(releaseDay)
                .category(category)
                .imageUrl(imageUrls)
                .videoUrl(videoUrls)
                .tags(tags)
                .build();

        Article article2 = new Article.Builder()
                .title(title)
                .link("different-link")
                .description("different-description")
                .releaseDay(LocalDateTime.of(2025, 5, 1, 15, 27))
                .category("different-category")
                .imageUrl(Collections.emptyList())
                .videoUrl(Collections.emptyList())
                .tags(Collections.emptyList())
                .build();
        
        assertEquals(article1.hashCode(), article2.hashCode());
    }

    @Test
    void testToString() {
        Article article = new Article.Builder()
                .title(title)
                .link(link)
                .description(description)
                .releaseDay(releaseDay)
                .category(category)
                .imageUrl(imageUrls)
                .videoUrl(videoUrls)
                .tags(tags)
                .build();
        
        String mainText = "Test Main Text";
        article.setMainText(mainText);
        
        String toString = article.toString();
        
        assertTrue(toString.contains(title));
        assertTrue(toString.contains(link));
        assertTrue(toString.contains(description));
    }
}