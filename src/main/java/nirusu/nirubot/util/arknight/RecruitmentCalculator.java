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

public class RecruitmentCalculator {
    private List<Operator> operators;
    private static RecruitmentCalculator calc;

    public static synchronized RecruitmentCalculator getRecruitment() {
        if (calc == null) {
            calc = new RecruitmentCalculator();
        }
        return calc;
    }

    private RecruitmentCalculator() {
        // loads all operator data from a json
        operators = loadOperators();
    }

    public synchronized List<Operator> loadOperators() {
        // file must be in root directory
        File opList = new File(System.getProperty("user.dir").concat(File.separator + "operators.json"));
        ArrayList<Operator> list;

        // gets parsed by Gson
        try {
            list = Nirubot.getGson().fromJson(Files.readString(opList.toPath(), StandardCharsets.UTF_8), new TypeToken<List<Operator>>(){}.getType());
        } catch (JsonSyntaxException | IOException e) {
            throw new IllegalArgumentException("Couldn't read operator list");
        }

        return list;
    }

    /**
     * Calculates all possible combinations with fitting operators.
     * Tags dont have to be formatted right, because they're getting converted anyway.
     * @param userInput the tags as list
     * @param totalInput all tags as one big string
     * @return a sorted list from worst to best of {@link nirusu.nirubot.util.arknight.TagCombination}
     */
    public List<TagCombination> calculate(@Nonnull final List<String> userInput, final String totalInput) {
        // convert the tags first because users input might be wrong
        // everything gets converted to upper case and spaced are swapped with underscore
        List<String> tags = Operator.convertTags(userInput, totalInput);

        // create set to prevent duplicates
        HashSet<TagCombination> tagCombinations = getCombinations(tags);


        // add operator who have the tags
        for (TagCombination cb : tagCombinations) {
            for (Operator o : operators) {
                if (cb.accepts(o)) {
                    cb.addOperator(o);
                }
            }
        }

        // remove tags without operators
        List<TagCombination> toRemove = new ArrayList<>();
        for (TagCombination cb : tagCombinations) {
            if (!cb.hasOperator()) {
                toRemove.add(cb);
            }
        }
        toRemove.forEach(tagCombinations::remove);

        // sort list from worst to best and return
        return tagCombinations.stream().sorted(Comparator.comparingDouble(TagCombination::getAvgRarity)).collect(Collectors.toList());
    }

    public static List<String> formatForDiscord(List<TagCombination> tags) {
        List<String> outList = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (TagCombination cb : tags) {
            for (String str : cb.toStringAsList()) {
                if (builder.length() + str.length() > 1800) {
                    outList.add(builder.toString());
                    builder = new StringBuilder();
                }
                builder.append(str);
            }
        }

        // send rest of the string
        if (builder.length() > 0) {
            outList.add(builder.toString());
        }
        return outList;
    }

    private HashSet<TagCombination> getCombinations(List<String> tags) {
        HashSet<TagCombination> tagCombinations = new HashSet<>();
        for (String tag : tags) {
            for (String tag2 : tags) {
                for (String tag3 : tags) {
                    if (!tag.equals(tag2) && !tag.equals(tag3) && !tag2.equals(tag3)) {
                        tagCombinations.add(new TagCombination(Arrays.asList(tag, tag2, tag3)));
                    }
                    if (!tag.equals(tag2)) {
                        tagCombinations.add(new TagCombination(Arrays.asList(tag, tag2)));
                    }
                    tagCombinations.add(new TagCombination(Arrays.asList(tag)));
                }
            }
        }
        return tagCombinations;
    }


}
