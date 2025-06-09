package newsagregator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Article {
    private final String title;
    private final String link;
    private final String description;
    private final LocalDateTime releaseDay;
    private final String category;
    private final List<String> imageUrl;
    private final List<String> videoUrl;
    private final List<String> tags;
    private String mainText;
    private String source;

    private Article(Builder builder) {
        this.title = builder.title;
        this.link = builder.link;
        this.description = builder.description;
        this.releaseDay = builder.releaseDay;
        this.category = builder.category;
        this.imageUrl = builder.imageUrl;
        this.videoUrl = builder.videoUrl;
        this.tags = builder.tags;
        this.mainText = builder.mainText;
        this.source = builder.source;
    }

    public static class Builder {
        private String title;
        private String link;
        private String description;
        private LocalDateTime releaseDay;
        private String category;
        private List<String> imageUrl;
        private List<String> videoUrl;
        private List<String> tags;
        private String mainText;
        private String source;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder link(String link) {
            this.link = link;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder releaseDay(LocalDateTime releaseDay) {
            this.releaseDay = releaseDay;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder imageUrl(List<String> imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder videoUrl(List<String> videoUrl) {
            this.videoUrl = videoUrl;
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder mainText(String mainText) {
            this.mainText = mainText;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public Article build() {
            return new Article(this);
        }
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    public String getTitle() {
        return title;
    }

    public String getLink () {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getReleaseDay() {
        return releaseDay;
    }

    public String getCategory() {
        return category;
    }

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public List<String> getVideoUrl() {
        return videoUrl;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getMainText(){ return mainText; }

    public String getSource(){
        return source;
    }

    @Override
    public String toString() {
        return "Title: " + title + "\n" + "Link: " + link + "\n"
                + "Description: " + description + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Article article = (Article) o;
        return Objects.equals(title, article.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
