package nirusu.nirubot.model.arknight;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import nirusu.nirubot.Nirubot;

public class Tag {
    private static List<Tag> tags;

    private static List<Tag> createTags() {
        List<Tag> tags = new ArrayList<>();
        InputStream in = Tag.class.getResourceAsStream("tags.json");
        StringBuilder tagList = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                tagList.append(line);
            }

        } catch (IOException e) {
            Nirubot.error("Couldn't get Operator.json for the recruitment tag calculator", e);
        }
        try {
            tags = Nirubot.getGson().fromJson(tagList.toString(),
                    new TypeToken<List<Tag>>() {
                    }.getType());
            tags.addAll(Role.getRoles());
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Couldn't read tags list");
        }
        return tags;
    }

    public static List<Tag> getAllTags() {

        if (tags == null) {
            tags = createTags();
        }
        return Collections.unmodifiableList(tags);
    }

    public static Optional<Tag> getTagByName(final String tag, final Language lang) {
        return getAllTags().stream().filter(t -> t.getTag(lang).equalsIgnoreCase(tag)).findFirst();
    }

    private String type;
    @SerializedName("tag_en")
    private String tagEn;
    @SerializedName("tag_cn")
    private String tagCn;
    @SerializedName("tag_jp")
    private String tagJp;
    @SerializedName("tag_kr")
    private String tagKr;

    public Type getType() {
        return Type.valueOf(this.type);
    }

    public String getTag(Language lang) {
        return switch(lang) {
            case EN -> tagEn;
            case CN -> tagCn;
            case KR -> tagKr;
            case JP -> tagJp;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Tag) {
            if (this == o) return true;

            return this.getTag(Language.CN).equals(((Tag) o).getTag(Language.CN));
        }
        return false;
    }

    public enum Type {
        QUALIFICATIONS, POSITION, AFFIX, ROLE
    }
}
