package nirusu.nirubot.util.arknight;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import nirusu.nirubot.Nirubot;

public class RecruitmenTagCalculator {
    private List<Operator> operators;

    public RecruitmenTagCalculator() {
        operators = RecruitmenTagCalculator.loadOperators();
    }

    public static List<Operator> loadOperators() {
        File opList = new File(System.getProperty("user.dir").concat(File.separator + "operators.json"));
        ArrayList<Operator> list = null;
        try {
            list = Nirubot.getGson().fromJson(Files.readString(opList.toPath(), StandardCharsets.UTF_8), new TypeToken<List<Operator>>(){}.getType());
        } catch (JsonSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        list.forEach(System.out::println);
        return list;
    }

    public static void main(String[] args)  {
        loadOperators();
    }

    public static List<TagCombination> calculate(final String[] tags) {
        List<TagCombination> tagCombinations = new ArrayList<>();



        return tagCombinations;
    }
}
