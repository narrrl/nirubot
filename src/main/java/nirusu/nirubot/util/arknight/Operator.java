package nirusu.nirubot.util.arknight;

public class Operator {
    public enum Qualification {
        STARTER,SENIOR_OPERATOR,TOP_OPERATOR
    }
    public enum Position {
        MELEE,RANGED
    }
    public enum OperatorClass {
        GUARD,MEDIC,VANGUARD,CASTER,SNIPER,DEFENDER,SUPPORTER,SPECIALIST
    }
    public enum Affix {
        HEALING,SUPPORT,DPS,AOE,SLOW,SURVIVAL,DEFENSE,DEBUFF,SHIFT,CROW_CONTROL,NUKER,SUMMON,FAST_REDEPLOY,DP_RECOVERY,ROBOT
    }

    private int rarity;

    private Qualification qualification;
    
    private Position pos;

    private OperatorClass cl;

    private Affix[] affixes;

    private String name;

	public static Iterable<Operator> loadOperators() {
		return null;
	}

}
