package nirusu.nirubot.util.tictactoe;

public class TTTBoard {

    public static final int PLAYER_ONE = 1;
    public static final int PLAYER_TWO = -PLAYER_ONE;
    public static final int NOPLAYER = 0;
    public static final int BOARD_SIZE = 3;


    private int[][] board;
    private int fillLevel;

    public TTTBoard() {
        this.board = new int[BOARD_SIZE][BOARD_SIZE];
        this.fillLevel = 0;
    }

    public int[][] getArray() {
        return board;
    }

    public int getFillLevel() {
        return fillLevel;
    }

    public boolean put(final int x, final int y, int player) {

        //validate coordinates
        if (!(x > -1 && y > -1 && x < BOARD_SIZE && y < BOARD_SIZE)) return false;
        if (this.board[y][x] != 0) return false;

        //put the number into the board
        this.board[y][x] = player;

        //increment fill level
        this.fillLevel++;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (this.getArray()[i][j] == PLAYER_ONE) builder.append("X");
                if (this.getArray()[i][j] == PLAYER_TWO) builder.append("O");
                if (this.getArray()[i][j] == NOPLAYER) builder.append("□");
                builder.append(" ");
            }
            if (i < BOARD_SIZE - 1) builder.append("\n");
        }
        return builder.toString();
    }
}
