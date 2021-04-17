package nirusu.nirubot.model.arknight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import nirusu.nirubot.Nirubot;

/**
 * This class represents a operator in arknights.
 */
public class Operator implements Comparable<Operator> {
    private String[] tags;
    private boolean hidden;
    private boolean globalHidden = false;
    private int level;
    private Tag[] operatorTags;
    private String type;

    @SerializedName("name_cn")
    private String nameCn;

    @SerializedName("name_en")
    private String nameEn;

    @SerializedName("name_jp")
    private String nameJp;

    @SerializedName("name_kr")
    private String nameKr;

    public String toString(Language lang) {
        String name = switch (lang) {
        case EN -> nameEn;
        case CN -> nameCn;
        case KR -> nameKr;
        case JP -> nameJp;
        default -> nameEn;
        };
        return "[" + name + "](" + "https://aceship.github.io/AN-EN-Tags/akhrchars.html?opname="
                + name.replace(" ", "_") + ") " + this.level + "☆";
    }

    @Override
    public int compareTo(Operator o) {
        return this.hashCode() - o.hashCode();
    }

    @Override
    public int hashCode() {
        return nameEn.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Operator)) {
            return false;
        } else if (o == this) {
            return true;
        }
        return nameEn.equals(((Operator) o).nameEn);
    }

    public boolean hasTag(final Tag tag) {

        for (Tag t : getTags()) {
            if (t.equals(tag))
                return true;
        }

        return false;
    }

    public List<Tag> getTags() {
        if (this.operatorTags == null) {
            this.operatorTags = createTags();
        }

        return Arrays.asList(this.operatorTags);
    }

    private Tag[] createTags() {
        this.operatorTags = new Tag[this.tags.length + 1];
        for (int i = 0; i < this.tags.length; i++) {

            this.operatorTags[i] = Tag.getTagByName(tags[i], Language.CN).orElse(null);

            if (this.operatorTags[i] == null) {
                Nirubot.warning(String.format("Invalid tag %s for operator %s", this.tags[i], this.nameEn));
            }
        }

            this.operatorTags[this.operatorTags.length - 1] = Tag.getTagByName(this.type, Language.CN).orElse(null);

            if (this.operatorTags[this.operatorTags.length - 1] == null) {
                Nirubot.warning(String.format("Invalid tag %s for operator %s", this.type, this.nameEn));
            }


        return this.operatorTags;
    }

    public int getRarity() {
        return this.level;
    }

    public boolean isntHidden() {
        return !this.hidden && !this.globalHidden;
    }

    public static List<Tag> convertTags(final List<String> oldTags, Language lang) {

        List<Tag> newTags = new ArrayList<>();

        for (String str : oldTags) {
            Tag t = Tag.getTagByName(str, lang)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid tag " + str));

            newTags.add(t);
        }

        return newTags;
    }
}
