package nirusu.nirubot.model.arknight;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import nirusu.nirubot.Nirubot;

public class Role extends Tag {

    public static List<Role> getRoles() {
        InputStream in = Tag.class.getResourceAsStream("role.json");
        StringBuilder tagList = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                tagList.append(line);
            }

        } catch (IOException e) {
            Nirubot.error("Couldn't get role.json for the recruitment tag calculator", e);
        }
        try {
            return Nirubot.getGson().fromJson(tagList.toString(),
                    new TypeToken<List<Role>>() {
                    }.getType());
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Couldn't read roles list");
        }

    }

    @SerializedName("type_en")
    private String typeEn;
    @SerializedName("type_cn")
    private String typeCn;
    @SerializedName("type_jp")
    private String typeJp;
    @SerializedName("type_kr")
    private String typeKr;

    @Override
    public Type getType() {
        return Type.ROLE;
    }

    @Override
    public String getTag(Language lang) {
        return switch(lang) {
            case EN -> typeEn;
            case CN -> typeCn;
            case KR -> typeKr;
            case JP -> typeJp;
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
}
