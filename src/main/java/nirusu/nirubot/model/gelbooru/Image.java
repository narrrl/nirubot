package nirusu.nirubot.model.gelbooru;

import com.google.gson.annotations.SerializedName;

import java.util.Set;

public class Image {
    private String source;
    private String tags;
    @SerializedName("file_url")
    private String url;
    private String rating;
    private Long id;

    public String getSource() {
        return source;
    }

    public String[] getTags() {
        return tags.split(" ");
    }

    public String getUrl() {
        return url;
    }

    public Rating getRating() {
        return Rating.of(rating);
    }

    public boolean hasTag(String tag) {
        return Set.of(getTags()).contains(tag);
    }

    public String getPostUrl() {
        return String.format("https://gelbooru.com/index.php?page=post&s=view&id=%d", id);
    }

    public enum Rating {
        SAFE("s"), QUESTIONABLE("q"), EXPLICIT("e"), UNKNOWN("u");

        private final String imageRating;

        Rating(final String rating) {
            this.imageRating = rating;
        }

        @Override
        public String toString() {
            return "rating:" + this.name().toLowerCase();
        }

        public static Rating of(String rating) {
            for (Rating r : Rating.values()) {
                if (r.imageRating.equals(rating)) {
                    return r;
                }
            }
            return Rating.UNKNOWN;
        }
    }
}
