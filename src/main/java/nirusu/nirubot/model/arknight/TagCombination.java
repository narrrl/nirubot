package nirusu.nirubot.model.arknight;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

public class TagCombination implements Comparable<TagCombination> {
    private final Set<Operator> possibleOperator;
    private final List<String> tags;

    /**
     * Case sensitive convert @param tags to upper case first
     */
    public TagCombination(final List<String> tags) {
        this.tags = new ArrayList<>();
        this.tags.addAll(tags);
        this.possibleOperator = new HashSet<>();
    }

    public void addOperator(@Nonnull final Operator op) {
        possibleOperator.add(op);
    }

    @Override
    public int compareTo(@NotNull TagCombination o) {

        return this.hashCode() - o.hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (String str : tags) {
            hash += str.hashCode();
        }
        return hash;
    }

    /**
     * Checks if a operator is a possibility for given tag combination
     * 
     * @param o the operator
     * @return true if operator has the right tags
     */
    public boolean accepts(Operator o) {
        for (String tag : tags) {
            if (!o.hasTag(tag)) {
                return false;
            }
        }

        // you cant get an 6 star without TOP_OPERATOR tag
        return o.getRarity() != 6 || tags.contains("TOP_OPERATOR");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        TagCombination cb = (TagCombination) o;
        return this.hashCode() - cb.hashCode() == 0;
    }

    public boolean hasOperator() {
        return !possibleOperator.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        List<Operator> ops = possibleOperator.stream().sorted(Comparator.comparingInt(Operator::getRarity))
                .collect(Collectors.toList());
        out.append("**");
        tags.forEach(str -> out.append(str).append(","));
        out.replace(out.length() - 1, out.length(), "");
        out.append("**\n");
        ops.forEach(op -> out.append(op).append(" "));
        return out.substring(0, out.length() - 1);
    }

    // when the embed is bigger than 2048 because of hyperlinks
    public List<String> toStringAsList() {
        List<String> strings = new ArrayList<>();
        List<Operator> ops = possibleOperator.stream().sorted(Comparator.comparingInt(Operator::getRarity))
                .collect(Collectors.toList());
        StringBuilder str = new StringBuilder();
        str.append("\n**");
        tags.forEach(tagsStr -> str.append(tagsStr).append(","));
        str.replace(str.length() - 1, str.length(), "");
        str.append("**\n");
        strings.add(str.toString());
        ops.forEach(op -> strings.add(op.toString()));
        return strings;
    }

    public int getLowestRarity() {
        if (possibleOperator.isEmpty()) {
            return 0;
        }

        int lowest = 7;

        for (Operator o : possibleOperator) {
            if (o.getRarity() < lowest) {
                lowest = o.getRarity();
            }
        }
        return lowest;
    }

    public int getHighestRarity() {
        int highest = 0;

        for (Operator o : possibleOperator) {
            if (o.getRarity() > highest) {
                highest = o.getRarity();
            }
        }
        return highest;
    }

    public float getAvgRarity() {

        if (possibleOperator.isEmpty()) {
            return 0;
        }

        int lowest = getLowestRarity();
        int highest = getHighestRarity();

        return lowest + ((float) highest / 10);

    }

}
