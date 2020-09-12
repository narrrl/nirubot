package nirusu.nirubot.util.arknight;

import java.util.ArrayList;
import java.util.List;

public class Operator implements Comparable<Operator> {
    public enum Qualification {
        STARTER,SENIOR_OPERATOR,TOP_OPERATOR, NONE
    }
    public enum Position {
        MELEE,RANGED
    }
    public enum OperatorClass {
        GUARD,MEDIC,VANGUARD,CASTER,SNIPER,DEFENDER,SUPPORTER,SPECIALIST
    }
    public enum Affix {
        HEALING,SUPPORT,DPS,AOE,SLOW,SURVIVAL,DEFENSE,DEBUFF,SHIFT,CROWD_CONTROL,NUKER,SUMMON,FAST_REDEPLOY,DP_RECOVERY,ROBOT
    }

    private int rarity;

    private Qualification qualification;
    
    private Position position;

    private OperatorClass operatorClass;

    private Affix[] affixes;

    private String name;

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int compareTo(Operator o) {
        return this.hashCode() - o.hashCode();
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

	public boolean hasTag(final String tag) {
        String tmp = tag.toLowerCase();
        if (qualification.name().toLowerCase().equals(tmp)) {
            return true;
        } else if (position.name().toLowerCase().equals(tmp)) {
            return true;
        } else if (operatorClass.name().toLowerCase().equals(tmp)) {
            return true;
        } else {
            for (Affix a : affixes) {
                if (a.name().toLowerCase().equals(tmp)) {
                    return true;
                }
            }
            return false;
        }
	}

	public int getRarity() {
		return this.rarity;
    }

    static List<String> convertTags(final List<String> oldTags) {
        List<String> newTags = new ArrayList<>();
        for (String str : oldTags) {
            newTags.add(str.replace("-", "_").toUpperCase());
        }
        if (newTags.contains("CROWD") && newTags.contains("CONTROL")) {
            newTags.remove("CROWD");
            newTags.remove("CONTROL");
            newTags.add("CROWD_CONTROL");
        }
        return newTags;
    }
    

    public static String getAllTagsAsString() {
        StringBuilder out = new StringBuilder();
        for (Qualification q : Qualification.values()) {
            if (!q.equals(Qualification.NONE)) {
                out.append(q.name()).append(" ");
            }
        }
        for (Position pos : Position.values()) {
            out.append(pos.name()).append(" ");
        }
        for (OperatorClass cl : OperatorClass.values()) {
            out.append(cl.name()).append(" ");
        }
        for (Affix a : Affix.values()) {
            out.append(a.name()).append(" ");
        }
        return out.substring(0, out.length() - 1);
    }


}
