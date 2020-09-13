package nirusu.nirubot.util.arknight;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import nirusu.nirubot.Nirubot;

public class Recruitment {
    private List<Operator> operators;
    private static Recruitment calc;

    public static synchronized Recruitment getRecruitment() {
        if (calc == null) {
            calc = new Recruitment();
        }
        return calc;
    }

    private Recruitment() {
        operators = loadOperators();
    }

    public List<Operator> loadOperators() {
        File opList = new File(System.getProperty("user.dir").concat(File.separator + "operators.json"));
        ArrayList<Operator> list;
        try {
            list = Nirubot.getGson().fromJson(Files.readString(opList.toPath(), StandardCharsets.UTF_8), new TypeToken<List<Operator>>(){}.getType());
        } catch (JsonSyntaxException | IOException e) {
            throw new IllegalArgumentException("Couldn't read operator list");
        }
        return list;
    }

    public List<TagCombination> calculate(@Nonnull final List<String> userInput) {
        List<String> tags = Operator.convertTags(userInput);
        HashSet<TagCombination> tagCombinations = new HashSet<>();
        for (String tag : tags) {
            tagCombinations.add(new TagCombination(Arrays.asList(new String[] {tag})));
        }

        for (String tag : tags) {
            for (String tag2 : tags) {
                if (!tag.equals(tag2)) {
                    tagCombinations.add(new TagCombination(Arrays.asList(new String[] {tag, tag2})));
                }
            }
        }

        for (String tag : tags) {
            for (String tag2 : tags) {
                for (String tag3 : tags) {
                    if (!tag.equals(tag2) && !tag.equals(tag3) && !tag2.equals(tag3)) {
                        tagCombinations.add(new TagCombination(Arrays.asList(new String[] {tag, tag2, tag3})));
                    }
                }
            }
        }

        for (TagCombination cb : tagCombinations) {
            for (Operator o : operators) {
                if (cb.accepts(o)) {
                    cb.addOperator(o);
                }
            }
        }

        List<TagCombination> toRemove = new ArrayList<>();
        for (TagCombination cb : tagCombinations) {
            if (!cb.hasOperator()) {
                toRemove.add(cb);
            }
        }
        toRemove.forEach(cb -> tagCombinations.remove(cb));

        return tagCombinations.stream().sorted(Comparator.comparingInt(TagCombination::getLowestRarity)).collect(Collectors.toList());

    }
}
