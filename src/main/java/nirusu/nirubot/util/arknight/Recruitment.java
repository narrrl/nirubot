package nirusu.nirubot.util.arknight;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

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
        operators = Recruitment.loadOperators();
    }

    public static List<Operator> loadOperators() {
        File opList = new File(System.getProperty("user.dir").concat(File.separator + "operators.json"));
        ArrayList<Operator> list;
        try {
            list = Nirubot.getGson().fromJson(Files.readString(opList.toPath(), StandardCharsets.UTF_8), new TypeToken<List<Operator>>(){}.getType());
        } catch (JsonSyntaxException | IOException e) {
            throw new IllegalArgumentException("Couldn't read operator list");
        }
        return list;
    }

    public static void main(String[] args)  {
        Recruitment rec = getRecruitment();
        List<TagCombination> com = rec.calculate(new String[] {"GUARD", "SNIPER", "DEFENDER", "VANGUARD", "STARTER"});
    }

    public List<TagCombination> calculate(@Nonnull final String[] tags) {
        List<TagCombination> tagCombinations = new ArrayList<>();
        // Guard sniper defender vanguard starter, guardsniper guarddefender guardvanguard guardstarter, 
        return tagCombinations;
    }
}
