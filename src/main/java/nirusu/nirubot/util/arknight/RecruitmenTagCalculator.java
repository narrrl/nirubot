package nirusu.nirubot.util.arknight;

import java.util.ArrayList;
import java.util.List;

public class RecruitmenTagCalculator {
    private List<Operator> operators;

    private RecruitmenTagCalculator() {
        operators = new ArrayList<>();
        Operator.loadOperators().forEach(operators::add);
    }

    public static List<TagCombination> calculate(final String[] tags) {
        List<TagCombination> tagCombinations = new ArrayList<>();



        return tagCombinations;
    }
    
}
