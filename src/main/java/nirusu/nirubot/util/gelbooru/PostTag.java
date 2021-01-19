package nirusu.nirubot.util.gelbooru;

import com.google.gson.annotations.SerializedName;

public class PostTag {
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

    public String getCount() {
        return count;
    }

    public String getCharacter() {
        return character;
    }

    public String getAmbiguous() {
        return ambiguous;
    }
}
