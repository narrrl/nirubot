package nirusu.nirubot.model.tictactoe;

public class Player {
    private static final Player NONE;
    static {
        NONE = new Player(Symbol.NONE);
    }

    private final Symbol symbol;

    public Player(Symbol symbol) {
        this.symbol = symbol;
    }

    public enum Symbol {
        ONE("⭕", -1), TWO("❌", 1), NONE("⬛", 0);
        private final String repr;
        private final int num;

        Symbol(final String repr, final int num) {
            this.repr = repr;
            this.num = num;
        }
    }

    public static Player empty() {
        return Player.NONE;
    }

    public Symbol getSymbol() {
        return this.symbol;
    }

    public int numeric() {
        return this.symbol.num;
    }

    @Override
    public String toString() {
        return this.symbol.repr;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        if (o == this) {
            return true;
        }
        Player p = (Player) o;
        return p.symbol.equals(this.symbol);
    }

    
}
