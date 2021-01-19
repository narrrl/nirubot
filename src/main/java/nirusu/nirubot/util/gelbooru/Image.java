package nirusu.nirubot.util.gelbooru;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

public class Image {
    private String source;
    private String tags;
    @SerializedName("file_url")
    private String url;
    private String rating;

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
