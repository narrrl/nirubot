package nirusu.nirubot.util.gelbooru;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

public class PostTag implements Comparable<PostTag> {
    private String id;
    @SerializedName("tag")
    private String tagName;
    private String count;
    private String character;
    private String ambiguous;

    public String getId() {
        return id;
    }

    public String getTagName() {
        return tagName;
    }

    public int getCount() {
        try {
            return Integer.parseInt(count);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public String getCharacter() {
        return character;
    }

    public String getAmbiguous() {
        return ambiguous;
    }

    @Override
    public String toString() {
        return String.format("**Name:** %s%n**Count:** %s", tagName, count);
    }

    @Override
    public int hashCode() {
        return tagName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        return this.tagName.equals(((PostTag) obj).tagName);
    }

    @Override
    public int compareTo(@NotNull PostTag postTag) {
        return this.hashCode() - postTag.hashCode();
    }
}
