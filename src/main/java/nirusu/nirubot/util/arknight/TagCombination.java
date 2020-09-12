package nirusu.nirubot.util.arknight;

public class TagCombination {
    private Operator[] possibleOperator;
    private String[] tags;

    public TagCombination(final Operator[] po, final String[] tags) {
        this.tags = tags;
        this.possibleOperator = po;
    }
    
}
