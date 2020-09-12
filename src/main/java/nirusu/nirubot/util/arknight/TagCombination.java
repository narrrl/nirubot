package nirusu.nirubot.util.arknight;

import nirusu.nirubot.util.arknight.Operator;
import net.dv8tion.jda.api.EmbedBuilder;

public class TagCombination {
    private Operator[] possibleOperator;
    private String[] tags;

    public EmbedBuilder getTagCombination() {
        // TODO implement
        return null;
    }

    public TagCombination(final Operator[] po, final String[] tags) {
        this.tags = tags;
        this.possibleOperator = po;
    }
    
}
