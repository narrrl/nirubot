package nirusu.nirubot.util.tictactoe;

public class TTTBoard {
    private static final int BOARD_SIZE = 3;
    private int[][] board;

    public TTTBoard() {
        this.board = new int[BOARD_SIZE][BOARD_SIZE];
    }

    public int[][] getBoard() {
        return board;
    }

    public boolean put(final int x, final int y, int player) {

        //validate coordinates
        if (!(x > -1 && y > -1 && x < BOARD_SIZE && y < BOARD_SIZE)) return false;
        if (this.board[y][x] != 0) return false;

        //put the number into the board
        this.board[y][x] = player;
        return true;
    }
}
