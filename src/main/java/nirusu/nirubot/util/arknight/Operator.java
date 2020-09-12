package nirusu.nirubot.util.arknight;

public class Operator {
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
        String affix = "";
        for (Affix str : affixes) {
            affix += str.name() + " ";
        }
        return this.name + " " + this.rarity + " " + this.position + " " + this.operatorClass + " " + affix.substring(0, affix.length() - 1) + " " + qualification;
    }
}
